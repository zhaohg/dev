package com.zhaohg.demo;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * Created by zhaohg on 2017-10-26.
 */
public class MessageListenerImpl implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
        for (MessageExt messageExt : messages) {
            System.out.println(messageExt.toString());
            System.out.println(new String(messageExt.getBody()));
        }
        System.out.println("getDelayLevelWhenNextConsume=" + context.getDelayLevelWhenNextConsume()
                + "getMessageQueue=" + context.getMessageQueue().toString()
                + "getDelayLevelWhenNextConsume=" + context.getDelayLevelWhenNextConsume());
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
    }
}