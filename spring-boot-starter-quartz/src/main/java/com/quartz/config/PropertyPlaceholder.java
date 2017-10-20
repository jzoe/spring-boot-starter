package com.quartz.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import java.util.Properties;

public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {
    private Properties properties = new Properties();

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        if (!props.isEmpty()) {
            properties.putAll(props);
        }
    }

    //static method for accessing context properties
    public Object getProperty(Object name) {
        return properties.get(name);
    }

    public Properties getProperties() {
        return properties;
    }
}
