package com.yilijishu.mybatis.wapper;


import com.yilijishu.mybatis.constant.Constant;

import java.util.List;

public class QueryConditions<T> {

    protected SqlKey sqlKey;

    protected T[] data;

    protected String column;

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
     * @return 生成SQL
     */
    public String toSqlString() {
        StringBuffer sbf = new StringBuffer();
        sbf.append(Constant.SPACE);
        sbf.append(escapeColumn());
        sbf.append(Constant.SPACE);
        sbf.append(sqlKey.getSqlSegment());
        sbf.append(Constant.SPACE);
        switch (sqlKey) {
            case IN:
            case NOT_IN: {
                sbf.append("(");
                if (data[0] instanceof List) {
                    List tmp = (List) data[0];
                    for (int i = 0; i < tmp.size(); i++) {
                        sbf.append(Constant.convertObject(tmp.get(i)));
                        if (i + 1 < tmp.size()) {
                            sbf.append(",");
                        }
                    }
                }
                sbf.append(")");
                break;
            }
            case LIKE: {
                sbf.append("%");
                sbf.append(Constant.convertObject(data[0]));
                sbf.append("%");
                break;
            }
            case LEFTLIKE:{
                sbf.append("%");
                sbf.append(Constant.convertObject(data[0]));
                break;
            }
            case RIGHTLIKE: {
                sbf.append(Constant.convertObject(data[0]));
                sbf.append("%");
                break;
            }
            case EQ:
            case NE:
            case GT:
            case GE:
            case LT:
            case LE: {
                sbf.append(Constant.convertObject(data[0]));
                break;
            }
            case BETWEEN:
            case NOT_BETWEEN: {
                sbf.append(Constant.convertObject(data[0]));
                sbf.append(" AND ");
                sbf.append(Constant.convertObject(data[1]));
                break;
            }
            case IS_NOT_NULL:
            case IS_NULL:
            default: {
                break;
            }

        }
        sbf.append(Constant.SPACE);
        return sbf.toString();
    }

}
