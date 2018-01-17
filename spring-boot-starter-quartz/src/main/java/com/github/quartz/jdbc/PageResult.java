package com.github.quartz.jdbc;

import java.util.List;

/**
 * Created by zhujie on 2017/3/8.
 */
public class PageResult<T> {
    private Long total;// 总条数
    private Integer pageSize;// 页面大小
    private Integer pageNo;// 当前页
    private Integer totalPageCount;// 共几页
    private List<T> items;// 集合数据

    public PageResult(Long total, Integer pageSize, Integer pageNo,
                      List<T> items) {
        this.total = total;
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.items = items;
        this.totalPageCount = (int) (total % pageSize == 0 ? total / pageSize : total
                / pageSize + 1);
    }


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
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

    public Integer getTotalPageCount() {
        return totalPageCount;
    }

    public void setTotalPageCount(Integer totalPageCount) {
        this.totalPageCount = totalPageCount;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

}
