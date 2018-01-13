package com.github.quartz.schedule;

import com.github.quartz.config.QuartzAutoConfiguration;
import com.github.quartz.config.QuartzBeanConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QuartzBeanConfiguration.class, QuartzAutoConfiguration.class})
public @interface EnableQuartz {
}
