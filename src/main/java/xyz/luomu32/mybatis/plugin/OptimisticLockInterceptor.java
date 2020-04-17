package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

//TODO 放弃使用插件实现。从Configuration获取到SQL后，没有简易的办法修改。直接从启动时修改后再注册到Configuration中，这样每次执行的时候不需要再拦截一次，性能还更好一点

/**
 * add version condition to any insert,update,delete sql
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        }
)
@Deprecated
public class OptimisticLockInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //update execution sql,add version
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];

        final SqlSource sqlSource = ms.getSqlSource();
        final Configuration configuration = ms.getConfiguration();

        MappedStatement newMs = new MappedStatement.Builder(ms.getConfiguration(),
                ms.getId(),
                new SqlSource() {
                    @Override
                    public BoundSql getBoundSql(Object parameterObject) {
                        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);

                        int pos = boundSql.getSql().indexOf("where");
                        if (pos == -1) {
                            return boundSql;
                        } else {
                            String[] sqls = boundSql.getSql().split("where");
                            String sql = sqls[1] + ",version=version+1" + " where " + sqls[2] + " and version=?";

                            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                            parameterMappings.add(new ParameterMapping.Builder(configuration, "version", int.class).build());

                            BoundSql newBoundSql = new BoundSql(configuration, sql, parameterMappings, boundSql.getParameterObject());
                            return newBoundSql;
                        }
                    }
                },
                ms.getSqlCommandType())
                .resource(ms.getResource())
                .fetchSize(ms.getFetchSize())
                .timeout(ms.getTimeout())
                .statementType(ms.getStatementType())
                .keyGenerator(ms.getKeyGenerator())
//                .keyProperty(ms.getKeyProperties())
//                .keyColumn(ms.getKeyColumns())
                .databaseId(ms.getDatabaseId())
                .lang(ms.getLang())
                .resultOrdered(ms.isResultOrdered())
//                .resultSets(ms.getResultSets())
                .resultMaps(ms.getResultMaps())
                .resultSetType(ms.getResultSetType())
                .flushCacheRequired(ms.isFlushCacheRequired())
                .useCache(ms.isUseCache())
                .cache(ms.getCache())
                .build();

        

        return invocation.getMethod().invoke(invocation.getTarget(), new Object[]{newMs, invocation.getArgs()[1]});
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
