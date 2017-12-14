package com.quartz.model.entity;

import com.quartz.model.annotation.Id;

import java.io.Serializable;

/**
 * @author 陈敏
 * Create date ：2017/10/19.
 * My blog： http://artislong.github.io
 */
public class QrtzTimedTaskParam implements Serializable {

    private static final long serialVersionUID = -2335037495608987344L;
    @Id
    private Long paramId;
    private String taskName;
    private String paramKey;
    private String paramValue;
    private String paramType;
    private String paramDesc;
    private Long sortId;

    public Long getParamId() {
        return paramId;
    }

    public QrtzTimedTaskParam setParamId(Long paramId) {
        this.paramId = paramId;
        return this;
    }

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
}
