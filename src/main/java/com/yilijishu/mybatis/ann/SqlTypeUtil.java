package com.yilijishu.mybatis.ann;

import com.yilijishu.mybatis.constant.Constant;

import java.util.HashMap;
import java.util.Map;

public class SqlTypeUtil {

    public static Map<String, String> SQLTYPE = new HashMap<>();

    static {
        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("int"), "Integer");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("int"), "NUMBER(10)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("int"), "Integer");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("int"), "Integer");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("int"), "Integer");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("long"), "BIGINT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("long"), "NUMBER(20)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("long"), "BIGINT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("long"), "BIGINT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("long"), "Integer");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("boolean"), "TINYINT(1)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("boolean"), "NUMBER(1)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("boolean"), "BOOLEAN");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("boolean"), "BIT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("boolean"), "INTEGER");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("Date"), "DATETIME");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("Date"), "TIMESTAMP(0)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("Date"), "TIMESTAMP");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("Date"), "DATETIME2(0)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("Date"), "TEXT");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("LocalDate"), "DATE");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("LocalDate"), "DATE");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("LocalDate"), "DATE");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("LocalDate"), "DATE");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("LocalDate"), "TEXT");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("BigDecimal"), "DECIMAL(18,2)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("BigDecimal"), "NUMBER(18,2)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("BigDecimal"), "NUMBER(18,2)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("BigDecimal"), "DECIMAL(18,2)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("BigDecimal"), "TEXT");


        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("double"), "DOUBLE");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("double"), "BINARY_DOUBLE");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("double"), "DOUBLE PRECISION");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("double"), "FLOAT(53)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("double"), "REAL");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("float"), "FLOAT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("float"), "BINARY_FLOAT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("float"), "real");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("float"), "FLOAT(24)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("float"), "REAL");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("byte"), "TINYINT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("byte"), "NUMBER(3)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("byte"), "smallint");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("byte"), "TINYINT");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("byte"), "INTEGER");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("byte[]"), "BLOB");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("byte[]"), "BLOB");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("byte[]"), "BYTEA");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("byte[]"), "VARBINARY(MAX)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("byte[]"), "BLOB");

        SQLTYPE.put(SetDataBase.DataBaseEnum.MYSQL.name().concat("String"), "VARCHAR(255)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.ORACLE.name().concat("String"), "VARCHAR2(255)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.POSTGRESQL.name().concat("String"), "VARCHAR(255)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLSERVER.name().concat("String"), "NVARCHAR(255)");
        SQLTYPE.put(SetDataBase.DataBaseEnum.SQLITE.name().concat("String"), "TEXT");

    }


    public static String getTableIdSqlType(boolean isId) {
        if(isId) {
            switch (Constant.dataBase) {
                case SQLSERVER: {
                    return " IDENTITY(1,1) PRIMARY KEY ";
                }
                case POSTGRESQL: {
                    return " SERIAL PRIMARY KEY ";
                }
                case ORACLE: {
                    return " GENERATED ALWAYS AS IDENTITY PRIMARY KEY ";
                }
                default: {
                    return " PRIMARY KEY AUTO_INCREMENT ";
                }
            }
        }
        return "";
    }

    public static String getSqlType(String defTypeColumn) {
        switch (defTypeColumn) {
            case "java.lang.Integer":
            case "int": {
                return SQLTYPE.get(Constant.dataBase.name().concat("int"));
            }
            case "long":
            case "java.lang.Long": {
                return SQLTYPE.get(Constant.dataBase.name().concat("long"));
            }
            case "java.lang.Boolean":
            case "boolean": {
                return SQLTYPE.get(Constant.dataBase.name().concat("boolean"));
            }
            case "java.sql.Date":
            case "java.util.Date":
            case "java.time.LocalDateTime": {
                return SQLTYPE.get(Constant.dataBase.name().concat("Date"));
            }
            case "java.time.LocalDate": {
                return SQLTYPE.get(Constant.dataBase.name().concat("LocalDate"));
            }
            case "java.math.BigDecimal": {
                return SQLTYPE.get(Constant.dataBase.name().concat("BigDecimal"));
            }
            case "java.lang.Double":
            case "double": {
                return SQLTYPE.get(Constant.dataBase.name().concat("double"));
            }
            case "java.lang.Float":
            case "float": {
                return SQLTYPE.get(Constant.dataBase.name().concat("float"));
            }
            case "byte[]":
            case "java.lang.Byte[]": {
                return SQLTYPE.get(Constant.dataBase.name().concat("byte[]"));
            }
            case "byte":
            case "java.lang.Byte": {
                return SQLTYPE.get(Constant.dataBase.name().concat("byte"));
            }
            default: {
                if ((defTypeColumn.contains("List") || defTypeColumn.contains("Map") || !defTypeColumn.contains("java")) && Constant.dataBase == SetDataBase.DataBaseEnum.MYSQL) {
                    return "json";
                }
                return SQLTYPE.get(Constant.dataBase.name().concat("String"));
            }
        }
    }

}
