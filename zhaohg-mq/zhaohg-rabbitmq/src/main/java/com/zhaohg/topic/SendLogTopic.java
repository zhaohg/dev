package com.zhaohg.topic;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.UUID;

/**
 * Created by zhaohg on 2018/8/14.
 * 按topic发送接收
 * <p>
 * 使用场景：发送端不只按固定的routing key发送消息，而是按字符串“匹配”发送，接收端同样如此
 * <p>
 * 发送端和场景4的区别：
 * 1、exchange的type为topic
 * 2、发送消息的routing key不是固定的单词，而是匹配字符串，如"*.lu.#"，*匹配一个单词，#匹配0个或多个单词。
 * <p>
 * 接收端和场景4的区别：
 * 1、exchange的type为topic
 * 2、接收消息的routing key不是固定的单词，而是匹配字符串。
 */

public class SendLogTopic {
    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] args) throws Exception {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(AMQP.PROTOCOL.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明转发器
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        //定义绑定键
        String[] routing_keys = new String[]{"kernel.info", "cron.warning", "auth.info", "kernel.critical"};
        for (String routing_key : routing_keys) {
            //发送4条不同绑定键的消息
            String msg = UUID.randomUUID().toString();
            channel.basicPublish(EXCHANGE_NAME, routing_key, null, msg.getBytes());
            System.out.println(" [x] Sent routingKey = " + routing_key + " ,msg = " + msg + ".");
        }

        channel.close();
        connection.close();
    }

}
