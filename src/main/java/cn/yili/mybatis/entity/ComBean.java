package cn.yili.mybatis.entity;

import lombok.Data;

@Data
public class ComBean {
    private String name;

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
    private String ifFieldConditionName;
}
