package com.github.quartz.http;import com.github.quartz.model.entity.QrtzTimedTask;/** * 定时任务调度类 */public interface ScheduleExecutor {    /**     * 调用接口     * @param qrtzTimedTask     * @return     */    void invoke(QrtzTimedTask qrtzTimedTask);}