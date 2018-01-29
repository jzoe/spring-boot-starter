package com.github.quartz.model.entity;

import com.github.quartz.model.assist.STATUS;
import org.quartz.TriggerKey;

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
    private String taskName;
    private String taskDesc;
    private String taskExpres;
    private String taskClass;
    private String taskMethod;
    private String taskGroup = TriggerKey.DEFAULT_GROUP;
    private STATUS status = STATUS.U;
    private Date createTime;
    private String creater;
    private String uri;

    private List<QrtzTimedTaskParam> qrtzTimedTaskParams = new ArrayList<QrtzTimedTaskParam>();

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

    public STATUS getStatus() {
        return status;
    }

    public QrtzTimedTask setStatus(String state) {
        this.status = STATUS.valueOf(state);
        return this;
    }

    public QrtzTimedTask setStatus(STATUS state) {
        this.status = status;
        return this;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public QrtzTimedTask setCreateTime(Date createTime) {
        this.createTime = createTime;
        return this;
    }

    public String getCreater() {
        return creater;
    }

    public QrtzTimedTask setCreater(String creater) {
        this.creater = creater;
        return this;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<QrtzTimedTaskParam> getQrtzTimedTaskParams() {
        return qrtzTimedTaskParams;
    }

    public QrtzTimedTask setQrtzTimedTaskParams(List<QrtzTimedTaskParam> qrtzTimedTaskParams) {
        this.qrtzTimedTaskParams.addAll(qrtzTimedTaskParams);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QrtzTimedTask that = (QrtzTimedTask) o;
        return qrtzTimedTaskParams != null ? qrtzTimedTaskParams.equals(that.qrtzTimedTaskParams) : that.qrtzTimedTaskParams == null;
    }

    @Override
    public int hashCode() {
        int result = qrtzTimedTaskParams != null ? qrtzTimedTaskParams.hashCode() : 0;
        return result;
    }

    public boolean isEmpty() {
        boolean isEmpty = this.getTaskName() == null
                && this.getTaskMethod() == null
                && this.getTaskClass() == null
                && this.getTaskExpres() == null
                && this.getTaskGroup() == null
                && this.getStatus() == null;
        return isEmpty;
    }
}
