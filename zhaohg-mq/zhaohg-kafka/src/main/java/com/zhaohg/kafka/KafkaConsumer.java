package com.zhaohg.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

/**
 * Created by zhaohg on 2017/3/24.
 */


public class KafkaConsumer implements MessageListener<Integer, String> {

    @Override
    public void onMessage(ConsumerRecord<Integer, String> record) {
        System.out.println(record);
    }

}