package com.quartz.utils;import com.quartz.schedule.MethodInvokingJob;import com.quartz.config.QuartzProperties;import com.quartz.schedule.StatusScheduleJob;import com.quartz.model.entity.QrtzTimedTask;import org.quartz.*;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import static com.quartz.model.constant.QuartzConstant.QUARTZ_DATA_KEY;/** * Created by chenmin on 17/11/13. * Schedule定时任务工具类，提供增加，停止，修改，运行功能。 */public class ScheduleUtil {    public static final Logger logger = LoggerFactory.getLogger(ScheduleUtil.class);    private static Scheduler scheduler;    private static QuartzProperties quartzProperties;    public static TriggerKey getTriggerKey(String jobName) {        return getTriggerKey(jobName, TriggerKey.DEFAULT_GROUP);    }    public static TriggerKey getTriggerKey(String jobName, String jobGroup) {        return TriggerKey.triggerKey(jobName, jobGroup);    }    public static TriggerBuilder createTriggerBuilder(String jobName, String jobGroup) {        return TriggerBuilder.newTrigger().withIdentity(jobName, jobGroup);    }    public static JobBuilder createJobBuilder(Class<? extends Job> jobClass, String jobName, String jobGroup) {        return JobBuilder.newJob(jobClass).withIdentity(jobName, jobGroup);    }    public static JobDetail createJobDetail(JobBuilder jobBuilder) {        return jobBuilder.build();    }    public static void initJob(QrtzTimedTask qrtzTimedTask) {        initJob(qrtzTimedTask, quartzProperties.getConcurrent()?MethodInvokingJob.class : StatusScheduleJob.class);    }    /**     * 初始化任务调度     *     * @param qrtzTimedTask     * @param cls     */    public static void initJob(QrtzTimedTask qrtzTimedTask, Class cls) {        logger.info("初始化任务调度");        try {            TriggerKey triggerKey = TriggerKey.triggerKey(qrtzTimedTask.getTaskName(), qrtzTimedTask.getTaskGroup());            CronTrigger trigger = (CronTrigger) ScheduleUtil.scheduler.getTrigger(triggerKey);            if (null == trigger) {                addQuartzJob(qrtzTimedTask, trigger, cls);            }        } catch (Exception e) {            logger.error("初始化任务调度异常！" + e.getMessage(), e);        }    }    private static void addQuartzJob(QrtzTimedTask qrtzTimedTask, CronTrigger trigger, Class cls) {        logger.info("向任务调度中添加定时任务");        try {            JobDetail jobDetail = JobBuilder.newJob(cls)                    .withIdentity(qrtzTimedTask.getTaskName(), qrtzTimedTask.getTaskGroup()).build();            jobDetail.getJobDataMap().put(QUARTZ_DATA_KEY, qrtzTimedTask);            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(qrtzTimedTask.getTaskExpres());            trigger = TriggerBuilder.newTrigger().withIdentity(qrtzTimedTask.getTaskName(), qrtzTimedTask.getTaskGroup())                    .withSchedule(scheduleBuilder).build();            ScheduleUtil.scheduler.scheduleJob(jobDetail, trigger);        } catch (Exception e) {            logger.error("向任务调度中添加定时任务异常！" + e.getMessage(), e);        }    }    /**     * 立即运行定时任务     *     * @param jobName     */    public static void runJob(String jobName) {        runJob(jobName, TriggerKey.DEFAULT_GROUP);    }    public static void runJob(String jobName, String jobGroup) {        logger.info("立即运行任务调度中的定时任务");        try {            if (null == jobName) {                logger.info("定时任务信息为空，无法立即运行");                return;            }            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);            if (null == jobKey) {                logger.info("任务调度中不存在[" + jobName + "]定时任务，不予立即运行！");                return;            }            ScheduleUtil.scheduler.triggerJob(jobKey);        } catch (Exception e) {            logger.error("立即运行任务调度中的定时任务异常！", e);        }    }    /**     * 修改任务调度中的定时任务     *     * @param cronExp     * @param triggerKey     * @param trigger     */    public static void updateJob(String cronExp, TriggerKey triggerKey, CronTrigger trigger) {        logger.info("修改任务调度中的定时任务");        try {            if (null == cronExp || null == triggerKey || null == trigger) {                logger.info("修改调度任务参数不正常！");                return;            }            logger.info("原始任务表达式:" + trigger.getCronExpression()                    + "，现在任务表达式:" + cronExp);            if (trigger.getCronExpression().equals(cronExp)) {                logger.info("任务调度表达式一致，不予进行修改！");                return;            }            logger.info("任务调度表达式不一致，进行修改");            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExp);            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();            ScheduleUtil.scheduler.rescheduleJob(triggerKey, trigger);        } catch (Exception e) {            logger.error("修改任务调度中的定时任务异常！", e);        }    }    /**     * 暂停任务调度中的定时任务     *     * @param jobName     */    public static void pauseJob(String jobName) {        pauseJob(jobName, TriggerKey.DEFAULT_GROUP);    }    /**     * 暂停任务调度中的定时任务     *     * @param jobName     * @param jobGroup     */    public static void pauseJob(String jobName, String jobGroup) {        logger.info("暂停任务调度中的定时任务");        try {            if (null == jobName) {                logger.info("暂停调度任务参数不正常！");                return;            }            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);            if (null == jobKey) {                logger.info("任务调度中不存在[" + jobName + "]定时任务，不予进行暂停！");                return;            }            ScheduleUtil.scheduler.pauseJob(jobKey);        } catch (Exception e) {            logger.error("暂停任务调度中的定时任务异常！", e);        }    }    /**     * 恢复任务调度中的定时任务     *     * @param jobName     */    public static void resumeJob(String jobName) {        resumeJob(jobName, TriggerKey.DEFAULT_GROUP);    }    /**     * 恢复任务调度中的定时任务     *     * @param jobName     * @param jobGroup     */    public static void resumeJob(String jobName, String jobGroup) {        logger.info("恢复任务调度中的定时任务");        try {            if (null == jobName) {                logger.info("恢复调度任务参数不正常！");                return;            }            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);            if (null == jobKey) {                logger.info("任务调度中不存在[" + jobName + "]定时任务，不予进行恢复！");                return;            }            ScheduleUtil.scheduler.resumeJob(jobKey);        } catch (Exception e) {            logger.error("恢复任务调度中的定时任务异常！", e);        }    }    /**     * 删除任务调度中的定时任务     *     * @param jobName     */    public static void deleteJob(String jobName) {        deleteJob(jobName, TriggerKey.DEFAULT_GROUP);    }    /**     * 删除任务调度中的定时任务     *     * @param jobName     * @param jobGroup     */    public static void deleteJob(String jobName, String jobGroup) {        logger.info("删除任务调度中的定时任务");        try {            if (null == jobName) {                logger.info("删除调度任务参数不正常！");                return;            }            JobKey jobKey = JobKey.jobKey(jobName, jobGroup);            if (null == jobKey) {                logger.info("任务调度中不存在[" + jobName + "]定时任务，不予进行删除！");                return;            }            ScheduleUtil.scheduler.deleteJob(jobKey);        } catch (Exception e) {            logger.error("删除任务调度中的定时任务异常！", e);        }    }    /**     * 删除任务调度定时器     *     * @param triggerKey     */    public static void deleteJob(TriggerKey triggerKey) {        logger.info("删除任务调度定时器");        try {            if (null == triggerKey) {                logger.info("停止任务定时器参数不正常，不予进行停止！");                return;            }            logger.info("停止任务定时器");            ScheduleUtil.scheduler.pauseTrigger(triggerKey);            ScheduleUtil.scheduler.unscheduleJob(triggerKey);        } catch (Exception e) {            logger.info("删除任务调度定时器异常！", e);        }    }    public ScheduleUtil setScheduler(Scheduler scheduler) {        ScheduleUtil.scheduler = scheduler;        return this;    }    public ScheduleUtil setQuartzProperties(QuartzProperties quartzProperties) {        ScheduleUtil.quartzProperties = quartzProperties;        return this;    }}