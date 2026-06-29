package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.iter.BaseBeanInterface;

public abstract class YiliBaseSql<Entity> {

    public abstract Entity getEntity();

    public abstract String toSql();

    protected BaseBeanInterface getBaseBeanInterface() {
        return (BaseBeanInterface) getEntity();
    }
}
