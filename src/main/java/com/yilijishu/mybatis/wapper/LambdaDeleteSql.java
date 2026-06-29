package com.yilijishu.mybatis.wapper;

import java.util.function.Supplier;

public class LambdaDeleteSql<Entity> extends LambdaYiliSql<Entity, LambdaDeleteSql<Entity>>
        implements DeleteInterface<Entity> {

    public LambdaDeleteSql(Supplier<Entity> suppliery) {
        super(suppliery.get());
    }

    public LambdaDeleteSql(Entity t) {
        super(t);
    }


    @Override
    public String getSql() {
        return super.toSql();
    }

    public String toSql() {
        return toSql(entity);
    }

}
