package com.github.quartz.examples;

import com.quartz.jdbc.QuartzRepository;
import com.quartz.model.entity.QrtzTimedTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuartzApplicationTests {

    @Autowired
    private QuartzRepository quartzRepository;
    @Autowired
    private JdbcOperations jdbcOperations;

    @Test
    public void test01() {
        List<QrtzTimedTask> query = jdbcOperations.query("select * from qrtz_timed_task", new BeanPropertyRowMapper<>(QrtzTimedTask.class));
        System.out.println(query);
    }

    @Test
    public void test02() throws Exception {
    }
}
