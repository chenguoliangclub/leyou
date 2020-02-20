package com.leyou.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.HashMap;
import java.util.Map;


@SpringBootTest
@RunWith(SpringRunner.class)
public class SmsTest {
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void testSend(){
        Map<String,String> msg = new HashMap<>();
        msg.put("phone","13880678698");
        msg.put("code","464646");
        amqpTemplate.convertAndSend("ly.sms.exchange","sms.verify.code",msg);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
