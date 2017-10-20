package com.fndsoft.druid.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.JdbcUtils;
import com.fndsoft.druid.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by 陈敏 on 2017/3/31.
 * 动态数据源注册
 */
public class DynamicDataSourceRegister implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceRegister.class);

    // 默认数据连接池
    public static final Object DATASOURCE_TYPE_DEFAULT = "com.alibaba.druid.pool.DruidDataSource";

    private ConversionService conversionService = new DefaultConversionService();
    private PropertyValues dataSourcePropertyValues;
    private Class<? extends DataSource> dataSourceType;
    private String filters;
    // 默认数据源
    private DataSource defaultDataSource;
    private DruidDataSource druidDataSource;

    private Map<String, DataSource> dataSourceMaps = new HashMap<String, DataSource>();

    /**
     * 加载多数据源配置
     * @param environment
     */
    public void setEnvironment(Environment environment) {
        initDruidDataSource(environment);
        initDefaultDataSource(environment);
    }

    private void initDruidDataSource(Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
        Map<String, Object> druidMap = propertyResolver.getSubProperties("druid" + ".");
//        Properties properties = new Properties();
//        for(Map.Entry<String, Object> entry : publicDruidConfig.entrySet()) {
//            properties.put("druid." + entry.getKey(), entry.getValue().toString());
//        }
//        druidDataSource.configFromPropety(properties);
//        druidDataSource.setInitialSize(publicDruidConfig.get("initialSize") == null?5:Integer.valueOf(publicDruidConfig.get("initialSize").toString()));
//        druidDataSource.setMinIdle(publicDruidConfig.get("minIdle") == null?5:Integer.valueOf(publicDruidConfig.get("minIdle").toString()));
//        druidDataSource.setMaxActive(publicDruidConfig.get("maxActive") == null?20:Integer.valueOf(publicDruidConfig.get("maxActive").toString()));
//        druidDataSource.setMaxWait(publicDruidConfig.get("maxWait") == null?60000L: Long.valueOf(publicDruidConfig.get("maxWait").toString()));

        druidDataSource = new DruidDataSource();
        druidDataSource.setInitialSize(druidMap.get("initialSize") == null?5:Integer.valueOf(druidMap.get("initialSize").toString()));
        druidDataSource.setMinIdle(druidMap.get("minIdle") == null?5:Integer.valueOf(druidMap.get("minIdle").toString()));
        druidDataSource.setMaxActive(druidMap.get("maxActive") == null?20:Integer.valueOf(druidMap.get("maxActive").toString()));
        druidDataSource.setMaxWait(druidMap.get("maxWait") == null?60000L: Long.valueOf(druidMap.get("maxWait").toString()));
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidMap.get("timeBetweenEvictionRunsMillis") == null?60000L:Long.valueOf(druidMap.get("timeBetweenEvictionRunsMillis").toString()));
        druidDataSource.setMinEvictableIdleTimeMillis(druidMap.get("minEvictableIdleTimeMillis") == null?300000L: Long.valueOf(druidMap.get("minEvictableIdleTimeMillis").toString()));
        druidDataSource.setValidationQuery(druidMap.get("validationQuery") == null?"SELECT 1 FROM DUAL":druidMap.get("validationQuery").toString());
        druidDataSource.setTestWhileIdle(druidMap.get("testWhileIdle") == null?true:Boolean.getBoolean(druidMap.get("testWhileIdle").toString()));
        druidDataSource.setTestOnBorrow(druidMap.get("testOnBorrow") == null?false:Boolean.getBoolean(druidMap.get("testOnBorrow").toString()));
        druidDataSource.setTestOnReturn(druidMap.get("testOnReturn") == null?false:Boolean.getBoolean(druidMap.get("testOnReturn").toString()));
        druidDataSource.setPoolPreparedStatements(druidMap.get("poolPreparedStatements") == null?true:Boolean.getBoolean(druidMap.get("poolPreparedStatements").toString()));
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidMap.get("maxPoolPreparedStatementPerConnectionSize") == null?20:Integer.valueOf(druidMap.get("maxPoolPreparedStatementPerConnectionSize").toString()));
        try {
            // filters为空，druid数据源没有加密，采用普通的配置方式
            filters = druidMap.get("filters") == null?"static,wall,log4j":druidMap.get("filters").toString();
            druidDataSource.setFilters(filters);
        } catch (SQLException e) {
            logger.error("配置监控统计拦截的filters失败");
        }
        druidDataSource.setUseGlobalDataSourceStat(druidMap.get("useGlobalDataSourceStat") == null?true:Boolean.getBoolean(druidMap.get("useGlobalDataSourceStat").toString()));
    }

    /**
     * 初始化默认数据源
     * @param environment
     */
    private void initDefaultDataSource(Environment environment) {
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
        Map<String, Object> dataSourceMap = new HashMap<String, Object>();
        String dataSourceDriver = null;
        try {
            if(propertyResolver.getProperty("type") == null) {
                dataSourceDriver = DATASOURCE_TYPE_DEFAULT.toString();
            } else {
                dataSourceDriver = propertyResolver.getProperty("type");
            }
            dataSourceType = (Class<? extends DataSource>)Class.forName(dataSourceDriver);
        } catch (ClassNotFoundException e) {
            logger.info(dataSourceDriver + "未找到，错误信息:{}" ,e.getMessage());
            return;
        }

        // 创建数据源
        String jndi = propertyResolver.getProperty("jndi-name");
        if(!StringUtils.isEmpty(jndi)) {
            buildJndiDataSource(jndi);
        } else {
            dataSourceMap.put("driverClassName", propertyResolver.getProperty("driverClassName"));
            dataSourceMap.put("url", propertyResolver.getProperty("url"));
            dataSourceMap.put("username", propertyResolver.getProperty("username"));
            dataSourceMap.put("password", propertyResolver.getProperty("password"));
            dataSourceMap.put("connectionProperties", propertyResolver.getProperty("connectionProperties")==null?"druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000":propertyResolver.getProperty("connectionProperties"));
            defaultDataSource = buildDataSource(dataSourceMap);
            dataBinder(defaultDataSource, environment);

            initDataSources(environment);
        }
    }

    private void buildJndiDataSource(String jndi) {
        String[] jndiNames = jndi.split(",");
        for(int i = 0; i < jndiNames.length; i++) {
            String jndiName = jndiNames[1];
            String jndiKey = jndiName.substring(jndiName.lastIndexOf("/"));
            DataSource dataSource = new JndiDataSourceLookup().getDataSource(jndiName);
            dataSourceMaps.put(jndiKey, dataSource);
            if(i == 0) {
                defaultDataSource = dataSource;
            }
        }
    }

    private void initDataSources(Environment environment) {
        // 读取配置文件获取更多的数据源，也可以通过defaultDataSource读取数据库获取更多数据源
        RelaxedPropertyResolver propertyResolver = new RelaxedPropertyResolver(environment, "spring.datasource.");
        String dataSourceProfixs = propertyResolver.getProperty("names");
        if(!StringUtils.isEmpty(dataSourceProfixs)) {
            for(String dataSourceProfix : dataSourceProfixs.split(",")) {
                Map<String, Object> dataSourceMap = propertyResolver.getSubProperties(dataSourceProfix + ".");
                Map<String, Object> map = new HashMap<String, Object>(dataSourceMap);
                map.put("dataSourceProfix", dataSourceProfix);
                DataSource dataSource = buildDataSource(map);
                if(dataSource != null) {
                    dataSourceMaps.put(dataSourceProfix, dataSource);
                    dataBinder(dataSource, environment);
                }
            }
        }
    }

    public DataSource buildDataSource(Map<String, Object> dataSourceMap) {
        String driverClassName = null;
        if(dataSourceMap.get("driver-class-name") == null) {
            if(dataSourceMap.get("driverClassName") == null) {
                throw new BusinessException("没有数据库驱动");
            } else {
                driverClassName = dataSourceMap.get("driverClassName").toString();
            }
        } else {
            driverClassName = dataSourceMap.get("driver-class-name").toString();
        }
        String url = dataSourceMap.get("url").toString();
        String username = dataSourceMap.get("username").toString();
        String password = dataSourceMap.get("password").toString();
        String connectionProperty = dataSourceMap.get("connectionProperties").toString();
        String[] connectionProperties = connectionProperty.split(";");
        Properties properties = new Properties();
        for(String connectionPropertie : connectionProperties) {
            String key = connectionPropertie.substring(0, connectionPropertie.indexOf("="));
            String value = connectionPropertie.substring(connectionPropertie.indexOf("=") + 1);
            properties.put(key, value);
        }
        if(dataSourceMap.containsKey("dataSourceProfix")) {
            String decryptKey = properties.getProperty("config.decrypt.key");
            String publicKey = dataSourceMap.get("decrypt.publickey").toString();
            decryptKey = decryptKey.substring(decryptKey.indexOf("{") + 1, decryptKey.lastIndexOf("}"));
            String decryptValue = "spring.datasource." + dataSourceMap.get("dataSourceProfix") + ".decrypt.publickey";
            if(decryptValue.equals(decryptKey)) {
                properties.setProperty("config.decrypt.key", publicKey);
            }
        }
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setConnectProperties(druidDataSource.getConnectProperties());
        dataSource.setInitialSize(druidDataSource.getInitialSize());
        dataSource.setMinIdle(druidDataSource.getMinIdle());
        dataSource.setMaxActive(druidDataSource.getMaxActive());
        dataSource.setMaxWait(druidDataSource.getMaxWait());
        try {
            dataSource.setFilters(filters);
        } catch (SQLException e) {
            logger.error("配置监控统计拦截的filters失败");
        }
        dataSource.setDriverClassName(driverClassName);
        dataSource.setConnectProperties(properties);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
//        try {
//            if(!dataSourceMap.containsKey("dataSourceProfix")) {
//                DruidPooledConnection connection = dataSource.getConnection();
//                logger.info(url + "数据源创建成功，连接：" + connection);
//                JdbcUtils.close(connection);
//            }
//            return dataSource;
//        } catch (SQLException e) {
//            logger.info(url + "数据源创建失败，错误信息:{}", e.getMessage());
//            return null;
//        }
    }

    private void dataBinder(DataSource dataSource, Environment environment) {
        RelaxedDataBinder dataBinder = new RelaxedDataBinder(dataSource);
        dataBinder.setConversionService(conversionService);
        dataBinder.setIgnoreNestedProperties(false);
        dataBinder.setIgnoreInvalidFields(false);
        dataBinder.setIgnoreUnknownFields(true);
        if(dataSourcePropertyValues == null) {
            Map<String, Object> subProperties = new RelaxedPropertyResolver(environment, "spring.datasource").getSubProperties(".");
            Map<String, Object> values = new HashMap<String, Object>(subProperties);
            // 排除已经设置的属性
            values.remove("driverClassName");
            values.remove("url");
            values.remove("username");
            values.remove("password");
            values.remove("connectionProperties");
            dataSourcePropertyValues = new MutablePropertyValues(values);
        }
        dataBinder.bind(dataSourcePropertyValues);
    }

    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {
        Map<String, Object> targetDataSources = new HashMap<String, Object>();
        // 将主数据源添加到更多数据源中
        targetDataSources.put("dataSource", defaultDataSource);
        DynamicDataSourceContextHolder.dataSourceIds.add("dataSource");

        // 添加更多数据源
        targetDataSources.putAll(dataSourceMaps);
        for(String key : dataSourceMaps.keySet()) {
            DynamicDataSourceContextHolder.dataSourceIds.add(key);
        }

        // 创建DynamicDataSource
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(DynamicDataSource.class);
        beanDefinition.setSynthetic(true);

        MutablePropertyValues mutablePropertyValues = beanDefinition.getPropertyValues();
        mutablePropertyValues.addPropertyValue("defaultTargetDataSource", defaultDataSource);
        mutablePropertyValues.addPropertyValue("targetDataSources", targetDataSources);
        beanDefinitionRegistry.registerBeanDefinition("dataSource", beanDefinition);
    }
}
