package com.zhaohg.kafka;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * kafka读写测试类
 * @author zhaohg
 * @since
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:kafka-producer.xml"})
public class KafkaTest {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;

    /**
     * 向kafka里写数据.<br/>
     */
    @Test
    public void testTemplateSend() {
        kafkaTemplate.sendDefault("haha111");
    }

}