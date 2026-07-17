package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.wapper.fun.SFunction;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LambdaUpdateSql<Entity> extends LambdaYiliSql<Entity, LambdaUpdateSql<Entity>>
        implements UpdateInterface<Entity> {

    @Getter
    @Setter
    private Map<String, Object> set;

    public LambdaUpdateSql(Supplier<Entity> suppliery) {
        super(suppliery.get());
        set = new HashMap<>();
    }

    public LambdaUpdateSql(Entity t) {
        super(t);
        set = new HashMap<>();
    }

    public LambdaUpdateSql<Entity> set(SFunction<Entity, ?> column, Object obj) {
        set.put(columnToString(column), obj);
        return this;
    }

    public LambdaUpdateSql<Entity> set(String column, Object obj) {
        set.put(column, obj);
        return this;
    }

    public LambdaUpdateSql<Entity> setSql(String sql) {
        if (StringUtils.isNotBlank(sql)) {
            set.put("CUSTOM_SQL", sql);
        }
        return this;
    }

    @Override
    public String getSql() {
        return super.toSql();
    }

    @Override
    public Map<String, Object> getMap() {
        return this.set;
    }

    public String toSql() {
        return toSql(entity);
    }

}
