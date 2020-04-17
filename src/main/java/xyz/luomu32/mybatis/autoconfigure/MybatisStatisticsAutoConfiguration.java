package xyz.luomu32.mybatis.autoconfigure;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.actuate.endpoint.RichGaugeReaderPublicMetrics;
import org.springframework.boot.actuate.metrics.rich.InMemoryRichGaugeRepository;
import org.springframework.boot.actuate.metrics.rich.RichGaugeReader;
import org.springframework.boot.actuate.metrics.rich.RichGaugeRepository;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import xyz.luomu32.mybatis.plugin.StatisticsInterceptor;
import xyz.luomu32.mybatis.spring.actuator.MyBatisMetricsRecoder;

import java.util.Properties;

@Configuration
@ConditionalOnClass(Endpoint.class)
@ConditionalOnProperty(value = "mybatis.metrics-slow-sql-enabled", matchIfMissing = true)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
public class MybatisStatisticsAutoConfiguration {

    @Bean
    public Interceptor statisticsInterceptro(
            MybatisProperties mybatisProperties) {

        StatisticsInterceptor interceptor = new StatisticsInterceptor();

        if (null != mybatisProperties.getMetricsSlowSqlThreshold()) {
            Properties properties = new Properties();
            properties.setProperty("slowSqlThreshold", mybatisProperties.getMetricsSlowSqlThreshold().toString());
            interceptor.setProperties(properties);
        }
        return interceptor;
    }

    @Bean
    public MyBatisMetricsRecoder myBatisMetricsRecoder(RichGaugeRepository richGaugeRepository) {
        return new MyBatisMetricsRecoder(richGaugeRepository);
    }

    @Bean
    public InMemoryRichGaugeRepository richGaugeRepository() {
        return new InMemoryRichGaugeRepository();
    }

    @Bean
    public RichGaugeReaderPublicMetrics publicMetrics(@Autowired RichGaugeReader richGaugeReader) {
        RichGaugeReaderPublicMetrics publicMetrics = new RichGaugeReaderPublicMetrics(richGaugeReader);
        return publicMetrics;
    }

}
