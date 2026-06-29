package com.yilijishu.mybatis.wapper;


public class DeleteSql<Entity> extends YiliSql<Entity, String, DeleteSql<Entity>> implements DeleteInterface<Entity> {

    public DeleteSql(Entity entity) {
        super(entity);
    }

    @Override
    public String getSql() {
        return super.toSql();
    }

    public String toSql() {
        return toSql(entity);
    }

}
