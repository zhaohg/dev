package com.zhaohg.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaohg on 2017-10-26.
 */
//TestQueue.java
@RunWith(value = SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-mq.xml"})
public class TestQueue {
    final String queue_key = "test_queue_key";
    @Autowired
    MQProducer mqProducer;

    @Test
    public void send() {
        Map<String, Object> msg = new HashMap<>();
        msg.put("data", "hello,rabbmitmq!");
        mqProducer.sendDataToQueue(queue_key, msg);
    }
}