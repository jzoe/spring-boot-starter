package com.fndsoft.druid.annotation;

import java.lang.annotation.*;

/**
 * Created by 陈敏 on 2017/3/31.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {
    String value();
}
