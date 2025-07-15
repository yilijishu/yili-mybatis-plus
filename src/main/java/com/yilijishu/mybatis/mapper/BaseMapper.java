package com.yilijishu.mybatis.mapper;

import com.yilijishu.mybatis.entity.Page;
import com.yilijishu.mybatis.mapper.provider.BaseSqlProvider;
import com.yilijishu.mybatis.wapper.DeleteSql;
import com.yilijishu.mybatis.wapper.QuerySql;
import com.yilijishu.mybatis.wapper.UpdateSql;
import org.apache.ibatis.annotations.*;

import java.util.Collection;
import java.util.List;

@Mapper
public interface BaseMapper<T> {

    String PARAM_PAGE = "page";
    String PARAM_OBJECT = "p";

    //--------------------------------------------------------- 标准产物.
    default void defaultMethod() {

    }

    @UpdateProvider(method = "create", type =  BaseSqlProvider.class)
    void create(@Param(PARAM_OBJECT) T p);

    @InsertProvider(method = "insert", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    <T> Integer insert(@Param(PARAM_OBJECT) T p);


    @UpdateProvider(method = "update", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer update(@Param(PARAM_OBJECT) T p);

    @UpdateProvider(method = "updateNotIfNull", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer updateNotIfNull(@Param(PARAM_OBJECT) T p);


    @InsertProvider(method = "insertAll", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertAll(@Param("list") Collection<T> p);

    @SelectProvider(method = "select", type = BaseSqlProvider.class)
    List<T> select(@Param(PARAM_OBJECT) T p, @Param(PARAM_PAGE) Page page);

    @SelectProvider(method = "get", type = BaseSqlProvider.class)
    T get(@Param(PARAM_OBJECT) T p);


    @SelectProvider(method = "count", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer count(@Param(PARAM_OBJECT) T p);

    @SelectProvider(method = "querySql", type = BaseSqlProvider.class)
    List<T> querySql(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @SelectProvider(method = "querySqlOfPage", type = BaseSqlProvider.class)
    List<T> querySqlOfPage(@Param(PARAM_OBJECT) QuerySql<T> querySql, @Param(PARAM_PAGE) Page page);

    @SelectProvider(method = "querySql", type = BaseSqlProvider.class)
    <R> R querySqlResultR(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @SelectProvider(method = "querySqlOne", type = BaseSqlProvider.class)
    T querySqlOne(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @SelectProvider(method = "querySqlOne", type = BaseSqlProvider.class)
    <R> R querySqlResultROne(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    @UpdateProvider(method = "updateSql", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer updateSql(UpdateSql<T> updateSql);


    @UpdateProvider(method = "deleteByIds", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteByIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids);

    @UpdateProvider(method = "deleteById", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteById(@Param(PARAM_OBJECT) T t, @Param("id") Object id);

    @UpdateProvider(method = "deleteByVirtualIds", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteByVirtualIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids);

    @UpdateProvider(method = "deleteByVirtualId", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteByVirtualId(@Param(PARAM_OBJECT) T t, @Param("id") Object id);

    @UpdateProvider(method = "delete", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer delete(@Param(PARAM_OBJECT) DeleteSql<T> deleteSql);

}
