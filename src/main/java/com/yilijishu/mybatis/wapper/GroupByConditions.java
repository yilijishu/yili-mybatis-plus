package com.yilijishu.mybatis.wapper;

import com.yilijishu.mybatis.constant.Constant;

public class GroupByConditions<T> extends QueryConditions<T> {

    private String[] columns;

    public GroupByConditions(SqlKey sqlKey, String... columns) {
        this.sqlKey = sqlKey;
        this.columns = columns;
    }


    public String toSqlString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Constant.SPACE);
        stringBuffer.append(sqlKey.getSqlSegment());
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
