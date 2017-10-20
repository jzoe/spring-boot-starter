package com.quartz.pojo;

import java.io.Serializable;

/**
 * Created by 陈敏 on 2017/4/27.
 */
public class QrtzTimedTaskParamTd implements Serializable {

    private static final long serialVersionUID = -2335037495608987344L;
    private Long paramId;
    private String taskName;
    private String paramKey;
    private String paramValue;
    private String paramType;
    private String paramDesc;

    public Long getParamId() {
        return paramId;
    }

    public void setParamId(Long paramId) {
        this.paramId = paramId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

}
