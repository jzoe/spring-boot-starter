package com.quartz.schedule;import com.quartz.jdbc.QuartzRepository;import com.quartz.model.assist.STATUS;import com.quartz.model.entity.QrtzTimedTask;import com.quartz.utils.QuartzUtil;import com.quartz.utils.ScheduleUtil;import org.quartz.*;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.context.ApplicationContext;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.util.ObjectUtils;import java.util.List;import static com.quartz.model.constant.QuartzConstant.JOB_DATA_MAP_KEY;/** * @DESCRIPTION: 动态刷新定时任务配置 * Status: T:暂停任务 D:删除任务 U:正常 E:无效 * @AUTHER: chenmin * @CREATE BY: 18/1/2 下午4:09 */public class ScheduleRefresh {    public static final Logger logger = LoggerFactory.getLogger(ScheduleRefresh.class);    private Scheduler scheduler;    private QuartzRepository quartzRepository;    private ApplicationContext applicationContext;    /**     * 每隔1分钟查库，并根据查询结果决定是否重新设置定时任务     *     * @throws Exception     */    @Scheduled(cron = "0 * * * * *")    public void scheduleUpdateCronTrigger() {        List<QrtzTimedTask> newQrtzTimedTasks = quartzRepository.queryTaskAll();        for (QrtzTimedTask newQrtzTimedTask : newQrtzTimedTasks) {            String taskName = newQrtzTimedTask.getTaskName();            Object bean = applicationContext.getBean(taskName + "Trigger");            try {                switch (newQrtzTimedTask.getStatus()) {                    case U:                        // 有效的任务实时更新                        update(newQrtzTimedTask, bean);                        break;                    case D:                        // 需要删除的任务，任务删除后，状态改为E                        stop(newQrtzTimedTask, bean);                        break;                    case S:                        // 需要启动的任务，启动成功后将状态改为U                        start(newQrtzTimedTask);                        break;                }            } catch (SchedulerException e) {                logger.error(taskName + " done error, ", e);            }        }    }    /**     * 更新任务     *     * @param newQrtzTimedTask     * @param bean     * @throws SchedulerException     */    private void update(QrtzTimedTask newQrtzTimedTask, Object bean) throws SchedulerException {        if (ObjectUtils.isEmpty(bean)) {            return;        }        String taskName = newQrtzTimedTask.getTaskName();        CronTrigger cronTrigger = (CronTrigger) bean;        TriggerKey cronTriggerKey = cronTrigger.getKey();        String jobName = cronTriggerKey.getName();        String jobGroup = cronTriggerKey.getGroup();        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(cronTriggerKey);        JobDataMap jobDataMap = trigger.getJobDataMap();        Object jobData = jobDataMap.get(JOB_DATA_MAP_KEY);        // 是否更新参数        boolean isUpdateParam = false;        if (!ObjectUtils.isEmpty(jobData)) {            QrtzTimedTask qrtzTimedTask = (QrtzTimedTask) jobData;            isUpdateParam = !qrtzTimedTask.equals(newQrtzTimedTask);        }        String currentCron = trigger.getCronExpression();        String newCron = newQrtzTimedTask.getTaskExpres();        // 是否更新时间表达式        boolean isUpdateExpres = !currentCron.equals(newCron);        // 参数有变化，删除原任务，重新注册一个任务        if (isUpdateParam) {            // 删除原任务            ScheduleUtil.deleteJob(cronTriggerKey);            // 新建JobDetail            QuartzUtil.createJobDetailBean(newQrtzTimedTask);            // 新建CronTrigger            QuartzUtil.createCronTriggerBean(newQrtzTimedTask);            // 将任务加入调度工厂            QuartzUtil.startSchedule(scheduler, newQrtzTimedTask);        } else if (isUpdateExpres) {            // 参数无变化，时间表达式有变化直接更新原任务            logger.info("正在刷新" + taskName + "任务");            jobDataMap.put(JOB_DATA_MAP_KEY, newQrtzTimedTask);            // 表达式调度构建器            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(newCron);            // 按新的cronExpression表达式重新构建trigger            trigger = trigger.getTriggerBuilder()                    .withIdentity(cronTriggerKey) //                    .withSchedule(scheduleBuilder) //                    .usingJobData(jobDataMap) //                    .build();            // 按新的trigger重新设置job执行            scheduler.rescheduleJob(cronTriggerKey, trigger);            logger.info(taskName + "任务刷新结束");        }    }    /**     * 停止任务     * @param newQrtzTimedTask     * @param bean     */    private void stop(QrtzTimedTask newQrtzTimedTask, Object bean) {        if (ObjectUtils.isEmpty(bean)) {            return;        }        String taskName = newQrtzTimedTask.getTaskName();        CronTrigger cronTrigger = (CronTrigger) bean;        TriggerKey cronTriggerKey = cronTrigger.getKey();        ScheduleUtil.deleteJob(cronTriggerKey);        quartzRepository.updateTaskStatus(taskName, STATUS.E);    }    private void start(QrtzTimedTask newQrtzTimedTask) throws SchedulerException {        QuartzUtil.createJobDetailBean(newQrtzTimedTask);        QuartzUtil.createCronTriggerBean(newQrtzTimedTask);        QuartzUtil.startSchedule(scheduler, newQrtzTimedTask);        quartzRepository.updateTaskStatus(newQrtzTimedTask.getTaskName(), STATUS.U);    }    public ScheduleRefresh setScheduler(Scheduler scheduler) {        this.scheduler = scheduler;        return this;    }    public ScheduleRefresh setQuartzRepository(QuartzRepository quartzRepository) {        this.quartzRepository = quartzRepository;        return this;    }    public ScheduleRefresh setApplicationContext(ApplicationContext applicationContext) {        this.applicationContext = applicationContext;        return this;    }}