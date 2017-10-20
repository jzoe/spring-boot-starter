package com.quartz.config;

import com.quartz.pojo.AmsTimedTaskParamTd;
import com.quartz.pojo.AmsTimedTaskTd;
import com.quartz.utils.BeanUtil;
import org.quartz.Trigger;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.util.*;

@Configuration
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(QuartzProperties.class)
public class QuartzAutoConfiguration implements BeanFactoryAware {

    private static final Logger logger = LoggerFactory.getLogger(QuartzAutoConfiguration.class);
    private static final String SELECT_AMS_TASK = "SELECT * FROM AMS_TIMED_TASK_TD T WHERE T.STATUS = 'U'";
    private static final String SELECT_AMS_TASK_PARAM = "SELECT * FROM AMS_TIMED_TASK_PARAM_TD T WHERE T.TASK_NAME = ?";

    private BeanFactory beanFactory;

    private static Map<String, AmsTimedTaskTd> taskExecutorMap = new LinkedHashMap<String, AmsTimedTaskTd>();

    @Bean
    public List<BeanInvokingJobDetailFactoryBean> methodInvokingJobDetailFactoryBean(JdbcTemplate jdbcTemplate, QuartzProperties quartzProperties) throws Exception {
        DefaultListableBeanFactory configurableBeanFactory = (DefaultListableBeanFactory ) beanFactory;
        taskExecutorMap.putAll(getTaskExecutors(jdbcTemplate));
        for (String key: taskExecutorMap.keySet()) {
            String beanName = key + "JobDetail";
            AmsTimedTaskTd amsTimedTaskTd = taskExecutorMap.get(key);
            Map<String, Object> params = buildParams(amsTimedTaskTd);
            String targetObject = buildTargetObject(amsTimedTaskTd);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanInvokingJobDetailFactoryBean.class).setScope("prototype");
            if (!ObjectUtils.isEmpty(amsTimedTaskTd.getTaskGroup())) {
                beanDefinitionBuilder.addPropertyValue("group", amsTimedTaskTd.getTaskGroup());
            }
            configurableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder
                    .addPropertyValue("targetMethod", amsTimedTaskTd.getTaskMethod())
                    .addPropertyValue("arguments", new Object[]{params})
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
    public List<CronTriggerFactoryBean> cronTriggerFactoryBean() {
        DefaultListableBeanFactory configurableBeanFactory = (DefaultListableBeanFactory ) beanFactory;
        for (String key: taskExecutorMap.keySet()) {
            AmsTimedTaskTd amsTimedTaskTd = taskExecutorMap.get(key);
            configurableBeanFactory.registerBeanDefinition(key + "Trigger", BeanDefinitionBuilder.genericBeanDefinition(CronTriggerFactoryBean.class).setScope("prototype")
                    .addPropertyReference("jobDetail", key + "JobDetail")
                    .addPropertyValue("cronExpression", amsTimedTaskTd.getTaskExpres())
                    .getRawBeanDefinition());
        }
        return null;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(Trigger[] trigger, DataSource dataSource, QuartzProperties quartzProperties, @Qualifier("quartzPlaceholder") PropertyPlaceholder quartzPlaceholder) throws Exception {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setTriggers(trigger);
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
    public PropertyPlaceholder propertyPlaceholder() {
        PropertyPlaceholder propertyPlaceholder = new PropertyPlaceholder();
        propertyPlaceholder.setLocation(new ClassPathResource("quartz.properties"));
        return propertyPlaceholder;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private Map<String, AmsTimedTaskTd> getTaskExecutors(JdbcTemplate jdbcTemplate) {
        Map<String, AmsTimedTaskTd> taskExecutorMap = new LinkedHashMap<String, AmsTimedTaskTd>();
        List<AmsTimedTaskTd> amsTimedTaskTds = jdbcTemplate.query(SELECT_AMS_TASK, new BeanPropertyRowMapper<AmsTimedTaskTd>(AmsTimedTaskTd.class));
        for (AmsTimedTaskTd amsTimedTaskTd : amsTimedTaskTds) {
            List<AmsTimedTaskParamTd> taskParamTds = jdbcTemplate.query(SELECT_AMS_TASK_PARAM, new BeanPropertyRowMapper<AmsTimedTaskParamTd>(AmsTimedTaskParamTd.class), amsTimedTaskTd.getTaskName());
            amsTimedTaskTd.setAmsTimedTaskParamTds(taskParamTds);
            taskExecutorMap.put(amsTimedTaskTd.getTaskName(), amsTimedTaskTd);
        }
        return taskExecutorMap;
    }

    private Map<String, Object> buildParams(AmsTimedTaskTd amsTimedTaskTd) {
        Map<String, Object> param = new HashMap<String, Object>();
        List<AmsTimedTaskParamTd> taskParams = amsTimedTaskTd.getAmsTimedTaskParamTds();
        if (!taskParams.isEmpty()) {
            for (AmsTimedTaskParamTd taskParam : taskParams) {
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

    private String buildTargetObject(AmsTimedTaskTd amsTimedTaskTd) {
        String taskInterFace = amsTimedTaskTd.getTaskClass();
        String interFaceName = taskInterFace.substring(taskInterFace.lastIndexOf(".") + 1);
        String beanName = null;

        // 表中配置的是接口，则获取接口的实现类
        if (interFaceName.startsWith("I") && interFaceName.endsWith("SV")) {
            String taskImpl = interFaceName.substring(interFaceName.indexOf("I") + 1) + "Impl";
            beanName = taskImpl.substring(0, 1).toLowerCase() + taskImpl.substring(1);
        }
        // 如果是实现类，则直接反射
        if (interFaceName.endsWith("SVImpl")) {
            beanName = interFaceName.substring(0, 1).toLowerCase() + interFaceName.substring(1);
        }

        // 根据实现类类型，从Spring上下文中获取对应的Bean
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
            logger.error(path + "application.properties配置文件不存在，将使用默认配置");
            return properties;
        }
        try {
            Properties applicationProperties = new Properties();
            applicationProperties.load(new FileInputStream(application));
            properties.putAll(applicationProperties);
            logger.info("加载" + path + "application.properties" + "配置文件完成");
        } catch (Exception e) {
            logger.error("加载" + path + "application.properties" + "配置文件失败，错误信息: {}", e);
            return properties;
        }

        return properties;
    }
}
