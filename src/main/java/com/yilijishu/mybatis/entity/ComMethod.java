package com.yilijishu.mybatis.entity;

import lombok.Data;

import java.util.List;

@Data
public class ComMethod {

    private boolean overrideOrderBy = false;

    private String overriderOrderByMethod;

    private boolean addSelectCondition = false;

    private List<String> addSelectConditionMethod;

}
