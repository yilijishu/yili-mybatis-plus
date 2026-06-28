package com.yilijishu.mybatis.wapper;

public class GroupByConditions<T> extends QueryConditions<T> {

    private String[] columns;

    public GroupByConditions(SqlKey sqlKey, String... columns) {
        this.sqlKey = sqlKey;
        this.columns = columns;
    }


    public String toSqlString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(sqlKey.getSqlSegment());
        for (int i = 0; i < columns.length; i++) {
            if (i > 0) {
                stringBuffer.append(" ,");
            }
            stringBuffer.append(columns[i]);
        }
        return stringBuffer.toString();
    }
}
