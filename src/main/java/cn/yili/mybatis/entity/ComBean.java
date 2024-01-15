package cn.yili.mybatis.entity;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;

@Data
public class ComBean {
    private String name;

    private boolean column;
    private String columValue;

    private boolean ignore;


    private boolean defWhere;
    private String defWhereValue;

    //是否为主键
    private boolean tableId;


    private boolean orderBy;
    private String orderByVal;
    private Integer order;


}
