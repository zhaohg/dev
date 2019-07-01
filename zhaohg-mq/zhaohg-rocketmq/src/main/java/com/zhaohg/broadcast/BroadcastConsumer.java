package com.zhaohg.broadcast;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * Created by zhaohg on 2018-10-26.
 */
public class BroadcastConsumer {
    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("example_group_name");
        //consumer.setNamesrvAddr("192.168.100.145:9876;192.168.100.146:9876");
        consumer.setConsumeMessageBatchMaxSize(10);//每次拉取10条
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //set to broadcast mode
        consumer.setMessageModel(MessageModel.BROADCASTING);// 广播消费

        consumer.subscribe("TopicTest", "TagA || TagC || TagD");

        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    // System.out.println("msgs的长度" + msgs.size());
                    System.out.printf(Thread.currentThread().getName() + " Receive New Messages: " + msgs + "%n");
                    for (MessageExt msg : msgs) {
                        String msgBody = new String(msg.getBody(), "utf-8");
                        if (msgBody.equals("Hello RocketMQ 4")) {
                            System.out.println("======错误=======");
                            int a = 1 / 0;
                        }
                    }

                } catch (Exception e) {//exception的情况，一般重复16次 10s、30s、1分钟、2分钟、3分钟等等
                    e.printStackTrace();
                    if (msgs.get(0).getReconsumeTimes() == 3) {
                        //记录日志
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;// 成功
                    } else {
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;// 重试
                    }
                }

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;// 成功
            }
        });

        consumer.start();
        System.out.printf("Broadcast Consumer Started.%n");
    }
}