package xyz.luomu32.mybatis.spring.actuator;

import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.rich.RichGaugeRepository;
import org.springframework.context.event.EventListener;

public class MyBatisMetricsRecoder {


    private final RichGaugeRepository richGaugeRepository;

    public MyBatisMetricsRecoder(RichGaugeRepository richGaugeRepository) {
        this.richGaugeRepository = richGaugeRepository;
    }

    @EventListener(SlowSqlEvent.class)
    public void onApplicationEvent(SlowSqlEvent event) {

        richGaugeRepository.set(new Metric<Number>(event.getId(), event.getCost()));
    }
}
