package com.quartz.model.execute;

import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;

import java.util.Map;

public class MySQLExecuter extends TaskExecuter {

    @Override
    protected String getITaskSQL(QrtzTimedTask qrtzTimedTask) {
        Map<String, Object> taskMaps = build(qrtzTimedTask);

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
