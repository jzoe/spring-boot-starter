package com.github.quartz.examples;

import com.quartz.utils.StringUtil;
import org.junit.Test;

public class QuartzApplicationTests {
    @Test
    public void test01() {
        System.out.println(StringUtil.underscoreName("taskName"));
        System.out.println(StringUtil.camelCaseName("task_name"));
    }
}
