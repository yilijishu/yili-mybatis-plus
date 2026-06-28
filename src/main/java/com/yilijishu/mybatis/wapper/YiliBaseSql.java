package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.iter.BaseBeanInterface;

public abstract class YiliBaseSql<Entity extends BaseBeanInterface> {

    public abstract Entity getEntity();

    public abstract String toSql();
}
