package xyz.luomu32.mybatis.test;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import xyz.luomu32.mybatis.plugin.Page;

@Mapper
public interface UserMapper {

    @Select("select id,username from t_user where username=#{username}")
    User findByUsername( String username);
}
