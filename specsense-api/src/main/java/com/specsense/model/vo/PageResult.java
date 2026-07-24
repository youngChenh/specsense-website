package com.specsense.model.vo;

import lombok.Data;

@Data
public class PageResult<T> {
    private long total;
    private int page;
    private int pageSize;
    private T data;

    public PageResult(long total, int page, int pageSize, T data) {
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.data = data;
    }
}
