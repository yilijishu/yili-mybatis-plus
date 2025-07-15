package com.yilijishu.mybatis.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 添加字段添加and判断条件。
 * 所设置的字段名必须要返回boolean
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IfFieldCondition {
    String value(); //字段名 方法名
}
