package com.zhaohg.routing;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Random;
import java.util.UUID;

/**
 * Created by zhaohg on 2018/8/14.
 * 按路线发送接收
 * 使用场景：发送端按routing key发送消息，不同的接收端按不同的routing key接收消息
 * <p>
 * 发送端和场景3的区别：
 * 1、exchange的type为direct
 * 2、发送消息的时候加入了routing key
 * <p>
 * 接收端和场景3的区别：
 * 在绑定queue和exchange的时候使用了routing key，即从该exchange上只接收routing key指定的消息。
 */

public class SendLogDirect {

    //交换名称
    private static final String   EXCHANGE_NAME = "ex_logs_direct";
    //日志分类
    private static final String[] SEVERITIES    = {"info", "warning", "error"};

    public static void main(String[] args) throws Exception {
        //创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        // 指定用户 密码
        factory.setUsername("guest");
        factory.setPassword("guest");
        // 指定端口
        factory.setPort(AMQP.PROTOCOL.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        // 声明转发器的类型
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");

        //随机发送6条随机类型（routing key）的日志给转发器
        for (int i = 0; i < 6; i++) {
            String severity = getSeverity();
            String message = severity + "_log :" + UUID.randomUUID().toString();
            // 发布消息至转发器，指定routingkey
            channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes());
            System.out.println(" [x] Sent '" + message + "'");
        }

        channel.close();
        connection.close();
    }

    /**
     * 随机产生一种日志类型
     * @return
     */
    private static String getSeverity() {
        Random random = new Random();
        int ranVal = random.nextInt(3);
        return SEVERITIES[ranVal];
    }

}