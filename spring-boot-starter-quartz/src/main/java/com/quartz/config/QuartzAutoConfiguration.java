package com.quartz.config;

import com.quartz.PropertyPlaceholder;
import com.quartz.jdbc.QuartzRepository;
import com.quartz.model.assist.STATUS;
import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;
import com.quartz.schedule.ScheduleRefresh;
import com.quartz.utils.BeanUtil;
import com.quartz.utils.ClassUtil;
import com.quartz.utils.ScheduleUtil;
import com.quartz.utils.SpringContextUtils;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * @author 陈敏
 *         Create date ：2017/10/19.
 *         My blog： http://artislong.github.io
 */
@Configuration
@EnableScheduling
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfiguration implements BeanFactoryAware, EnvironmentAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(QuartzAutoConfiguration.class);

    private Map<String, QrtzTimedTask> taskExecutorMap = new LinkedHashMap<String, QrtzTimedTask>();
    private BeanFactory beanFactory;
    private Environment environment;
    private ApplicationContext applicationContext = null;

    @Bean("defaultMethodInvokingJobDetailFactoryBean")
    public BeanInvokingJobDetailFactoryBean jobDetailFactoryBean() {
        BeanInvokingJobDetailFactoryBean beanInvokingJobDetailFactoryBean = new BeanInvokingJobDetailFactoryBean();
        beanInvokingJobDetailFactoryBean.setApplicationContext(applicationContext);
        return beanInvokingJobDetailFactoryBean;
    }

    @Bean
    @Order(1)
    public List<BeanInvokingJobDetailFactoryBean> methodInvokingJobDetailFactoryBean(QuartzRepository quartzRepository,
                                                                                     QuartzProperties quartzProperties) {
        DefaultListableBeanFactory configurableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        taskExecutorMap.putAll(getTaskExecutors(quartzRepository));
        for (String key : taskExecutorMap.keySet()) {
            String beanName = key + "JobDetail";
            QrtzTimedTask qrtzTimedTask = taskExecutorMap.get(key);
            Map<String, Object> params = buildParams(qrtzTimedTask);
            String targetObject = buildTargetObject(qrtzTimedTask);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanInvokingJobDetailFactoryBean.class).setScope("prototype");
            if (!ObjectUtils.isEmpty(qrtzTimedTask.getTaskGroup())) {
                beanDefinitionBuilder.addPropertyValue("group", qrtzTimedTask.getTaskGroup());
            }
            if (!params.isEmpty()) {
                beanDefinitionBuilder.addPropertyValue("arguments", new Object[]{params});
            }
            configurableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder
                    .addPropertyValue("targetMethod", qrtzTimedTask.getTaskMethod())
                    .addPropertyValue("targetBean", targetObject)
                    .addPropertyValue("durable", quartzProperties.getDurability())
                    .addPropertyValue("shouldRecover", quartzProperties.getShouldRecover())
                    .addPropertyValue("volatility", quartzProperties.getVolatility())
                    .addPropertyValue("concurrent", quartzProperties.getConcurrent())
                    .getRawBeanDefinition());
        }
        return null;
    }

    @Bean
    @Order(2)
    public List<CronTriggerFactoryBean> cronTriggerFactoryBean() {
        DefaultListableBeanFactory configurableBeanFactory = (DefaultListableBeanFactory) beanFactory;
        for (String key : taskExecutorMap.keySet()) {
            QrtzTimedTask qrtzTimedTask = taskExecutorMap.get(key);
            configurableBeanFactory.registerBeanDefinition(key + "Trigger", BeanDefinitionBuilder.genericBeanDefinition(CronTriggerFactoryBean.class).setScope("prototype")
                    .addPropertyReference("jobDetail", key + "JobDetail")
                    .addPropertyValue("cronExpression", qrtzTimedTask.getTaskExpres())
                    .getRawBeanDefinition());
        }
        return null;
    }

    @Bean
    @Resource
    @Order(3)
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource,
                                                     QuartzProperties quartzProperties,
                                                     @Qualifier("quartzPlaceholder") PropertyPlaceholder quartzPlaceholder) {
        List<Trigger> triggers = new ArrayList<Trigger>();
        for (String key : taskExecutorMap.keySet()) {
            Trigger trigger = (Trigger) SpringContextUtils.getContext().getBean(key + "Trigger");
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
    @Qualifier("quartzPlaceholder")
    @Order(0)
    public PropertyPlaceholder propertyPlaceholder() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder();
        propertyPlaceholder.setLocation(new ClassPathResource("quartz.properties"));
        return propertyPlaceholder;
    }

    @Bean
    public QuartzRepository quartzRepository(PropertyPlaceholder propertyPlaceholder,
                                             JdbcOperations jdbcOperations,
                                             PlatformTransactionManager transactionManager) {
        Object tablePrefixObj = propertyPlaceholder.getProperty("org.quartz.jobStore.tablePrefix");
        String tablePrefix = ObjectUtils.isEmpty(tablePrefixObj) ? "qrtz_" : tablePrefixObj.toString();
        Object tableSuffixObj = propertyPlaceholder.getProperty("org.quartz.jobStore.tableSuffix");
        String tableSuffix = ObjectUtils.isEmpty(tableSuffixObj) ? "" : tableSuffixObj.toString();
        return new QuartzRepository(jdbcOperations, transactionManager) //
                .setTablePrefix(tablePrefix)    //
                .setTableSuffix(tableSuffix);
    }

    @Bean
    public ScheduleUtil scheduleUtil(QuartzProperties quartzProperties,
                                     Scheduler scheduler) {
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

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<String, QrtzTimedTask> getTaskExecutors(QuartzRepository quartzRepository) {
        Map<String, QrtzTimedTask> taskExecutorMap = new LinkedHashMap<String, QrtzTimedTask>();
        List<QrtzTimedTask> qrtzTimedTasks = quartzRepository.queryValidTask(STATUS.VALID);
        for (QrtzTimedTask qrtzTimedTask : qrtzTimedTasks) {
            List<QrtzTimedTaskParam> taskParamTds = quartzRepository.queryTaskParam(qrtzTimedTask.getTaskName());
            qrtzTimedTask.setQrtzTimedTaskParams(taskParamTds);
            taskExecutorMap.put(qrtzTimedTask.getTaskName(), qrtzTimedTask);
        }
        if (taskExecutorMap.isEmpty()) {
            return getDefaultTask();
        }
        return taskExecutorMap;
    }

    private Map<String, Object> buildParams(QrtzTimedTask qrtzTimedTask) {
        Map<String, Object> param = new LinkedHashMap<String, Object>();
        List<QrtzTimedTaskParam> taskParams = qrtzTimedTask.getQrtzTimedTaskParams();
        if (!taskParams.isEmpty()) {
            for (QrtzTimedTaskParam taskParam : taskParams) {
                String paramKey = taskParam.getParamKey();
                String paramValue = taskParam.getParamValue();
                String paramType = taskParam.getParamType();
                Object value = paramValue;
                if (!StringUtils.isEmpty(paramType)) {
                    value = BeanUtil.getPrimitiveValue(paramValue, paramType);
                }
                param.put(paramKey, value);
            }
        }
        return param;
    }

    private String buildTargetObject(QrtzTimedTask qrtzTimedTask) {
        String taskInterFace = qrtzTimedTask.getTaskClass();
        int tag = taskInterFace.lastIndexOf(".");
        String beanName = null;
        if (tag > 0) {
            String interFaceName = taskInterFace.substring(tag + 1);
            if (interFaceName.startsWith("I") && interFaceName.endsWith("SV")) {
                String taskImpl = interFaceName.substring(interFaceName.indexOf("I") + 1) + "Impl";
                beanName = ClassUtil.getClassName(taskImpl);
            } else {
                beanName = ClassUtil.getClassName(interFaceName);
            }
        } else {
            beanName = taskInterFace;
        }
        return beanName;
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

    private Map<String, QrtzTimedTask> getDefaultTask() {
        Map<String, QrtzTimedTask> map = new LinkedHashMap<String, QrtzTimedTask>();
        String defaultScheduler = "defaultScheduler";
        map.put(defaultScheduler, new QrtzTimedTask()
                .setTaskName(defaultScheduler)
                .setTaskClass("com.quartz.config.QuartzAutoConfiguration.DefaultScheduler")
                .setTaskExpres("0 0 4 ? * *")
                .setTaskMethod("execute")
                .setTaskDesc("default scheduler")
                .setTaskGroup(TriggerKey.DEFAULT_GROUP)
        );
        return map;
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
