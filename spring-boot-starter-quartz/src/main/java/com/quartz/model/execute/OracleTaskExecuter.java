package com.quartz.model.execute;

import com.quartz.model.entity.QrtzTimedTask;
import com.quartz.model.entity.QrtzTimedTaskParam;
import com.quartz.utils.ClassUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class OracleTaskExecuter extends TaskExecuter {

    private static final Logger logger = LoggerFactory.getLogger(OracleTaskExecuter.class);

    @Override
    protected String getITaskSQL(QrtzTimedTask qrtzTimedTask) {
        try {
            Map<String, Object> maps = buildISQL(qrtzTimedTask);

            Object tableIdValue = ClassUtil.getFieldValue(qrtzTimedTask, this.getTableId());
            if (tableIdValue == null) {

            } else {

            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    protected String getITaskParamSQL(QrtzTimedTaskParam qrtzTimedTaskParam) {
        return null;
    }

    @Override
    protected String getUTaskSQL(QrtzTimedTask qrtzTimedTask) {
        try {
            String s = buildUSQL(qrtzTimedTask);
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected String getUTaskParamSQL(QrtzTimedTaskParam qrtzTimedTaskParam) {
        return null;
    }
}
