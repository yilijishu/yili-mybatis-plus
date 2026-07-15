package com.yilijishu.mybatis.entity;

import lombok.Data;

import java.util.List;

@Data
public class PageData<T> {

    private Page page;

    private List<T> data;

    public PageData(List<T> data, Page page) {
        this.page = page;
        this.data = data;
    }

    public PageData() {

    }
}
