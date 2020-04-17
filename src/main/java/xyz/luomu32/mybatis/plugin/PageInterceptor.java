package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

/**
 * 分页插件
 * Dao的分页方法，分页参数不好处理，pageSize，pageNo
 * 分页方法返回需要定制参数，以包含更多的数据，比如总页数，总记录数等
 * 不同的数据库分页语句不同，无法在插件中硬编码，
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        }

)
@Deprecated
public class PageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = invocation.getArgs()[1];
        RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
        ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];

        if ((rowBounds.getLimit() == Integer.MAX_VALUE) && parameter instanceof Pageable) {
            Pageable pageable = (Pageable) parameter;

            BoundSql boundSql = mappedStatement.getSqlSource().getBoundSql(parameter);
            String[] sqlParts = boundSql.getSql().split(" ");

            String condition = find(sqlParts, "where");
            String tableName = find(sqlParts, "from");

            Executor executor = (Executor) invocation.getTarget();
            ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameter, boundSql);
            Connection connection = executor.getTransaction().getConnection();
            String sql;
            if (condition != null) {
//                String condition = sql.split("where")[1];
                sql = "select count(*) from " + tableName + " where " + condition;
            } else {
                sql = "select count(*) from " + tableName;
            }
            long total = this.count(sql, connection, parameterHandler);
            System.out.println(total);
            rowBounds = new RowBounds((pageable.getPageNo() - 1) * pageable.getPageSize(), pageable.getPageSize());
            return invocation.getMethod().invoke(invocation.getTarget(), mappedStatement, parameter, rowBounds, resultHandler);
//
        }


        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private String find(String[] sql, String type) {
        int pos = 0;
        for (String s : sql) {
            if (s.equalsIgnoreCase(type)) {
                return sql[pos + 1].replace('\n', ' ');
            }
            pos++;
        }
        return null;
    }

    private long count(String sql, Connection connection, ParameterHandler parameterHandler) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        parameterHandler.setParameters(preparedStatement);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong(1);
        } else {
            throw new RuntimeException("");
        }
    }
}
