package xyz.luomu32.mybatis.test;

import org.apache.ibatis.annotations.Mapper;
import xyz.luomu32.mybatis.plugin.Page;

@Mapper
public interface UserDao {

    User findByUsername(String username);

    int update(User user);

    Page<User> findAll(User user);

}
