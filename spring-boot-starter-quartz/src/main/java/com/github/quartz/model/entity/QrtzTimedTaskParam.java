package com.github.quartz.model.entity;

import java.io.Serializable;

/**
 * @author 陈敏
 * Create date ：2017/10/19.
 * My blog： http://artislong.github.io
 */
public class QrtzTimedTaskParam implements Serializable {

    private static final long serialVersionUID = -2335037495608987344L;
    private String taskName;
    private String paramKey;
    private String paramValue;
    private String paramType;
    private String paramDesc;
    private Long sortId;

    public String getTaskName() {
        return taskName;
    }

    public QrtzTimedTaskParam setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public String getParamKey() {
        return paramKey;
    }

    public QrtzTimedTaskParam setParamKey(String paramKey) {
        this.paramKey = paramKey;
        return this;
    }

    public String getParamValue() {
        return paramValue;
    }

    public QrtzTimedTaskParam setParamValue(String paramValue) {
        this.paramValue = paramValue;
        return this;
    }

    public String getParamType() {
        return paramType;
    }

    public QrtzTimedTaskParam setParamType(String paramType) {
        this.paramType = paramType;
        return this;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public QrtzTimedTaskParam setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
        return this;
    }

    public Long getSortId() {
        return sortId;
    }

    public QrtzTimedTaskParam setSortId(Long sortId) {
        this.sortId = sortId;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QrtzTimedTaskParam that = (QrtzTimedTaskParam) o;
        if (paramKey != null ? !paramKey.equals(that.paramKey) : that.paramKey != null) return false;
        if (paramValue != null ? !paramValue.equals(that.paramValue) : that.paramValue != null) return false;
        if (paramType != null ? !paramType.equals(that.paramType) : that.paramType != null) return false;
        return sortId != null ? sortId.equals(that.sortId) : that.sortId == null;
    }

    @Override
    public int hashCode() {
        int result = paramKey != null ? paramKey.hashCode() : 0;
        result = 31 * result + (paramValue != null ? paramValue.hashCode() : 0);
        result = 31 * result + (paramType != null ? paramType.hashCode() : 0);
        result = 31 * result + (sortId != null ? sortId.hashCode() : 0);
        return result;
    }
}
