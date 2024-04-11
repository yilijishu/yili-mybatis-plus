package cn.yili.mybatis.service;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.wapper.DeleteSql;
import cn.yili.mybatis.wapper.QuerySql;
import cn.yili.mybatis.wapper.UpdateSql;

import java.util.Collection;
import java.util.List;

public interface BaseService<T> {
    Integer insert(T p);

    Integer insertAll(List<T> p);

    Integer update(T p);

    List<T> select(T p, Page page);

    Integer count(T p);

    T get(T p);

    List<T> select(T p);

    List<T> querySql(QuerySql<T> querySql);

    T querySqlOne(QuerySql<T> querySql);

    Integer updateSql(UpdateSql<T> updateSql);

    Integer deleteByIds(T t, Collection<?> ids);

    Integer deleteById(T t, Object id);
    
    Integer delete(DeleteSql<T> deleteSql);
}
