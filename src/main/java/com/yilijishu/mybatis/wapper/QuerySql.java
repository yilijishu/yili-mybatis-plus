package com.yilijishu.mybatis.wapper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuerySql<Entity> extends YiliSql<Entity, String, QuerySql<Entity>>
        implements QueryInterface<Entity> {

    private List<String> select;

    public QuerySql(Entity t) {
        super(t);
        this.orderBy = new ArrayList<>();
        select = new ArrayList<>();
    }

    public QuerySql<Entity> select(String... column) {
        if (column != null && column.length > 0) {
            select.addAll(Arrays.asList(column));
        }
        return this;
    }


    public List<String> getSelect() {
        return this.select;
    }

    @Override
    public String getSql() {
        return super.toSql();
    }

    @Override
    public String toSql() {
        return toSql(entity);
    }
}
