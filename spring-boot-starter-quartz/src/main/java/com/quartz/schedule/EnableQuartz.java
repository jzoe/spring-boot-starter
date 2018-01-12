package com.quartz.schedule;

import com.quartz.config.QuartzAutoConfiguration;
import com.quartz.config.QuartzBeanConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QuartzBeanConfiguration.class, QuartzAutoConfiguration.class})
public @interface EnableQuartz {
}
