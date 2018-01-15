package com.github.quartz.schedule;

import com.github.quartz.config.QuartzBeanConfiguration;
import com.github.quartz.config.QuartzDataBaseConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 只开启相关Bean的自动配置，不开启Quartz的配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QuartzBeanConfiguration.class, QuartzDataBaseConfiguration.class})
public @interface EnableQuartzDataBase {
}
