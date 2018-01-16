package com.github.quartz.examples;

import com.github.quartz.jdbc.QuartzRepository;
import com.github.quartz.model.entity.QrtzTimedTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QuartzApplicationTests {

    @Autowired
    private QuartzRepository quartzRepository;
    @Autowired
    private JdbcOperations jdbcOperations;

    @Test
    public void test01() {
        List<QrtzTimedTask> execute = quartzRepository.execute("select * from qrtz_timed_task t where t.task_name like ?",
                new Object[]{"%01%"});
        System.out.println(execute);
    }

    @Test
    public void test02() throws Exception {
        List<String> ipAddress = new ArrayList<String>();
        ipAddress.add("192.168.5.248");
        ipAddress.add("192.168.6.248");
        ipAddress.add("192.168.7.248");
        for (int i = 0; i < 10; i++) {
            Random random = new Random();
            int number = random.nextInt(ipAddress.size());
            System.out.println(ipAddress.get(number));
        }
    }
}
