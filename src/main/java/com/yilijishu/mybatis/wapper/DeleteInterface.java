package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.iter.BaseBeanInterface;

public interface DeleteInterface<Entity> {

    String getSql();

    default String toSql(Entity entity) {
        StringBuilder result = new StringBuilder();
        result.append(" DELETE FROM ");
        result.append(getBaseBeanInterface(entity).baseGenTable());
        result.append(getSql());
        return result.toString();
    }

    default BaseBeanInterface getBaseBeanInterface(Entity entity) {
        return (BaseBeanInterface)entity;
    }
}
