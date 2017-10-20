package com.fndsoft.druid.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by 陈敏 on 2017/5/17.
 */
@Configuration
@EnableConfigurationProperties(DruidDataSourceProperties.class)
@ConditionalOnClass(DruidDataSourceProperties.class)
@ConditionalOnProperty(prefix = "spring.datasource.druid", value = "enabled", matchIfMissing = true)
public class DruidAutoConfiguration {
}
