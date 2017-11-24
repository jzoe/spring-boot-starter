package com.quartz.model.execute;

import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;

import java.util.Map;

public class MySQLExecuter extends TaskExecuter {

    @Override
    protected String getITaskSQL(QrtzTimedTask qrtzTimedTask) {
        try {
            Map<String, Object> taskMaps = buildISQL(qrtzTimedTask);
        } catch (Exception e) {
        }

        return null;
    }

    @Override
    protected String getITaskParamSQL(QrtzTimedTaskParam qrtzTimedTaskParam) {
        return null;
    }

    @Override
    protected String getUTaskSQL(QrtzTimedTask qrtzTimedTask) {
        return null;
    }

    @Override
    protected String getUTaskParamSQL(QrtzTimedTaskParam qrtzTimedTaskParam) {
        return null;
    }
}
