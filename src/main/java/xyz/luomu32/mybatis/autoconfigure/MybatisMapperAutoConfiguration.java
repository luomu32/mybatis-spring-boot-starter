package xyz.luomu32.mybatis.autoconfigure;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import xyz.luomu32.mybatis.BaseDao;

@Configuration
public class MybatisMapperAutoConfiguration implements EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private BeanFactory beanFactory;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer() {

        String basePackage = environment.getProperty("mybatis.mapper-base-package");

        MapperScannerConfigurer configurer = new MapperScannerConfigurer();
        if (basePackage == null) {
            configurer.setAnnotationClass(Mapper.class);
            configurer.setMarkerInterface(BaseDao.class);
            basePackage = StringUtils.arrayToDelimitedString(AutoConfigurationPackages.get(this.beanFactory).toArray(), ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
            configurer.setBasePackage(basePackage);
        } else
            configurer.setBasePackage(basePackage);

        return configurer;
    }
}
