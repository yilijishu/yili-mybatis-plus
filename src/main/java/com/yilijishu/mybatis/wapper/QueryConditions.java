package com.yilijishu.mybatis.wapper;


import com.yilijishu.mybatis.constant.Constant;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class QueryConditions<T> {

    @Getter
    @Setter
    private SqlKey sqlKey;

    @Getter
    @Setter
    private T[] data;
    @Getter
    @Setter
    private String column;

    public QueryConditions() {
    }

    public QueryConditions(SqlKey sqlKey, String column, T... data) {
        this.sqlKey = sqlKey;
        this.column = column;
        this.data = data;
    }

    public QueryConditions(SqlKey sqlKey, String column) {
        this.sqlKey = sqlKey;
        this.column = column;
    }


    public QueryConditions<T> sk(SqlKey sqlKey) {
        this.sqlKey = sqlKey;
        return this;
    }

    public QueryConditions<T> add(T... data) {
        this.data = data;
        return this;
    }

    public SqlKey getSqlKey() {
        return sqlKey;
    }


    public String escapeColumn() {
        return Constant.escape(column);
    }

    /**
     * 条件生成.
     * @param param 传递param
     * @return 生成SQL
     */
    public String toSqlString(String param) {
        StringBuffer sbf = new StringBuffer();
        sbf.append(Constant.SPACE);
        sbf.append(escapeColumn());
        sbf.append(Constant.SPACE);
        sbf.append(sqlKey.getSqlSegment());
        sbf.append(Constant.SPACE);
        String value = "#{".concat(param).concat(".data[0]").concat("}");
        switch (sqlKey) {
            case LIKE: {
                sbf.append(Constant.like("ALL", value));
                break;
            }
            case LEFTLIKE:{
                sbf.append(Constant.like("LEFT", value));
                break;
            }
            case RIGHTLIKE: {
                sbf.append(Constant.like("RIGHT", value));
                break;
            }
            case BETWEEN:
            case NOT_BETWEEN: {
                sbf.append(value);
                sbf.append(" AND ");
                sbf.append("#{".concat(param).concat(".data[1]").concat("}"));
                break;
            }
            case IS_NOT_NULL:
            case IS_NULL:
            case DESC:
            case ASC: {
                break;
            }
            case IN:
            case NOT_IN: {
                sbf.append("(");
                sbf.append(value);
//                if (data[0] instanceof List) {
//                    List tmp = (List) data[0];
//                    for (int i = 0; i < tmp.size(); i++) {
//                        sbf.append("#{".concat(param).concat(".data[0][").concat(String.valueOf(i)).concat("]").concat("}"));
//                        if (i + 1 < tmp.size()) {
//                            sbf.append(",");
//                        }
//                    }
//                }
                sbf.append(")");
                break;
            }
            default: {
                sbf.append(value);
                break;
            }

        }
        sbf.append(Constant.SPACE);
        return sbf.toString();
    }

}
