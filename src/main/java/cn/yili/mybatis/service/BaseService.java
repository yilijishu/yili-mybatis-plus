package cn.yili.mybatis.service;

import cn.yili.mybatis.entity.Page;

import java.util.List;

public interface BaseService<T> {

    Integer insert(T p);

    Integer insertAll(List<T> p);

    Integer update(T p);

    List<T> select(T p, Page page);

    Integer count(T p);

    T get(T p);

    List<T> select(T p);

}
