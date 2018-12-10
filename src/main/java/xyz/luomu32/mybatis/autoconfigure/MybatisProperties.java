package xyz.luomu32.mybatis.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "mybatis")
public class MybatisProperties {


    /**
     * the package of mapper class
     */
    private String mapperBasePackage;

    /**
     * the locations of mapper files
     */
    private String[] mapperLocations;

    /**
     * the location of configuration file
     */
    private Resource configurationLocation;

    private String typeHandlersPackage;

    private Boolean failFast;

    private List<Class<?>> interceptors = new ArrayList<>();


    public String[] getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public String getMapperBasePackage() {
        return mapperBasePackage;
    }

    public void setMapperBasePackage(String mapperBasePackage) {
        this.mapperBasePackage = mapperBasePackage;
    }

    public Boolean getFailFast() {
        return failFast;
    }

    public void setFailFast(Boolean failFast) {
        this.failFast = failFast;
    }


    public Resource getConfigurationLocation() {
        return configurationLocation;
    }

    public void setConfigurationLocation(Resource configurationLocation) {
        this.configurationLocation = configurationLocation;
    }

    public String getTypeHandlersPackage() {
        return typeHandlersPackage;
    }

    public void setTypeHandlersPackage(String typeHandlersPackage) {
        this.typeHandlersPackage = typeHandlersPackage;
    }

    public List<Class<?>> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<Class<?>> interceptors) {
        this.interceptors = interceptors;
    }
}
