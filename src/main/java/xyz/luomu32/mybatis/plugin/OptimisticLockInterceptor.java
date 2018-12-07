package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

//TODO 放弃使用插件实现。从Configuration获取到SQL后，没有简易的办法修改。直接从启动时修改后再注册到Configuration中，这样每次执行的时候不需要再拦截一次，性能还更好一点
/**
 * add version condition to any insert,update,delete sql
 */
@Intercepts(
        {
                @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
        }
)
@Deprecated
public class OptimisticLockInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(OptimisticLockInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        //update execution sql,add version
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
