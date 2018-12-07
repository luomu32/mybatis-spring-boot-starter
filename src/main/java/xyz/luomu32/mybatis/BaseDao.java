package xyz.luomu32.mybatis;

import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;

@Mapper
public interface BaseDao<T, ID> {

    int create(T entity);

    int creates(Collection<T> entities);

    void deleteById(ID id);

    void update(T entity);

    T findById(ID id);

}
