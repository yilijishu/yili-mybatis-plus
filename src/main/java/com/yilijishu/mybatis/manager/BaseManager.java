package com.yilijishu.mybatis.manager;

import com.yilijishu.mybatis.entity.Page;
import com.yilijishu.mybatis.entity.PageData;
import com.yilijishu.mybatis.mapper.BaseMapper;
import com.yilijishu.mybatis.wapper.YiliBaseSql;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

public class BaseManager<T, TB extends BaseMapper<T>> {
    //-----------------------------------------标准产物START-----------------------------------------
    @Autowired
    protected TB mapper;

    public Integer insert(T p) {
        return mapper.insert(p);
    }

    public Integer insertAll(List<T> p) {
        return mapper.insertAll(p);
    }

    public Integer update(T p) {
        return mapper.update(p);
    }

    public Integer updateNotIfNull(T p) {
        return mapper.updateNotIfNull(p);
    }

    public List<T> select(T p) {
        return mapper.select(p);
    }

    public PageData<List<T>> selectPage(T p, Page page) {
        page.setCount(count(p));
        if (page.isQuery()) {
            return new PageData<>(mapper.selectPage(p, page), page);
        } else {
            return new PageData<>(null, page);
        }
    }

    public T get(T p) {
        return mapper.get(p);
    }

    public Integer count(T p) {
        return mapper.count(p);
    }

    public List<T> querySql(YiliBaseSql<T> querySql) {
        return mapper.querySql(querySql);
    }

    public List<T> querySqlOfPage(YiliBaseSql<T> querySql, Page page) {
        return mapper.querySqlOfPage(querySql, page);
    }

    public <R> List<R> querySqlResultR(YiliBaseSql<T> querySql) {
        return mapper.querySqlResultR(querySql);
    }

    public <R> R querySqlResultROne(YiliBaseSql<T> querySql) {
        return mapper.querySqlResultROne(querySql);
    }

    public T querySqlOne(YiliBaseSql<T> querySql) {
        return mapper.querySqlOne(querySql);
    }

    public Integer updateSql(YiliBaseSql<T> updateSql) {
        return mapper.updateSql(updateSql);
    }

    public Integer delete(T t) {
        return mapper.delete(t);
    }

    public Integer deleteByIds(T t, Collection<?> ids) {
        return mapper.deleteByIds(t, ids);

    }


    public Integer deleteByVirtualIds(T t, Collection<?> ids) {
        return mapper.deleteByVirtualIds(t, ids);

    }


    public Integer deleteSql(YiliBaseSql<T> deleteSql) {
        return mapper.deleteSql(deleteSql);
    }


}
