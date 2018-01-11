package com.quartz.schedule;

import com.quartz.config.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@AutoConfigurationPackage
@Import(QuartzAutoConfiguration.class)
public @interface EnableQuartz {

    Class<?>[] exclude() default {};

    String[] excludeName() default {};
}
