package com.yilijishu.mybatis.entity;

import lombok.Data;

import java.lang.reflect.Type;

@Data
public class ComBean {
    private String name;

    private String defTypeColumn;

    private boolean column = false;
    private String columValue;

    private boolean ignore = false;


    private boolean defWhere  = false;
    private String defWhereValue;

    //是否为主键
    private boolean tableId = false;

    //ORDERBY
    private boolean orderBy = false;
    private String orderByVal;
    private Integer order;


    //插入是否忽略.
    private boolean ignoreInsert = false;

    //判断是否增加判断条件
    private boolean ifFieldCondition = false;
    //判断条件名
    private String ifFieldConditionName;

    //虚拟ID
    private boolean virtualTableId = false;

    //不为空
    private Boolean notNull = false;

    //数据库类型
    private String columnType;

    /**
     * 默认修改
     */
    private boolean autoModifyTime = false;

    //默认创建
    private boolean autoCreateTime = false;

    //是否删除
    private boolean delTag = false;
    //删除标记
    private String delTagValue;

    private boolean handler = false;
    private String handlerStr;

}
