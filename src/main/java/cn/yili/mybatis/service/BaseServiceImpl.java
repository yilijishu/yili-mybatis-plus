package cn.yili.mybatis.service;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.manager.BaseManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseServiceImpl<T> implements BaseService<T> {

    @Autowired
    private BaseManager<T> baseManager;

    @Override
    public Integer insert(T p) {
        return baseManager.insert(p);
    }

    @Override
    public Integer insertAll(List<T> p) {
        return baseManager.insertAll(p);
    }

    @Override
    public Integer update(T p) {
        return baseManager.update(p);
    }

    @Override
    public List<T> select(T p, Page page) {
        return baseManager.select(p, page);
    }

    @Override
    public Integer count(T p) {
        return baseManager.count(p);
    }

    @Override
    public T get(T p) {
        return baseManager.get(p);
    }

    @Override
    public List<T> select(T p) {
        return baseManager.select(p);
    }
}
