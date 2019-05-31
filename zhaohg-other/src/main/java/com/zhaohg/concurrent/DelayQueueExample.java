package com.zhaohg.concurrent;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;

/**
 * Created by zhaohg on 2018-11-14.
 */
public class DelayQueueExample {

    public static void main(String[] args) throws InterruptedException {
        DelayQueue<Delayed> queue = new DelayQueue<>();
        Delayed element = new DelayedElement();
        queue.put(element);

        Delayed takeElement = queue.take();


    }
}