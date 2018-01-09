package com.quartz.config;

import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;
import com.quartz.utils.BeanUtil;
import com.quartz.utils.SpringContextUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.quartz.model.constant.QuartzConstant.QUARTZ_DATA_KEY;

/**
 * Created by 陈敏 on 2017/11/19.
 */
public class MethodInvokingJob implements Job {
    private static final Logger logger = LoggerFactory.getLogger(StatusScheduleJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getMergedJobDataMap();
        if (jobDataMap.containsKey(QUARTZ_DATA_KEY)) {
            QrtzTimedTask qrtzTimedTask = (QrtzTimedTask) jobDataMap.get(QUARTZ_DATA_KEY);
            String taskInterFace = qrtzTimedTask.getTaskClass();
            String taskMethod = qrtzTimedTask.getTaskMethod();
            String classPath = null;
            try {
                logger.info("========" + qrtzTimedTask.getTaskClass() + "." + qrtzTimedTask.getTaskMethod() + "("+qrtzTimedTask.getTaskDesc()+")" + "开始执行========");
                Map<String, Object> param = buildParams(qrtzTimedTask);

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
                Object bean = SpringContextUtils.getContext().getBean(beanName);
                Method method = ReflectionUtils.findMethod(bean.getClass(), taskMethod, Map.class);
                ReflectionUtils.invokeMethod(method, bean, param);
            } catch (Exception e) {
                String errorMessage;
                if(e instanceof ClassNotFoundException) {
                    errorMessage = classPath + "接口不存在,具体异常信息为:";
                } else if(e instanceof NoSuchMethodException) {
                    errorMessage = classPath + "接口不存在,具体异常信息为:";
                } else {
                    errorMessage = taskMethod + "方法执行失败，具体异常信息为:";
                }
                logger.error(errorMessage, e);
            }
            logger.info("========" + qrtzTimedTask.getTaskClass() + "." + qrtzTimedTask.getTaskMethod() + "("+qrtzTimedTask.getTaskDesc()+")" + "执行结束========");
        }
    }

    private Map<String, Object> buildParams(QrtzTimedTask qrtzTimedTask) throws ClassNotFoundException {
        Map<String, Object> param = new HashMap<String, Object>();
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
}
