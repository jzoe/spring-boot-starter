package com.github.quartz.jdbc;

/**
 * Created by zhujie on 2017/3/8.
 */

import java.io.Serializable;

/**
 * 分页
 */
public class Page implements Serializable {
    private Integer pageNo;
    private Integer pageSize;

    public Page() {
    }

    public Page(Integer pageNo, Integer pageSize) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public int firstResult() {
        return (pageNo - 1) * pageSize;
    }

}