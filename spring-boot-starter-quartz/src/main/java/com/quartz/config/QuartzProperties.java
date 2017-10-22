package com.quartz.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 陈敏
 * Create date ：2017/10/19.
 * My blog： http://artislong.github.io
 */
@ConfigurationProperties(prefix = "quartz", ignoreInvalidFields = true)
public class QuartzProperties {

    private Boolean overwriteExistingJobs;
    private Integer startupDelay;
    private Boolean autoStartup;
    private Boolean concurrent;
    private Boolean durability;
    private Boolean volatility;
    private Boolean shouldRecover;

    public Boolean getOverwriteExistingJobs() {
        return overwriteExistingJobs;
    }

    public void setOverwriteExistingJobs(Boolean overwriteExistingJobs) {
        this.overwriteExistingJobs = overwriteExistingJobs;
    }

    public Integer getStartupDelay() {
        return startupDelay;
    }

    public void setStartupDelay(Integer startupDelay) {
        this.startupDelay = startupDelay;
    }

    public Boolean getAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(Boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    public Boolean getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Boolean concurrent) {
        this.concurrent = concurrent;
    }

    public Boolean getDurability() {
        return durability;
    }

    public void setDurability(Boolean durability) {
        this.durability = durability;
    }

    public Boolean getVolatility() {
        return volatility;
    }

    public void setVolatility(Boolean volatility) {
        this.volatility = volatility;
    }

    public Boolean getShouldRecover() {
        return shouldRecover;
    }

    public void setShouldRecover(Boolean shouldRecover) {
        this.shouldRecover = shouldRecover;
    }
}
