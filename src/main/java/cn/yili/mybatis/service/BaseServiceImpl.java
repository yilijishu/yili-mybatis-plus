package cn.yili.mybatis.service;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.manager.BaseManager;

import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {

    public abstract BaseManager<T> getBaseManager();

    @Override
    public Integer insert(T p) {
        return getBaseManager().insert(p);
    }

    @Override
    public Integer insertAll(List<T> p) {
        return getBaseManager().insertAll(p);
    }

    @Override
    public Integer update(T p) {
        return getBaseManager().update(p);
    }

    @Override
    public List<T> select(T p, Page page) {
        return getBaseManager().select(p, page);
    }

    @Override
    public Integer count(T p) {
        return getBaseManager().count(p);
    }

    @Override
    public T get(T p) {
        return getBaseManager().get(p);
    }

    @Override
    public List<T> select(T p) {
        return getBaseManager().select(p);
    }
}
