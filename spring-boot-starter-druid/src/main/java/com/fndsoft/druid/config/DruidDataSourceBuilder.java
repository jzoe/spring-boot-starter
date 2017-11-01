package com.fndsoft.druid.config;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.jdbc.DatabaseDriver;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class DruidDataSourceBuilder {
    private static final String[] DATA_SOURCE_TYPE_NAMES = new String[] {
            "org.apache.tomcat.jdbc.pool.DataSource",
            "com.zaxxer.hikari.HikariDataSource",
            "org.apache.commons.dbcp.BasicDataSource",
            "org.apache.commons.dbcp2.BasicDataSource",
            "com.alibaba.druid.pool.DruidDataSource"};

    private Class<? extends DataSource> type;

    private ClassLoader classLoader;

    private Map<String, Object> properties = new HashMap<String, Object>();

    public static DruidDataSourceBuilder create() {
        return new DruidDataSourceBuilder(null);
    }

    public static DruidDataSourceBuilder create(ClassLoader classLoader) {
        return new DruidDataSourceBuilder(classLoader);
    }

    public DruidDataSourceBuilder(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public DataSource build() {
        Class<? extends DataSource> type = getType();
        DataSource result = BeanUtils.instantiate(type);
        maybeGetDriverClassName();
        bind(result);
        return result;
    }

    private void maybeGetDriverClassName() {
        if (!this.properties.containsKey("driverClassName")
                && this.properties.containsKey("url")) {
            String url = this.properties.get("url").toString();
            String driverClass = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
            this.properties.put("driverClassName", driverClass);
        }
    }

    private void bind(DataSource result) {
        MutablePropertyValues properties = new MutablePropertyValues(this.properties);
        new RelaxedDataBinder(result).withAlias("url", "jdbcUrl")
                .withAlias("username", "user").bind(properties);
    }

    public DruidDataSourceBuilder type(Class<? extends DataSource> type) {
        this.type = type;
        return this;
    }

    public DruidDataSourceBuilder url(String url) {
        this.properties.put("url", url);
        return this;
    }

    public DruidDataSourceBuilder driverClassName(String driverClassName) {
        this.properties.put("driverClassName", driverClassName);
        return this;
    }

    public DruidDataSourceBuilder username(String username) {
        this.properties.put("username", username);
        return this;
    }

    public DruidDataSourceBuilder password(String password) {
        this.properties.put("password", password);
        return this;
    }

    public DruidDataSourceBuilder setProperties(Map<String, Object> properties) {
        this.properties = properties;
        return this;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends DataSource> findType() {
        if (this.type != null) {
            return this.type;
        }
        for (String name : DATA_SOURCE_TYPE_NAMES) {
            try {
                return (Class<? extends DataSource>) ClassUtils.forName(name,
                        this.classLoader);
            }
            catch (Exception ex) {
                // Swallow and continue
            }
        }
        return null;
    }

    private Class<? extends DataSource> getType() {
        Class<? extends DataSource> type = findType();
        if (type != null) {
            return type;
        }
        throw new IllegalStateException("No supported DataSource type found");
    }
}
