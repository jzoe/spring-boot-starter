package com.github.quartz.examples;

import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.utils.StringUtil;
import org.junit.Test;
import org.springframework.util.ObjectUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class QuartzApplicationTests {
    @Test
    public void test01() {
        System.out.println(StringUtil.underscoreName("taskName"));
        System.out.println(StringUtil.camelCaseName("task_name"));
    }

    @Test
    public void test02() throws Exception {
        QrtzTimedTask qrtzTimedTask = new QrtzTimedTask();
        qrtzTimedTask.setTaskName("test");
        qrtzTimedTask.setTaskDesc("gaga");
        qrtzTimedTask.setTaskExpres("0/5 * * * * ?");
        qrtzTimedTask.setTaskClass("com.Test");
        qrtzTimedTask.setTaskMethod("test");
        qrtzTimedTask.setCreateTime(new Date());
        qrtzTimedTask.setTaskId(10001L);
        String s = build(qrtzTimedTask);
        System.out.println(s);
    }

    protected String build(Object obj) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        StringBuilder sb = new StringBuilder();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 过滤class属性
            if (!key.equals("class")) {
                String columnName = StringUtil.underscoreName(key);
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                if (getter == null) {
                    continue;
                }
                Object value = getter.invoke(obj);
                if (!ObjectUtils.isEmpty(value)) {
                    if (value instanceof Date) {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                        value = "DATE'" + value + "'";
                    }
                    if (value != null) {
                        sb.append(columnName + " = '" + value.toString() + "'");
                        sb.append(",");
                    }
                }
            }
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf(","));
    }
}
