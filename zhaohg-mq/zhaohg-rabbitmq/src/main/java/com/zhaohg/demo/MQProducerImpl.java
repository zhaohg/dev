package com.zhaohg.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhaohg on 2017-10-26.
 * 说明：convertAndSend：将Java对象转换为消息发送到匹配Key的交换机中Exchange，由于配置了JSON转换，
 * 这里是将Java对象转换成JSON字符串的形式。
 * 原文：Convert a Java object to an Amqp Message and send it to a default exchange with a specific routing key.
 */
@Service
public class MQProducerImpl implements MQProducer {
    private final static Logger       LOGGER = LoggerFactory.getLogger(MQProducerImpl.class);
    com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    @Autowired
    private              AmqpTemplate amqpTemplate;

    /* (non-Javadoc)
     * @see com.stnts.tita.rm.api.mq.MQProducer#sendDataToQueue(java.lang.String, java.lang.com.zhaohg.Object)
     */
    @Override
    public void sendDataToQueue(String queueKey, Object object) {
        try {
            amqpTemplate.convertAndSend(queueKey, object);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }

    }
}