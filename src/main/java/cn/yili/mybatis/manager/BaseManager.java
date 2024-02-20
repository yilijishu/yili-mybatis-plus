package cn.yili.mybatis.manager;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.mapper.BaseMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class BaseManager<T> {
    //-----------------------------------------标准产物START-----------------------------------------
    @Autowired
    private BaseMapper<T> mapper;

    public Integer insert(T p) {
        return mapper.insert(p);
    }

    public Integer insertAll(List<T> p) {
        return mapper.insertAll(p);
    }

    public Integer update(T p) {
        return mapper.update(p);
    }

    public List<T> select(T p) {
        return mapper.select(p, null);
    }

    public List<T> select(T p, Page page) {
        page.setCount(count(p));
        if (page.isQuery()) {
            return mapper.select(p, page);
        } else {
            return null;
        }
    }

    public T get(T p) {
        return mapper.get(p);
    }

    public Integer count(T p) {
        return mapper.count(p);
    }


}
