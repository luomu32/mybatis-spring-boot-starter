package xyz.luomu32.mybatis.spring.actuator;

import org.springframework.context.ApplicationEvent;

public class SlowSqlEvent extends ApplicationEvent {

    private String sql;

    private String id;

    private long cost;

    public SlowSqlEvent(Object source, String sql, String id, long cost) {
        super(source);
        this.sql = sql;
        this.id = id;
        this.cost = cost;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }
}
