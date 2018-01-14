package com.github.quartz.examples;

import com.github.quartz.jdbc.QuartzRepository;
import com.github.quartz.model.entity.QrtzTimedTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class QuartzApplicationTests {

    @Autowired
    private QuartzRepository quartzRepository;
    @Autowired
    private JdbcOperations jdbcOperations;

    @Test
    public void test01() {
        List<QrtzTimedTask> qrtzTimedTaskList = quartzRepository.queryTaskAll();
        System.out.println(qrtzTimedTaskList);
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
