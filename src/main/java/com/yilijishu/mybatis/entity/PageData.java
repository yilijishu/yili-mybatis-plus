package com.yilijishu.mybatis.entity;

import lombok.Data;

@Data
public class PageData<T> {

    private Page page;

    private T data;

    public PageData(T data, Page page) {
        this.page = page;
        this.data = data;
    }

    public PageData() {

    }
}
