package com.yilijishu.mybatis.wapper;

import lombok.Data;

import java.util.List;

@Data
public class OrderByConditions<T> extends QueryConditions<T> {

    private List<QueryConditions> orderByConditionsList;

    public OrderByConditions() {

    }

    public OrderByConditions(SqlKey sqlKey, List<QueryConditions> orderByConditionsList) {
        this.sqlKey = sqlKey;
        this.orderByConditionsList = orderByConditionsList;
    }


    @Override
    public String toSqlString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sqlKey.getSqlSegment());
        orderByConditionsList.forEach(e->{
            stringBuffer.append(e.toSqlString());
        });
        return stringBuffer.toString();
    }
}
