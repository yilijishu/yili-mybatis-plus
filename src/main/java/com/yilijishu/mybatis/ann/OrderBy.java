package com.yilijishu.mybatis.ann;

import com.yilijishu.mybatis.constant.Constant;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 默认order by 条件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OrderBy {
    String value() default Constant.OrderByValue.DESC;
    int order() default 1;

}
