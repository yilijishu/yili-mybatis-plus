package com.yilijishu.mybatis.service.impl;

import com.yilijishu.mybatis.entity.Page;
import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.mybatis.manager.BaseManager;
import com.yilijishu.mybatis.service.BaseService;
import com.yilijishu.mybatis.wapper.UpdateSql;
import com.yilijishu.utils.exceptions.BizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BaseServiceImpl implements BaseService {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 插入数据，传递实体类
     *
     * @param p 实体类
     * @return 返回插入条数
     */
    @Override
    public Integer insert(Object p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.insert(p);
    }

    /**
     * 批量插入数据，传递实体类
     *
     * @param p 实体类
     * @return 返回插入条数
     */
    @Override
    public Integer insertAll(List p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.insertAll(p);
    }


    /**
     * 修改数据，传递实体类
     *
     * @param p 实体类
     * @return 返回插入条数
     */
    @Override
    public Integer update(Object p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.update(p);
    }

    /**
     * 修改数据（数据不为空时修改），传递实体类
     *
     * @param p 实体类
     * @return 返回插入条数
     */
    @Override
    public Integer updateIfNot(Object p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.updateNotIfNull(p);
    }


    /**
     * 删除数据
     *
     * @param p   实体类
     * @return 删除条数
     */
    @Override
    public Integer delete(Object p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.delete(p);

    }

    /**
     * 批量删除数据【通过ID删除】，传递虚拟id列表
     *
     * @param p   实体类
     * @param ids ID集合
     * @return 返回删除条数
     */
    @Override
    public Integer deleteByIds(Object p, List ids) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.deleteByIds(p, ids);
    }

    /**
     * 批量删除数据【通过虚拟ID删除】，传递虚拟id列表
     *
     * @param p   实体类
     * @param ids ID集合
     * @return 返回删除条数
     */
    @Override
    public Integer deleteByVirtualIds(Object p, List ids) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.deleteByVirtualIds(p, ids);
    }

    /**
     * 逻辑删除数据【通过ID逻辑删除】，传递ID列表
     *
     * @param p   实体类
     * @param ids ID集合
     * @return 返回删除条数
     */
    @Override
    public Integer virtualDeleteByIds(Object p, List ids) {
        if (p instanceof BaseBeanInterface) {
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) p;
        boolean delTag = bbi.genDelTag();
        if (delTag) {
            String column = bbi.genDelTagColumn();
            String value = bbi.genDelTagValue();
            String id = bbi.baseGenId();
            BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
            return baseManager.updateSql(new UpdateSql(p).set(column, value).in(id, ids));
        } else {
            return 0;
        }
    }

    /**
     * 逻辑删除数据【通过虚拟ID逻辑删除】，传递虚拟ID列表
     *
     * @param p   实体类
     * @param ids 虚拟ID集合
     * @return 返回删除条数
     */
    @Override
    public Integer virtualDeleteByVirtualIds(Object p, List ids) {
        if (p instanceof BaseBeanInterface) {
            throw new BizException("无效的实体");
        }
        BaseBeanInterface bbi = (BaseBeanInterface) p;
        boolean delTag = bbi.genDelTag();
        if (delTag) {
            String column = bbi.genDelTagColumn();
            String value = bbi.genDelTagValue();
            String virtualId = bbi.baseGenVirtualId();
            BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
            return baseManager.updateSql(new UpdateSql(p).set(column, value).in(virtualId, ids));
        } else {
            return 0;
        }
    }

    /**
     * 分页查询数据【遵循标准排序】
     *
     * @param p    实体类
     * @param page 分页类
     * @return 返回集合数据
     */
    @Override
    public List<Object> selectByPage(Object p, Page page) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        page.setCount(baseManager.count(p));
        if (page.isQuery()) {
            return baseManager.select(p, page);
        }
        return new ArrayList<>();
    }

    /**
     * 查询数据【遵循标准排序】
     *
     * @param p 实体类
     * @return 返回集合数据
     */
    @Override
    public List<Object> select(Object p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.select(p);
    }

    /**
     * 获取单条数据【遵循标准排序】
     *
     * @param p 实体类
     * @return 返回数据
     */
    @Override
    public Object get(Object p) {
        BaseManager baseManager = applicationContext.getBean(p.getClass().getSimpleName() + "Manager", BaseManager.class);
        return baseManager.get(p);
    }
}
