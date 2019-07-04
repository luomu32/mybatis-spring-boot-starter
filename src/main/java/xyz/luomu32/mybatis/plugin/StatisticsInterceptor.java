package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.rich.RichGaugeRepository;

import java.sql.Statement;
import java.util.Properties;

@Intercepts({
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class})
})
public class StatisticsInterceptor implements Interceptor {
    
    private int slowSqlThreshold = 500;

    private final RichGaugeRepository richGaugeRepository;

    private static final ThreadLocal<String> statementId = new ThreadLocal<>();
    private static final ThreadLocal<String> sql = new ThreadLocal<>();

    public StatisticsInterceptor(RichGaugeRepository richGaugeRepository) {
        this.richGaugeRepository = richGaugeRepository;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if (invocation.getTarget() instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Object parameter = invocation.getArgs()[1];
            statementId.set(mappedStatement.getId());
            sql.set(mappedStatement.getBoundSql(parameter).getSql());
            return invocation.proceed();
        } else {
            long start = System.currentTimeMillis();

            Object result = invocation.proceed();

            long end = System.currentTimeMillis();
            long cost = (end - start);
            String id = statementId.get();
            if (cost >= slowSqlThreshold) {
                richGaugeRepository.set(new Metric<Number>(id, cost));
            }
            return result;
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        if (properties.containsKey("slowSqlThreshold")) {
            this.slowSqlThreshold = Integer.parseInt(properties.getProperty("slowSqlThreshold"));
        }
    }
}
