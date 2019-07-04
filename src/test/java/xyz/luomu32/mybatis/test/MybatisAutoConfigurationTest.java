package xyz.luomu32.mybatis.test;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.actuate.metrics.rich.InMemoryRichGaugeRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import xyz.luomu32.mybatis.autoconfigure.MybatisAutoConfiguration;
import xyz.luomu32.mybatis.autoconfigure.MybatisMapperAutoConfiguration;

import java.util.List;

public class MybatisAutoConfigurationTest {

    private AnnotationConfigApplicationContext applicationContext;

    @Before
    public void setup() {
        applicationContext = new AnnotationConfigApplicationContext();
    }

    @After
    public void cleanup() {
        applicationContext.close();
    }


    @Test
    public void testAutoConfiguration() {
        applicationContext.register(AutoConfigurationConfig.class);
        applicationContext.refresh();

        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        assert sqlSessionFactory != null;

        UserMapper userDao = applicationContext.getBean(UserMapper.class);
        assert userDao != null;

        User user = userDao.findByUsername("zhangs");
        assert user != null;

        assert sqlSessionFactory.getConfiguration().getMappedStatement("xyz.luomu32.mybatis.test.UserMapper.findByUsername") != null;
    }

    @Test
    public void testAddInterceptorUseConfig() {
        EnvironmentTestUtils.addEnvironment(applicationContext.getEnvironment(),
                "mybatis.mapper-base-package=xyz.luomu32.mybatis.test",
                "mybatis.interceptors=xyz.luomu32.mybatis.test.TestInterceptor");

        applicationContext.register(DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class, MybatisMapperAutoConfiguration.class);
        applicationContext.refresh();

        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        List<Interceptor> interceptors = sqlSessionFactory.getConfiguration().getInterceptors();
        assert interceptors.size() == 1;
        assert interceptors.get(0).getClass().equals(TestInterceptor.class);
    }

    @Test
    public void testAddInterceptorUseBean() {
        applicationContext.register(DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class, InterceptorConfig.class);
        applicationContext.refresh();

        SqlSessionFactory sqlSessionFactory = applicationContext.getBean(SqlSessionFactory.class);
        List<Interceptor> interceptors = sqlSessionFactory.getConfiguration().getInterceptors();
        assert interceptors.size() == 1;
        assert interceptors.get(0).getClass().equals(TestInterceptor.class);
    }

    @Test
    public void testStatistics() {
        EnvironmentTestUtils.addEnvironment(applicationContext.getEnvironment(), "mybatis.metricsSlowSqlThreshold=1");

        applicationContext.register(AutoConfigurationConfig.class);
        applicationContext.refresh();

        InMemoryRichGaugeRepository repository = applicationContext.getBean(InMemoryRichGaugeRepository.class);

        assert repository != null;

        UserMapper userDao = applicationContext.getBean(UserMapper.class);
        userDao.findByUsername("zhangs");

        assert repository.findOne("xyz.luomu32.mybatis.test.UserMapper.findByUsername") != null;

    }

    @EnableAutoConfiguration
    static class AutoConfigurationConfig {

    }

    static class InterceptorConfig {
        @Bean
        public Interceptor interceptor() {
            return new TestInterceptor();
        }
    }
}
