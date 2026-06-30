package com.yilijishu.mybatis.constant;

import com.yilijishu.mybatis.ann.SetDataBase;

public final class Constant {

    public static final class OrderByValue {
        public static final String DESC = "DESC";
        public static final String ASC = "ASC";
    }

    public static final String IFNULL_TEMPLATE = "      <if test=\"{CONDITION}\">" +
            "        {WHERE}" +
            "      </if>";

    public enum Oper {
        WHERE, UPDATE
    }

    public static SetDataBase.DataBaseEnum dataBase = null;

    public static final String SQLSERVER_S_START = "[";
    public static final String SQLSERVER_S_END = "]";
    public static final String MYSQL_S = "`";
    public static final String ORACLE_S = "\"";

    public static final String SPACE = " ";

    public static final String SINGLE_QUOTE = "'";

    public static final String NUM_1 = "1";
    public static final String NUM_0 = "0";


    public static final String OFFSET = " OFFSET ";
    public static final String ROWS_FETCH_NEXT = " ROWS FETCH NEXT ";
    public static final String ROWS_ONLY = " ROWS ONLY ";
    public static final String LIMIT = " LIMIT ";

    /**
     * 转义表字段、表名等。
     * @param dbLabel 表字段
     * @return 返回转义后的数据
     */
    public static String escape(String dbLabel) {
        if (dataBase != null) {
            switch (dataBase) {
                case SQLSERVER: {
                    return (SQLSERVER_S_START).concat(dbLabel).concat(SQLSERVER_S_END);
                }
                case POSTGRESQL:
                case ORACLE: {
                    return (ORACLE_S).concat(dbLabel).concat(ORACLE_S);
                }
                case MYSQL:
                case SQLITE:
                default: {
                    return (MYSQL_S).concat(dbLabel).concat(MYSQL_S);
                }
            }
        }
        return (MYSQL_S).concat(dbLabel).concat(MYSQL_S);
    }

    /**
     * 获取database
     * @return 时间戳
     */
    public static String escapeDate() {
        if (dataBase != null) {
            switch (dataBase) {
                case SQLSERVER: {
                    return "GETDATE()";
                }
                case ORACLE: {
                    return "SYSDATE";
                }

                case SQLITE: {
                    return "DATETIME('now')";
                }
                case POSTGRESQL:
                case MYSQL:
                default: {
                    break;
                }
            }
        }
        return "CURRENT_TIMESTAMP";
    }

    /**
     * 处理数据库的分页
     * @param offsetStr 偏移量字段 或者值
     * @param sizeStr  size字段 或者值
     * @return  返回分页SQL
     */
    public static String limitEscape(String offsetStr, String sizeStr) {
        if (dataBase != null) {
            switch (dataBase) {
                case SQLSERVER:
                case ORACLE: {
                    return OFFSET.concat(offsetStr).concat(ROWS_FETCH_NEXT).concat(sizeStr).concat(ROWS_ONLY);
                }
                case MYSQL:
                case POSTGRESQL:
                case SQLITE:
                default: {
                    break;
                }
            }
        }
        return LIMIT.concat(sizeStr).concat(OFFSET).concat(offsetStr);
    }


    /**
     * 对Object数据转换成数据库识别的值
     * @param data  object数据
     * @return 识别的值
     */
    public static String convertObject(Object data) {
        if (data instanceof Integer || data instanceof Long || data instanceof Double || data instanceof Byte || data instanceof Short || data instanceof Float) {
            return data.toString();
        } else if (data instanceof Boolean) {
            Boolean r = (Boolean) data;
            return r != null && r ? NUM_1 : NUM_0;
        } else {
            return SINGLE_QUOTE.concat(data.toString()).concat(SINGLE_QUOTE);
        }
    }
}
