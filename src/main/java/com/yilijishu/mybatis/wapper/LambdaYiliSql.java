package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.iter.BaseBeanInterface;
import com.yilijishu.mybatis.wapper.fun.SFunction;
import com.yilijishu.mybatis.wapper.util.LambdaReflectUtil;

public class LambdaYiliSql<Entity extends BaseBeanInterface, Children extends LambdaYiliSql<Entity, Children>> extends YiliSql<Entity, SFunction<Entity, ?>, Children> {

    public LambdaYiliSql(Entity entity) {
        super(entity);
    }

    @Override
    public String columnToString(SFunction<Entity, ?> column) {
        return LambdaReflectUtil.getColumnName(column);
    }

    @Override
    public String[] columnToString(SFunction<Entity, ?>... column) {
        String[] result = new String[column.length];
        for(int i=0;i<column.length;i++) {
            result[i] = LambdaReflectUtil.getColumnName(column[i]);
        }
        return result;
    }
}
