package com.quartz.schedule;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Created by 陈敏 on 2017/11/19.
 * 有状态Job
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class StatusScheduleJob extends MethodInvokingJob {
}
