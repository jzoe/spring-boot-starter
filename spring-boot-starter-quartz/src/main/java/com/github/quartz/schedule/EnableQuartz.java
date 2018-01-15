package com.github.quartz.schedule;

import com.github.quartz.config.QuartzAutoConfiguration;
import com.github.quartz.config.QuartzBeanConfiguration;
import com.github.quartz.config.QuartzDataBaseConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启完整的Quartz自动配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QuartzBeanConfiguration.class, QuartzDataBaseConfiguration.class, QuartzAutoConfiguration.class})
public @interface EnableQuartz {
}
