package cn.yili.mybatis.manager;

import cn.yili.mybatis.entity.Page;
import cn.yili.mybatis.mapper.BaseMapper;
import cn.yili.mybatis.wapper.DeleteSql;
import cn.yili.mybatis.wapper.QuerySql;
import cn.yili.mybatis.wapper.UpdateSql;
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

    public List<T> querySql(QuerySql<T> querySql) {
        return mapper.querySql(querySql);
    }

    public <R> R querySqlResultR(QuerySql<T> querySql) {
        return mapper.querySqlResultR(querySql);
    }

    public <R> R querySqlResultROne(QuerySql<T> querySql) {
        return mapper.querySqlResultROne(querySql);
    }

    public T querySqlOne(QuerySql<T> querySql) {
        return mapper.querySqlOne(querySql);
    }

    public Integer updateSql(UpdateSql<T> updateSql) {
        return mapper.updateSql(updateSql);
    }

    public Integer deleteByIds(T t, Collection<?> ids) {
        return mapper.deleteByIds(t, ids);

    }

    public Integer deleteById(T t, Object id) {
        return mapper.deleteById(t, id);
    }


    public Integer delete(DeleteSql<T> deleteSql) {
        return mapper.delete(deleteSql);
    }


}
