package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.wapper.fun.SFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class LambdaQuerySql<Entity> extends LambdaYiliSql<Entity, LambdaQuerySql<Entity>>
        implements QueryInterface<Entity> {
    private List<String> select;

    public LambdaQuerySql(Supplier<Entity> suppliery) {
        super(suppliery.get());
        select = new ArrayList<>();
    }
    public LambdaQuerySql(Entity t) {
        super(t);
        select = new ArrayList<>();
    }


    public LambdaQuerySql<Entity> select(SFunction<Entity, ?>... column) {
        if (column != null && column.length > 0) {
            select.addAll(Arrays.asList(columnToString(column)));
        }
        return this;
    }

    public LambdaQuerySql<Entity> select(String... column) {
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
