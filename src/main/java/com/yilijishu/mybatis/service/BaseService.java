package com.yilijishu.mybatis.service;

import com.yilijishu.mybatis.entity.Page;

import java.util.List;

public interface BaseService {
    /**
     * 插入数据，传递实体类
     * @param p 实体类
     * @return 返回插入条数
     */
    Integer insert(Object p);
    /**
     * 批量插入数据，传递实体类
     * @param p 实体类
     * @return 返回插入条数
     */
    Integer insertAll(List p);
    /**
     * 修改数据，传递实体类
     * @param p 实体类
     * @return 返回插入条数
     */
    Integer update(Object p);
    /**
     * 修改数据（数据不为空时修改），传递实体类
     * @param p 实体类
     * @return 返回插入条数
     */
    Integer updateIfNot(Object p);
    /**
     * 批量删除数据，传递id列表
     * @param p 实体
     * @return 删除条数
     */
    Integer delete(Object p);

    /**
     * 批量删除数据【通过虚拟ID删除】，传递虚拟id列表
     * @param p 实体类
     * @param ids ID集合
     * @return 返回删除条数
     */
    Integer deleteByIds(Object p, List ids);

    /**
     * 批量删除数据【通过虚拟ID删除】，传递虚拟id列表
     * @param p 实体类
     * @param ids ID集合
     * @return 返回删除条数
     */
    Integer deleteByVirtualIds(Object p, List ids);
    /**
     * 逻辑删除数据【通过ID逻辑删除】，传递ID列表
     * @param p 实体类
     * @param ids ID集合
     * @return 返回删除条数
     */
    Integer virtualDeleteByIds(Object p, List ids);
    /**
     * 逻辑删除数据【通过虚拟ID逻辑删除】，传递虚拟ID列表
     * @param p 实体类
     * @param ids 虚拟ID集合
     * @return 返回删除条数
     */
    Integer virtualDeleteByVirtualIds(Object p, List ids);
    /**
     * 分页查询数据【遵循标准排序】
     * @param p 实体类
     * @param page 分页类
     * @return 返回集合数据
     */
    List<Object> selectByPage(Object p, Page page);
    /**
     * 查询数据【遵循标准排序】
     * @param p 实体类
     * @return 返回集合数据
     */
    List<Object> select(Object p);
    /**
     * 获取单条数据【遵循标准排序】
     * @param p 实体类
     * @return 返回数据
     */
    Object get(Object p);
}
