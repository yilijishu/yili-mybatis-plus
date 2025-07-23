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

    /**
     * 创建表
     * @param p 实例
     */
    @UpdateProvider(method = "create", type =  BaseSqlProvider.class)
    void create(@Param(PARAM_OBJECT) T p);

    /**
     * 插入
     * @param p 实例
     * @return 执行条数
     */
    @InsertProvider(method = "insert", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insert(@Param(PARAM_OBJECT) T p);

    /**
     * 全部修改
     * @param p 实例
     * @return 执行条数
     */
    @UpdateProvider(method = "update", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer update(@Param(PARAM_OBJECT) T p);

    /**
     * 修改非空数据
     * @param p 实例
     * @return 返回执行条数
     */
    @UpdateProvider(method = "updateNotIfNull", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer updateNotIfNull(@Param(PARAM_OBJECT) T p);

    /**
     * 批量插入
     * @param p 列表
     * @return 执行条数
     */
    @InsertProvider(method = "insertAll", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    Integer insertAll(@Param("list") Collection<T> p);

    /**
     * 查询-分页
     * @param p 实例
     * @param page 分页
     * @return 返回集合
     */
    @SelectProvider(method = "select", type = BaseSqlProvider.class)
    List<T> select(@Param(PARAM_OBJECT) T p, @Param(PARAM_PAGE) Page page);

    /**
     * 获取数据
     * @param p 实例
     * @return 返回数据实例
     */
    @SelectProvider(method = "get", type = BaseSqlProvider.class)
    T get(@Param(PARAM_OBJECT) T p);

    /**
     * 查询总数
     * @param p 实例
     * @return 返回数量
     */
    @SelectProvider(method = "count", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer count(@Param(PARAM_OBJECT) T p);

    /**
     * 自定义查询
     * @param querySql 自定义查询实例
     * @return 返回实例集合
     */
    @SelectProvider(method = "querySql", type = BaseSqlProvider.class)
    List<T> querySql(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    /**
     * 自定义查询-分页
     * @param querySql 自定义查询实例
     * @param page 分页类
     * @return 返回实例集合
     */
    @SelectProvider(method = "querySqlOfPage", type = BaseSqlProvider.class)
    List<T> querySqlOfPage(@Param(PARAM_OBJECT) QuerySql<T> querySql, @Param(PARAM_PAGE) Page page);

    /**
     * 查询数据返回指定R
     * @param querySql 自定义查询
     * @param <R> 指定类
     * @return 返回实体
     */
    @SelectProvider(method = "querySql", type = BaseSqlProvider.class)
    <R> R querySqlResultR(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    /**
     * 查询单条数据
     * @param querySql 自定义查询
     * @return 返回数据
     */
    @SelectProvider(method = "querySqlOne", type = BaseSqlProvider.class)
    T querySqlOne(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    /**
     * 查询单挑数据
     * @param querySql 自定义查询
     * @param <R> 指定类
     * @return 返回指定类数据
     */
    @SelectProvider(method = "querySqlOne", type = BaseSqlProvider.class)
    <R> R querySqlResultROne(@Param(PARAM_OBJECT) QuerySql<T> querySql);

    /**
     * 自定义修改
     * @param updateSql 自定义修改类
     * @return 返回执行条数
     */
    @UpdateProvider(method = "updateSql", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer updateSql(UpdateSql<T> updateSql);


    /**
     * 条件删除
     * @param t
     * @return 返回执行条数
     */
    @UpdateProvider(method = "delete", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer delete(@Param(PARAM_OBJECT) T t);

    /**
     * 批量删除。根据ID
     * @param t 实例
     * @param ids id集合
     * @return 执行条数
     */
    @UpdateProvider(method = "deleteByIds", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteByIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids);

    /**
     * 批量删除 - 根据虚拟ID
     * @param t 实例
     * @param ids id集合
     * @return 返回执行条数
     */
    @UpdateProvider(method = "deleteByVirtualIds", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteByVirtualIds(@Param(PARAM_OBJECT) T t, @Param("ids") Collection<?> ids);


    /**
     * 自定义删除
     * @param deleteSql 自定义删除
     * @return 执行条数
     */
    @UpdateProvider(method = "deleteSql", type = BaseSqlProvider.class)
    @ResultType(Integer.class)
    Integer deleteSql(@Param(PARAM_OBJECT) DeleteSql<T> deleteSql);

}
