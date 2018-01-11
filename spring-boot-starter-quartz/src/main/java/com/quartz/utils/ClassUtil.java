package com.quartz.utils;import org.springframework.util.ReflectionUtils;import java.lang.reflect.Field;import java.lang.reflect.Method;import java.util.ArrayList;import java.util.List;/** * Created by chenmin on 17/11/19. */public class ClassUtil {    public static String getClassName(String classPath) {        return classPath.substring(0, 1).toLowerCase() + classPath.substring(1);    }    public static Object getFieldValue(Object object, String fieldName) {        Class<?> clazz = object.getClass();        Method getter = getGetter(clazz, fieldName);        if (getter == null) {            Field field = ReflectionUtils.findField(clazz, fieldName);            return ReflectionUtils.getField(field, fieldName);        } else {            return ReflectionUtils.invokeMethod(getter, object);        }    }    public static Method getGetter(Class<?> clazz, String fieldName) {        String get = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);        return ReflectionUtils.findMethod(clazz, get);    }    public static void invokeSetter(Object target, String fieldName, Object... values) {        List<Class<?>> paramTypeClasses = new ArrayList<Class<?>>();        Class<?> clazz = target.getClass();        for (Object value : values) {            paramTypeClasses.add(value.getClass());        }        String set = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);        Method setter = ReflectionUtils.findMethod(clazz, set, paramTypeClasses.toArray(new Class<?>[0]));        ReflectionUtils.invokeMethod(setter, target, values);    }}