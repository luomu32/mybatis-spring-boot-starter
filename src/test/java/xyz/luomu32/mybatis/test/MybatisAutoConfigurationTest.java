package xyz.luomu32.mybatis.test;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(
        classes = TestConfig.class,
        properties = {
                "mybatis.mapper-base-package=xyz.luomu32.mybatis.test",
                "mybatis.mapper-locations=classpath:User.xml"
        })
@SpringBootConfiguration
public class MybatisAutoConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void testApplication() {
        assert applicationContext != null;
    }

    @Test
    public void testSqlSessionFactory() {
        assert applicationContext.getBean(SqlSessionFactory.class) != null;
    }

    @Test
    public void testMapper() {
        UserDao userDao = applicationContext.getBean(UserDao.class);
        assert userDao != null;

        User user = userDao.findByUsername("zhangs");
        assert user != null;
    }
}
