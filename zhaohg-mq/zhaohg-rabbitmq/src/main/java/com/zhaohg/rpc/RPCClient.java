package com.zhaohg.rpc;

/**
 * Created by zhaohg on 2017/8/14.
 */

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

//RPC调用客户端
public class RPCClient {

    private Connection connection;
    private Channel    channel;
    private String     requestQueueName = "rpc_queue";
    private String     replyQueueName;

    public RPCClient() throws Exception {
        //• 先建立一个连接和一个通道，并为回调声明一个唯一的'回调'队列
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(AMQP.PROTOCOL.PORT);
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel = connection.createChannel();

        //• 注册'回调'队列，这样就可以收到RPC响应
        replyQueueName = channel.queueDeclare().getQueue();
    }

    //发送RPC请求
    public String call(String message) throws IOException, InterruptedException {

        final String corrId = UUID.randomUUID().toString();

        //发送请求消息，消息使用了两个属性：replyto和correlationId
        AMQP.BasicProperties props = new AMQP.BasicProperties.Builder().correlationId(corrId).replyTo(replyQueueName).build();
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

        final BlockingQueue<String> response = new ArrayBlockingQueue<String>(1);

        //等待接收结果
        channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //检查它的correlationId是否是我们所要找的那个
                if (properties.getCorrelationId().equals(corrId)) {
                    response.offer(new String(body, "UTF-8"));
                }
            }
        });

        return response.take();
    }

    public void close() throws IOException {
        connection.close();
    }

}