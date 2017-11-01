package com.fndsoft.druid.config;

import com.alibaba.druid.filter.logging.LogFilter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 陈敏 on 2017/5/16.
 */
@Configuration
public class DruidDataSourceConfig {

    @Bean
    @Resource
    public ServletRegistrationBean druidServlet(Environment environment) {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "druid.");
        Map<String, Object> subProperties = propertyResolver.getSubProperties("control.");
        if(ObjectUtils.isEmpty(subProperties)) {
            reg.addInitParameter("loginUsername", "root");
            reg.addInitParameter("loginPassword", "root");
            reg.addInitParameter("resetEnable","false");
        } else {
            for(Map.Entry<String, Object> entry : subProperties.entrySet()) {
                reg.addInitParameter(entry.getKey(), entry.getValue().toString());
            }
        }
        return reg;
    }

    @Bean
    public FilterRegistrationBean druidWebStatFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        //添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid2/*");
        return filterRegistrationBean;
    }

    @Bean
    public LogFilter logfilterRegistrationBean() {
        LogFilter filter =  new Slf4jLogFilter();
        filter.setStatementExecutableSqlLogEnable(true);
        return filter;
    }
}