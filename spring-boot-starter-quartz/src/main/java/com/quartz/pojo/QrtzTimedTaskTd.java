package com.quartz.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 陈敏 on 2017/4/18.
 */
public class QrtzTimedTaskTd implements Serializable {
    private static final long serialVersionUID = 6302921962918981967L;

    private Long taskId;
    private String taskName;
    private String taskDesc;
    private String taskExpres;
    private String taskClass;
    private String taskMethod;
    private String taskGroup;
    private String runMode;
    private String status;
    private Date createTime;
    private String creater;
    private String ext1;
    private String ext2;
    private Date ext3;
    private Date ext4;

    private List<QrtzTimedTaskParamTd> qrtzTimedTaskParamTds = new ArrayList<QrtzTimedTaskParamTd>();

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
    }

    public String getTaskExpres() {
        return taskExpres;
    }

    public void setTaskExpres(String taskExpres) {
        this.taskExpres = taskExpres;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public void setTaskClass(String taskClass) {
        this.taskClass = taskClass;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public void setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public void setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
    }

    public String getRunMode() {
        return runMode;
    }

    public void setRunMode(String runMode) {
        this.runMode = runMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String state) {
        this.status = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public Date getExt3() {
        return ext3;
    }

    public void setExt3(Date ext3) {
        this.ext3 = ext3;
    }

    public Date getExt4() {
        return ext4;
    }

    public void setExt4(Date ext4) {
        this.ext4 = ext4;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public List<QrtzTimedTaskParamTd> getQrtzTimedTaskParamTds() {
        return qrtzTimedTaskParamTds;
    }

    public void setQrtzTimedTaskParamTds(List<QrtzTimedTaskParamTd> qrtzTimedTaskParamTds) {
        this.qrtzTimedTaskParamTds.addAll(qrtzTimedTaskParamTds);
    }
}