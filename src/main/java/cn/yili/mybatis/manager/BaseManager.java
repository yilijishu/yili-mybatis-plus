package cn.yili.mybatis.manager;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.mapper.BaseMapper;

import java.util.List;

public abstract class BaseManager<T> {
    //-----------------------------------------标准产物START-----------------------------------------

    public Integer insert(T p) {
        return getMapper().insert(p);
    }

    public Integer insertAll(List<T> p) {
        return getMapper().insertAll(p);
    }

    public Integer update(T p) {
        return getMapper().update(p);
    }

    public List<T> select(T p) {
        return getMapper().select(p, null);
    }

    public List<T> select(T p, Page page) {
        page.setCount(count(p));
        if (page.isQuery()) {
            return getMapper().select(p, page);
        } else {
            return null;
        }
    }

    public T get(T p) {
        return getMapper().get(p);
    }

    public Integer count(T p) {
        return getMapper().count(p);
    }
    //-----------------------------------------标准产物END-----------------------------------------
    public abstract BaseMapper<T> getMapper();



}
