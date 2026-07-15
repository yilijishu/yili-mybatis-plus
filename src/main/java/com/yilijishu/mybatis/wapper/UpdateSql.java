package com.yilijishu.mybatis.wapper;


import com.yilijishu.mybatis.iter.BaseBeanInterface;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class UpdateSql<Entity> extends YiliSql<Entity, String, UpdateSql<Entity>>
        implements UpdateInterface<Entity> {

    private Map<String, Object> set;

    public UpdateSql(Entity t) {
        super(t);
        this.set = new HashMap<>();
    }


    public UpdateSql<Entity> set(String column, Object obj) {
        if (StringUtils.isNotBlank(column) && obj != null) {
            set.put(column, obj);
        }
        return this;
    }
    public UpdateSql<Entity> setSql(String sql) {
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
