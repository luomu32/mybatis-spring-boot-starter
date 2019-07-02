package xyz.luomu32.mybatis.test;

import xyz.luomu32.mybatis.plugin.Pageable;

public class User extends Pageable {

    private Long id;

    private String username;

    private int version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
