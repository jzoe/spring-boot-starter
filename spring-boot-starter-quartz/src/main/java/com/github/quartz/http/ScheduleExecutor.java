package com.github.quartz.http;import com.github.quartz.model.entity.QrtzTimedTask;import org.quartz.SchedulerException;/** * 定时任务调度类 */public interface ScheduleExecutor {    /**     * 调用接口     * @param qrtzTimedTask     * @return     */    void invoke(QrtzTimedTask qrtzTimedTask) throws SchedulerException;    void add(QrtzTimedTask qrtzTimedTask) throws SchedulerException;    void stop(QrtzTimedTask qrtzTimedTask) throws SchedulerException;    void update(QrtzTimedTask qrtzTimedTask) throws SchedulerException;}