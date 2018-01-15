package com.github.quartz.config;

import com.github.quartz.PropertyPlaceholder;
import com.github.quartz.http.ScheduleExecutor;
import com.github.quartz.http.ScheduleExecutorImpl;
import com.github.quartz.http.ScheduleServlet;
import com.github.quartz.jdbc.QuartzRepository;
import com.github.quartz.model.assist.QrtzStatus;
import com.github.quartz.model.entity.QrtzTimedTask;
import com.github.quartz.schedule.QuartzInit;
import com.github.quartz.schedule.ScheduleRefresh;
import com.github.quartz.utils.QuartzUtil;
import com.github.quartz.utils.ScheduleUtil;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.github.quartz.model.constant.HttpConstant.QUARTZ_API;

/**
 * @author 陈敏
 *         Create date ：2017/10/19.
 *         My blog： http://artislong.github.io
 */
@Configuration
@EnableScheduling
@ConditionalOnBean(QuartzRepository.class)
@AutoConfigureAfter(QuartzBeanConfiguration.class)
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfiguration implements BeanFactoryAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(QuartzAutoConfiguration.class);

    private ApplicationContext applicationContext;
    private List<QrtzTimedTask> qrtzTimedTaskList = new ArrayList<QrtzTimedTask>();
    private boolean isStart;
    private BeanFactory beanFactory;

    public QuartzAutoConfiguration(QuartzProperties quartzProperties, QuartzRepository quartzRepository) {
        isStart = QuartzUtil.quartzIsStart(quartzProperties);
        qrtzTimedTaskList.addAll(getTaskExecutors(quartzRepository));
    }

//    @Bean
//    public List<BeanInvokingJobDetailFactoryBean> methodInvokingJobDetailFactoryBean() {
//        if (isStart) {
//            QuartzUtil.createJobDetailBeans(qrtzTimedTaskList);
//        }
//        return null;
//    }
//
//    @Bean
//    public List<CronTriggerFactoryBean> cronTriggerFactoryBean() {
//        if (isStart) {
//            QuartzUtil.createCronTriggerBeans(qrtzTimedTaskList);
//        }
//        return null;
//    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,
                                                     QuartzProperties quartzProperties,
                                                     @Qualifier("quartzPlaceholder") PropertyPlaceholder quartzPlaceholder) {
        if (isStart) {
            QuartzUtil.createJobDetailBeans(qrtzTimedTaskList);
            QuartzUtil.createCronTriggerBeans(qrtzTimedTaskList);
        } else {
            return null;
        }
        List<Trigger> triggers = new ArrayList<Trigger>();
        for (QrtzTimedTask qrtzTimedTask : qrtzTimedTaskList) {
            Trigger trigger = (Trigger) applicationContext.getBean(qrtzTimedTask.getTaskName() + "Trigger");
            triggers.add(trigger);
        }
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(triggers.toArray(new Trigger[0]));
        Properties properties = quartzProperties();
        if (properties.isEmpty()) {
            properties.putAll(quartzPlaceholder.getProperties());
        }
        schedulerFactoryBean.setQuartzProperties(properties);
        // 用于quartz集群,QuartzScheduler 启动时更新己存在的Job，这样就不用每次修改targetObject后删除qrtz_job_details表对应记录了
        schedulerFactoryBean.setOverwriteExistingJobs(quartzProperties.getOverwriteExistingJobs());
        // QuartzScheduler 延时启动，应用启动完10秒后 QuartzScheduler 再启动
        schedulerFactoryBean.setStartupDelay(quartzProperties.getStartupDelay());
        schedulerFactoryBean.setAutoStartup(quartzProperties.getAutoStartup());
        schedulerFactoryBean.setDataSource(dataSource);
        return schedulerFactoryBean;
    }

    @Bean
    public ScheduleUtil scheduleUtil(Scheduler scheduler, QuartzProperties quartzProperties) {
        return new ScheduleUtil()  //
                .setQuartzProperties(quartzProperties)  //
                .setScheduler(scheduler);
    }

    @Bean
    public ScheduleRefresh scheduleRefresh(Scheduler scheduler, QuartzRepository quartzRepository) {
        return new ScheduleRefresh()    //
                .setScheduler(scheduler)    //
                .setQuartzRepository(quartzRepository)  //
                .setApplicationContext(applicationContext);
    }

    @Bean
    public ScheduleExecutor scheduleExecutor(Scheduler scheduler) {
        return new ScheduleExecutorImpl()
                .setApplicationContext(applicationContext)
                .setScheduler(scheduler);
    }

    @Bean
    public QuartzInit quartzCleanner(DataSource dataSource, QuartzProperties quartzProperties) {
        return new QuartzInit(quartzProperties, dataSource).setSql("cleanTask.sql");
    }

    @Bean
    public QuartzUtil quartzUtil(QuartzRepository quartzRepository, QuartzProperties quartzProperties) {
        return new QuartzUtil()
                .setConfigurableBeanFactory(beanFactory)
                .setQuartzProperties(quartzProperties)
                .setApplicationContext(applicationContext)
                .setQuartzRepository(quartzRepository);
    }

    @Bean
    public ScheduleServlet scheduleServlet(ScheduleExecutor scheduleExecutor) {
        ScheduleServlet scheduleServlet = new ScheduleServlet();
        scheduleServlet.setScheduleExecutor(scheduleExecutor);
        return scheduleServlet;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(ScheduleServlet scheduleServlet) {
        ServletRegistrationBean servletRegister = new ServletRegistrationBean(scheduleServlet, QUARTZ_API);
        servletRegister.setAsyncSupported(true);
        return servletRegister;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private List<QrtzTimedTask> getTaskExecutors(QuartzRepository quartzRepository) {
        List<QrtzTimedTask> qrtzTimedTasks = quartzRepository.queryValidTaskAndParam(QrtzStatus.U);
        if (qrtzTimedTasks.isEmpty()) {
            qrtzTimedTasks.addAll(getDefaultTask());
        }
        return qrtzTimedTasks;
    }


    private static Properties quartzProperties() {
        Properties properties = new Properties();
        String path = System.getProperty("quartz.config.location");
        if (path == null) {
            logger.info("jvm的quartz.config.location参数未配置，读取QUARTZ_CONFIG_LOCATION环境变量");
            path = System.getenv("QUARTZ_CONFIG_LOCATION");
        }
        if (path == null) {
            logger.info("QUARTZ_CONFIG_LOCATION环境变量未配置，将使用默认配置");
            return properties;
        }
        File application = new File(path + "quartz.properties");
        if (!application.exists()) {
            logger.error(path + "quartz.properties配置文件不存在，将使用默认配置");
            return properties;
        }
        try {
            Properties applicationProperties = new Properties();
            applicationProperties.load(new FileInputStream(application));
            properties.putAll(applicationProperties);
            logger.info("加载" + path + "quartz.properties" + "配置文件完成");
        } catch (Exception e) {
            logger.error("加载" + path + "quartz.properties" + "配置文件失败，错误信息: {}", e);
            return properties;
        }

        return properties;
    }

    private static List<QrtzTimedTask> getDefaultTask() {
        List<QrtzTimedTask> defaultTask = new ArrayList<QrtzTimedTask>();
        String defaultScheduler = "defaultScheduler";
        defaultTask.add(new QrtzTimedTask()
                .setTaskName(defaultScheduler)
                .setTaskClass("com.quartz.config.QuartzAutoConfiguration.DefaultScheduler")
                .setTaskExpres("0 0 4 ? * *")
                .setTaskMethod("execute")
                .setTaskDesc("default scheduler")
                .setTaskGroup(TriggerKey.DEFAULT_GROUP));
        return defaultTask;
    }

    @Bean
    public DefaultScheduler defaultScheduler() {
        return new DefaultScheduler();
    }

    public static class DefaultScheduler {
        public void execute() {
        }
    }
}
