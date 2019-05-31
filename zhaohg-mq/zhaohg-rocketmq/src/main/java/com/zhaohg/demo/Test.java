package com.zhaohg.demo;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * Created by zhaohg on 2017-10-26.
 */
public class Test {

    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
//      DefaultMQProducer product = (DefaultMQProducer) Application.applicationContext.getBean("rocketmqProduct");
        DefaultMQProducer product = new DefaultMQProducer("rocketMqProduct");
        Message msg = new Message("PushTopic", "push", "rocketmq for demo3.".getBytes());
        SendResult result = product.send(msg);
        System.out.println("id:" + result.getMsgId() + " result:" + result.getSendStatus());
    }
}
