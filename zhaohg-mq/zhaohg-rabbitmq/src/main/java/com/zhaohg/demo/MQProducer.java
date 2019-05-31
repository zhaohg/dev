package com.zhaohg.demo;

/**
 * Created by zhaohg on 2017-10-26.
 */
public interface MQProducer {
    /**
     * 发送消息到指定队列
     * @param queueKey
     * @param object
     */
    public void sendDataToQueue(String queueKey, Object object);
}