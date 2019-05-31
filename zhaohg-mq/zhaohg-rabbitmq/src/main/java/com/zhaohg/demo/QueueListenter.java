package com.zhaohg.demo;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.stereotype.Component;

/**
 * Created by zhaohg on 2017-10-26.
 */
@Component
public class QueueListenter implements MessageListener {

    @Override
    public void onMessage(Message msg) {
        try {
            System.out.print(msg.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}