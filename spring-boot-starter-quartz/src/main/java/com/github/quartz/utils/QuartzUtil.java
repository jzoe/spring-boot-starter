package com.github.quartz.utils;import com.github.quartz.config.BeanInvokingJobDetailFactoryBean;import com.github.quartz.config.QuartzProperties;import com.github.quartz.exception.BusinessException;import com.github.quartz.model.entity.QrtzTimedTask;import com.github.quartz.model.entity.QrtzTimedTaskParam;import org.quartz.*;import org.springframework.beans.factory.BeanFactory;import org.springframework.beans.factory.InitializingBean;import org.springframework.beans.factory.support.BeanDefinitionBuilder;import org.springframework.beans.factory.support.DefaultListableBeanFactory;import org.springframework.context.ApplicationContext;import org.springframework.scheduling.quartz.CronTriggerFactoryBean;import org.springframework.util.ObjectUtils;import org.springframework.util.StringUtils;import java.util.Collection;import java.util.LinkedHashMap;import java.util.List;import java.util.Map;import static com.github.quartz.model.constant.QuartzConstant.JOB_DATA_MAP_KEY;/** * @DESCRIPTION: * @AUTHER: chenmin * @CREATE BY: 18/1/12 下午3:19 */public class QuartzUtil implements InitializingBean {    private static DefaultListableBeanFactory configurableBeanFactory;    private static QuartzProperties quartzProperties;    private static ApplicationContext applicationContext;    public static void createJobDetailBeans(Collection<QrtzTimedTask> qrtzTimedTasks) {        for (QrtzTimedTask qrtzTimedTask : qrtzTimedTasks) {            createJobDetailBean(qrtzTimedTask);        }    }    public static void createJobDetailBean(QrtzTimedTask qrtzTimedTask) {        Map<String, Object> params = buildParams(qrtzTimedTask);        String targetObject = buildTargetObject(qrtzTimedTask);        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(BeanInvokingJobDetailFactoryBean.class).setScope("prototype");        if (!ObjectUtils.isEmpty(qrtzTimedTask.getTaskGroup())) {            beanDefinitionBuilder.addPropertyValue("group", qrtzTimedTask.getTaskGroup());        }        String beanName = qrtzTimedTask.getTaskName() + "JobDetail";        QuartzUtil.configurableBeanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder                .addPropertyValue("targetMethod", qrtzTimedTask.getTaskMethod())                .addPropertyValue("targetBean", targetObject)                .addPropertyValue("durable", QuartzUtil.quartzProperties.getDurability())                .addPropertyValue("shouldRecover", QuartzUtil.quartzProperties.getShouldRecover())                .addPropertyValue("volatility", QuartzUtil.quartzProperties.getVolatility())                .addPropertyValue("concurrent", QuartzUtil.quartzProperties.getConcurrent())                .addPropertyValue("arguments", new Object[]{params})                .getRawBeanDefinition());    }    public static void createCronTriggerBeans(Collection<QrtzTimedTask> qrtzTimedTasks) {        for (QrtzTimedTask qrtzTimedTask : qrtzTimedTasks) {            createCronTriggerBean(qrtzTimedTask);        }    }    public static void createCronTriggerBean(QrtzTimedTask qrtzTimedTask) {        JobDataMap jobDataMap = new JobDataMap();        jobDataMap.put(JOB_DATA_MAP_KEY, qrtzTimedTask);        String key = qrtzTimedTask.getTaskName();        QuartzUtil.configurableBeanFactory.registerBeanDefinition(key + "Trigger",                BeanDefinitionBuilder.genericBeanDefinition(CronTriggerFactoryBean.class).setScope("prototype")                        .addPropertyReference("jobDetail", key + "JobDetail")                        .addPropertyValue("cronExpression", qrtzTimedTask.getTaskExpres())                        .addPropertyValue("jobDataMap", jobDataMap)                        .getRawBeanDefinition());    }    public static void startSchedule(Scheduler scheduler, QrtzTimedTask qrtzTimedTask) throws SchedulerException {        Trigger trigger = (Trigger) QuartzUtil.applicationContext.getBean(qrtzTimedTask.getTaskName() + "Trigger");        JobDetail jobDetail = (JobDetail) QuartzUtil.applicationContext.getBean(qrtzTimedTask.getTaskName() + "JobDetail");        scheduler.scheduleJob(jobDetail, trigger);    }    public static Map<String, Object> buildParams(QrtzTimedTask qrtzTimedTask) {        Map<String, Object> param = new LinkedHashMap<String, Object>();        List<QrtzTimedTaskParam> taskParams = qrtzTimedTask.getQrtzTimedTaskParams();        if (!taskParams.isEmpty()) {            for (QrtzTimedTaskParam taskParam : taskParams) {                String paramKey = taskParam.getParamKey();                String paramValue = taskParam.getParamValue();                String paramType = taskParam.getParamType();                Object value = paramValue;                if (!StringUtils.isEmpty(paramType)) {                    value = BeanUtil.getPrimitiveValue(paramValue, paramType);                }                param.put(paramKey, value);            }        }        return param;    }    public static String buildTargetObject(QrtzTimedTask qrtzTimedTask) {        String taskInterFace = qrtzTimedTask.getTaskClass();        int tag = taskInterFace.lastIndexOf(".");        String beanName = null;        if (tag > 0) {            String interFaceName = taskInterFace.substring(tag + 1);            if (interFaceName.startsWith("I") && interFaceName.endsWith("SV")) {                String taskImpl = interFaceName.substring(interFaceName.indexOf("I") + 1) + "Impl";                beanName = ClassUtil.getClassName(taskImpl);            } else {                beanName = ClassUtil.getClassName(interFaceName);            }        } else {            beanName = taskInterFace;        }        return beanName;    }    public static boolean quartzIsStart(QuartzProperties quartzProperties) {        List<String> excludeServers = quartzProperties.getExcludeServers();        if (!excludeServers.isEmpty()) {            if (excludeServers.contains(HttpUtil.getIpAddress())) {                return false;            }        }        return true;    }    public QuartzUtil setConfigurableBeanFactory(BeanFactory beanFactory) {        QuartzUtil.configurableBeanFactory = (DefaultListableBeanFactory) beanFactory;        return this;    }    public QuartzUtil setQuartzProperties(QuartzProperties quartzProperties) {        QuartzUtil.quartzProperties = quartzProperties;        return this;    }    public QuartzUtil setApplicationContext(ApplicationContext applicationContext) {        QuartzUtil.applicationContext = applicationContext;        return this;    }    @Override    public void afterPropertiesSet() throws Exception {        if (ObjectUtils.isEmpty(quartzProperties)) {            throw new BusinessException("quartzProperties can not be null");        }        if (ObjectUtils.isEmpty(applicationContext)) {            throw new BusinessException("applicationContext can not be null");        }        if (ObjectUtils.isEmpty(configurableBeanFactory)) {            throw new BusinessException("configurableBeanFactory can not be null");        }    }}