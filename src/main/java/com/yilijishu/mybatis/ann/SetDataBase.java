package com.yilijishu.mybatis.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SetDataBase {

    DataBaseEnum value() default DataBaseEnum.MYSQL;
    enum DataBaseEnum{
        MYSQL, ORACLE, POSTGRESQL, SQLSERVER, SQLITE;
        public static DataBaseEnum convert(String db) {
            DataBaseEnum result = DataBaseEnum.valueOf(db.toUpperCase());
            if(result == null) {
                result = MYSQL;
            }
            return result;
        }
    }
}
