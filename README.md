# mybatis-spring-boot-starter
![Maven Central](https://img.shields.io/maven-central/v/xyz.luomu32/mybatis-spring-boot-starter.svg)
## How to use it

1. first add dependency to your project

```xml
<dependency>
   <groupId>xyz.luomu32</groupId>
   <artifactId>mybatis-spring-boot-starter</artifactId>
   <version>LATEST</version>
</dependency>
```

2. write xml mapper file and add to ‘mapper’ directory under `src/main/resources`
3. write java data access class and with @Mapper annotation
4. enjoy it

## Configuration

### XML mapper file

by the default,the starter will try to load xml file at 'mapper' directory under `src/main/resource`.you can also change it with application.properties or application.yml.like this:

```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
```

or

```yaml
mybatis:
  mapper-locations: 
  - classpath:mapper/*.xml
  - classpath:User.xml
```

### Java Mapper

by the default,the starter will try to scan Java Mapper with @Mapper annotation under your base project package.you can also dem the package,and the @Mapper annotation will not necessary.

```yaml
mybatis:
  mapper-base-package: com.xxx.xxx.dao
```

### Plugin

```yaml
mybatis:
  interceptors: xyz.luomu32.mybatis.plugin.OptimisticLockInterceptor
```

or just add to Spring container with @Compent or @Bean.the starter will get bean which implement `org.apache.ibatis.plugin.Interceptor` from Spring container,and register it to Mybatis.

