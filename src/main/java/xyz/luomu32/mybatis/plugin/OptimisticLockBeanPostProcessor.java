package xyz.luomu32.mybatis.plugin;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class OptimisticLockBeanPostProcessor implements BeanPostProcessor {


    private final String SqlSessionFactoryBeanName = SqlSessionFactoryBean.class.getName();

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (beanName.equals(SqlSessionFactoryBeanName)) {
            Configuration configuration = null;
            try {
                configuration = ((SqlSessionFactoryBean) bean).getObject().getConfiguration();
                configuration.setDefaultScriptingLanguage(OptimisticLockXMLLanguageDriver.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
