package com.github.quartz.schedule;import com.github.quartz.jdbc.QuartzRepository;import com.github.quartz.model.entity.QrtzTimedTask;import com.github.quartz.utils.QuartzUtil;import org.quartz.Scheduler;import org.quartz.SchedulerException;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.InitializingBean;import org.springframework.context.ApplicationContext;import org.springframework.scheduling.annotation.Scheduled;import org.springframework.util.ObjectUtils;import java.util.List;/** * @DESCRIPTION: 动态刷新定时任务配置 * Status: T:暂停任务 D:删除任务 U:正常 E:无效 * @AUTHER: chenmin * @CREATE BY: 18/1/2 下午4:09 */public class ScheduleRefresh implements InitializingBean {    public static final Logger logger = LoggerFactory.getLogger(ScheduleRefresh.class);    private Scheduler scheduler;    private QuartzRepository quartzRepository;    private ApplicationContext applicationContext;    private boolean isRefresh;    /**     * 每隔1分钟查库，并根据查询结果决定是否重新设置定时任务     *     * @throws Exception     */    @Scheduled(cron = "0 * * * * *")    public void scheduleUpdateCronTrigger() {        if (!isRefresh) {            return;        }        List<QrtzTimedTask> newQrtzTimedTasks = quartzRepository.queryTaskAll();        for (QrtzTimedTask newQrtzTimedTask : newQrtzTimedTasks) {            String taskName = newQrtzTimedTask.getTaskName();            Object bean = applicationContext.getBean(taskName + "Trigger");            try {                switch (newQrtzTimedTask.getStatus()) {                    case U:                        // 有效的任务实时更新                        QuartzUtil.update(newQrtzTimedTask, scheduler);                        break;                    case D:                        // 需要删除的任务，任务删除后，状态改为E                        QuartzUtil.stop(newQrtzTimedTask);                        break;                    case S:                        // 需要启动的任务，启动成功后将状态改为U                        QuartzUtil.start(newQrtzTimedTask, scheduler);                        break;                }            } catch (SchedulerException e) {                logger.error(taskName + " done error, ", e);            }        }    }    public ScheduleRefresh setScheduler(Scheduler scheduler) {        this.scheduler = scheduler;        return this;    }    public ScheduleRefresh setQuartzRepository(QuartzRepository quartzRepository) {        this.quartzRepository = quartzRepository;        return this;    }    public ScheduleRefresh setApplicationContext(ApplicationContext applicationContext) {        this.applicationContext = applicationContext;        return this;    }    @Override    public void afterPropertiesSet() throws Exception {        if (ObjectUtils.isEmpty(scheduler)) {            logger.debug("scheduler is null");            isRefresh = false;        }        if (ObjectUtils.isEmpty(quartzRepository)) {            logger.debug("quartzRepository is null");            isRefresh = false;        }        if (ObjectUtils.isEmpty(applicationContext)) {            logger.debug("applicationContext is null");            isRefresh = false;        }    }}