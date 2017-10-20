package com.fndsoft.druid.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Created by 陈敏 on 2017/3/31.
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        /**
         * DynamicDataSourceContextHolder代码中使用setDataSource
         * 设置当前的数据源，在路由类中使用getDataSource进行获取，
         * 交给AbstractRoutingDataSource进行注入使用。
         */
        return DynamicDataSourceContextHolder.getDataSource();
    }
}
