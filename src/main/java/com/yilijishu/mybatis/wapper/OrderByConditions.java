package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.constant.Constant;
import lombok.Data;

import java.util.List;

@Data
public class OrderByConditions<T> extends QueryConditions<T> {

    private List<QueryConditions> orderByConditionsList;

    public OrderByConditions() {

    }

    public OrderByConditions(SqlKey sqlKey, List<QueryConditions> orderByConditionsList) {
        setSqlKey(sqlKey);
        this.orderByConditionsList = orderByConditionsList;
    }


    @Override
    public String toSqlString(String param) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Constant.SPACE);
        stringBuffer.append(getSqlKey().getSqlSegment());
        stringBuffer.append(Constant.SPACE);
        for(int i=0;i<orderByConditionsList.size();i++) {
            QueryConditions e = orderByConditionsList.get(i);
            if(i>0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(e.toSqlString(param));
        }
        return stringBuffer.toString();
    }
}
