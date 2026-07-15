package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.constant.Constant;
import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.utils.exceptions.BizException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface UpdateInterface<Entity> {


    String getSql();

    Map<String, Object> getMap();

    default String toSql(Entity entity) {
        StringBuilder result = new StringBuilder();
        result.append(" UPDATE ");
        result.append(getBaseBeanInterface(entity).baseGenTable());
        result.append(" SET ");
        AtomicInteger index = new AtomicInteger(0);
        getMap().forEach((k, v) -> {
            int i = index.getAndIncrement();
            if (i > 0) {
                result.append(" ,");
            }
            if("CUSTOM_SQL".equals(k)) {
                result.append(v);
            } else {
                result.append(Constant.escape(k));
                result.append(" = ");
                result.append(Constant.convertObject(v));
            }
        });
        String w = getSql();
        if (StringUtils.isNotBlank(w)) {
            result.append(w);
        } else {
            throw new BizException("禁止全局修改");
        }
        return result.toString();
    }


    default BaseBeanInterface getBaseBeanInterface(Entity entity) {
        return (BaseBeanInterface) entity;
    }
}
