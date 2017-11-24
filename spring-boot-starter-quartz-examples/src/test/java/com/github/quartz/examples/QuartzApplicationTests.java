package com.github.quartz.examples;

import com.quartz.model.annotation.Id;
import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.utils.ClassUtil;
import com.quartz.utils.StringUtil;
import org.junit.Test;
import org.springframework.util.ObjectUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
        Map<String, Object> maps = buildISQL(qrtzTimedTask);
        String s = buildUSQL(qrtzTimedTask);
        System.out.println(maps);
        System.out.println(s);
    }

    @Test
    public void test03() {
        QrtzTimedTask qrtzTimedTask = new QrtzTimedTask();
        qrtzTimedTask.setTaskName("test");
        qrtzTimedTask.setTaskDesc("gaga");
        qrtzTimedTask.setTaskExpres("0/5 * * * * ?");
        qrtzTimedTask.setTaskClass("com.Test");
        qrtzTimedTask.setTaskMethod("test");
        qrtzTimedTask.setCreateTime(new Date());
        qrtzTimedTask.setTaskId(10001L);
        Object fieldValue = ClassUtil.getFieldValue(qrtzTimedTask, "taskClass");
        System.out.println(fieldValue);
    }


    protected String buildUSQL(Object obj) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        StringBuilder sb = new StringBuilder();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 过滤class属性
            if (!key.equals("class")) {
                String columnName = StringUtil.underscoreName(key).toUpperCase();
                Id id = obj.getClass().getDeclaredField(key).getAnnotation(Id.class);
                if (id != null) {
                    continue;
                }
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                if (getter == null) {
                    continue;
                }
                Object value = getter.invoke(obj);
                if (!ObjectUtils.isEmpty(value)) {
                    if (value instanceof Date) {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                        sb.append(columnName + " = " + "DATE'" + value.toString() + "'");
                    } else {
                        sb.append(columnName + " = '" + value.toString() + "'");
                    }
                    sb.append(",");
                }
            }
        }
        return sb.toString().substring(0, sb.toString().lastIndexOf(","));
    }

    protected Map<String, Object> buildISQL(Object obj) throws Exception {
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (PropertyDescriptor property : propertyDescriptors) {
            String key = property.getName();
            // 过滤class属性
            if (!key.equals("class")) {
                Id id = obj.getClass().getDeclaredField(key).getAnnotation(Id.class);
                if (id != null) {
                    continue;
                }
                String columnName = StringUtil.underscoreName(key).toUpperCase();
                // 得到property对应的getter方法
                Method getter = property.getReadMethod();
                if (getter == null) {
                    continue;
                }
                Object value = getter.invoke(obj);
                if (!ObjectUtils.isEmpty(value)) {
                    columns.append(columnName + ",");
                    if (value instanceof Date) {
                        value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value);
                        values.append("DATE'" + value.toString() + "'");
                    } else {
                        values.append("'" + value.toString() + "'");
                    }
                    values.append(",");
                }
            }
        }
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("COLUMNS", StringUtil.remove(columns.toString(),","));
        maps.put("VALUES", StringUtil.remove(values.toString(), ","));
        return maps;
    }
}
