package xyz.luomu32.mybatis.autoconfigure;

import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import xyz.luomu32.mybatis.plugin.OptimisticLockXMLLanguageDriver;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@ConditionalOnClass({SqlSession.class, SqlSessionFactoryBean.class})
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@Import(MybatisMapperAutoConfiguration.class)
public class MybatisAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    private final MybatisProperties properties;

    private final Interceptor[] interceptors;

    public MybatisAutoConfiguration(MybatisProperties properties,
                                    ObjectProvider<Interceptor[]> interceptors) {
        this.properties = properties;
        this.interceptors = interceptors.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean(SqlSessionFactory.class)
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();

        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(getMapperResource(properties));

        if (properties.getFailFast() != null)
            factoryBean.setFailFast(properties.getFailFast());

        if (properties.getConfigurationLocation() != null)
            factoryBean.setConfigLocation(properties.getConfigurationLocation());

        if (properties.getTypeHandlersPackage() != null)
            factoryBean.setTypeHandlersPackage(properties.getTypeHandlersPackage());

        List<Interceptor> interceptors = new ArrayList<>();
        for (Class interceptorClass : properties.getInterceptors()) {
            if (Interceptor.class.isAssignableFrom(interceptorClass)) {
                Interceptor interceptor = (Interceptor) BeanUtils.instantiate(interceptorClass);
                interceptors.add(interceptor);
            }
        }
        if (this.interceptors != null && this.interceptors.length != 0)
            interceptors.addAll(Arrays.asList(this.interceptors));

        factoryBean.setPlugins(interceptors.toArray(new Interceptor[]{}));

        return factoryBean;
    }


    private Resource[] getMapperResource(MybatisProperties properties) {

        Resource[] resources;
        if (properties.getMapperLocations() == null) {
            try {
                LOGGER.info("not specify mapper file location,try to find under '/mapper' directory");
                resources = new PathMatchingResourcePatternResolver().getResources("/mapper/*.xml");
            } catch (IOException e) {
                resources = new Resource[0];
            }
        } else {
            List<Resource> resources1 = new ArrayList<>();
            PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            for (String location : properties.getMapperLocations()) {
                try {
                    for (Resource resource : resourcePatternResolver.getResources(location)) {
                        resources1.add(resource);
                    }
                } catch (IOException e) {

                }
            }
            resources = new Resource[resources1.size()];
            resources1.toArray(resources);
        }

        if (resources.length == 0)
            LOGGER.info("not found any mybatis mapper file");


        for (Resource resource : resources) {
            try {
                String mapper = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));

                new InputStreamResource(new ByteArrayInputStream("".getBytes()));
            } catch (IOException e) {

            }
        }
        return resources;
    }

}
