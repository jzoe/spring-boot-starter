package com.quartz.model.entity;

import com.quartz.model.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 陈敏
 * Create date ：2017/10/19.
 * My blog： http://artislong.github.io
 */
public class QrtzTimedTask implements Serializable {
    private static final long serialVersionUID = 6302921962918981967L;

    @Id
    private Long taskId;
    private String taskName;
    private String taskDesc;
    private String taskExpres;
    private String taskClass;
    private String taskMethod;
    private String taskGroup;
    private String status;
    private Date createTime;
    private String creater;
    private String ext1;
    private String ext2;
    private Date ext3;
    private Date ext4;

    private List<QrtzTimedTaskParam> qrtzTimedTaskParams = new ArrayList<QrtzTimedTaskParam>();

    public Long getTaskId() {
        return taskId;
    }

    public QrtzTimedTask setTaskId(Long taskId) {
        this.taskId = taskId;
        return this;
    }

    public String getTaskName() {
        return taskName;
    }

    public QrtzTimedTask setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public QrtzTimedTask setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc;
        return this;
    }

    public String getTaskExpres() {
        return taskExpres;
    }

    public QrtzTimedTask setTaskExpres(String taskExpres) {
        this.taskExpres = taskExpres;
        return this;
    }

    public String getTaskClass() {
        return taskClass;
    }

    public QrtzTimedTask setTaskClass(String taskClass) {
        this.taskClass = taskClass;
        return this;
    }

    public String getTaskMethod() {
        return taskMethod;
    }

    public QrtzTimedTask setTaskMethod(String taskMethod) {
        this.taskMethod = taskMethod;
        return this;
    }

    public String getTaskGroup() {
        return taskGroup;
    }

    public QrtzTimedTask setTaskGroup(String taskGroup) {
        this.taskGroup = taskGroup;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public QrtzTimedTask setStatus(String state) {
        this.status = state;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public QrtzTimedTask setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getExt1() {
        return ext1;
    }

    public QrtzTimedTask setExt1(String ext1) {
        this.ext1 = ext1;
        return this;
    }

    public String getExt2() {
        return ext2;
    }

    public QrtzTimedTask setExt2(String ext2) {
        this.ext2 = ext2;
        return this;
    }

    public Date getExt3() {
        return ext3;
    }

    public QrtzTimedTask setExt3(Date ext3) {
        this.ext3 = ext3;
        return this;
    }

    public Date getExt4() {
        return ext4;
    }

    public QrtzTimedTask setExt4(Date ext4) {
        this.ext4 = ext4;
        return this;
    }

    public String getCreater() {
        return creater;
    }

    public QrtzTimedTask setCreater(String creater) {
        this.creater = creater;
        return this;
    }

    public List<QrtzTimedTaskParam> getQrtzTimedTaskParams() {
        return qrtzTimedTaskParams;
    }

    public QrtzTimedTask setQrtzTimedTaskParams(List<QrtzTimedTaskParam> qrtzTimedTaskParams) {
        this.qrtzTimedTaskParams.addAll(qrtzTimedTaskParams);
        return this;
    }
}
