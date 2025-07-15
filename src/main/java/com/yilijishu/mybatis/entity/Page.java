package com.yilijishu.mybatis.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 计算相关内容
 * Created by ADMIN on 16/11/23.
 */
public class Page {

    @Setter
    private Integer start = 0;

    @Getter
    @Setter
    private Integer pageSize = 20;

    @Setter
    private Integer end = 0;

    @Getter
    @Setter
    private Integer count = 0;

    @Setter
    private Integer pageCount = 0;

    @Getter
    @Setter
    private Integer page = 1;

    @Getter
    @Setter
    private Integer offset = 0;

    public Page(Integer page, Integer pageSize, Integer count) {
        if (page != null && page > 0) {
            this.page = page;
        }
        if (pageSize != null && pageSize > 0) {
            this.pageSize = pageSize;
        }
        this.count = count;
    }

    public Page() {
    }

    public Page(Integer page, Integer pageSize) {

        if (page != null && page > 0) {
            this.page = page;
        }

        if (pageSize != null && pageSize > 0) {
            this.pageSize = pageSize;
        }
    }


    public Integer getStart() {
        return (page - 1) * pageSize + offset;
    }

    public Integer getEnd() {
        return page * pageSize - 1 + offset;
    }

    public Integer getPageCount() {
        return (count - 1) / pageSize + 1;
    }

    public Integer getNextPage() {
        if (hasNextPage()) {
            return page + 1;
        }
        return page;
    }

    public boolean hasNextPage() {
        if (getPageCount() > page) {
            return true;
        }
        return false;
    }

    public boolean isQuery() {
        return this.getCount() > 0 && this.getPage() <= this.getPageCount();
    }

}
