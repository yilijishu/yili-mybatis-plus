package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.constant.Constant;

public class GroupByConditions<T> extends QueryConditions<T> {

    private String[] columns;

    public GroupByConditions(SqlKey sqlKey, String... columns) {
        this.setSqlKey(sqlKey);
        this.columns = columns;
    }


    public String toSqlString(String param) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Constant.SPACE);
        stringBuffer.append(getSqlKey().getSqlSegment());
        stringBuffer.append(Constant.SPACE);
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                stringBuffer.append(" ,");
            }
            stringBuffer.append(columns[i]);
        }
        return stringBuffer.toString();
    }
}
