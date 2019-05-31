package com.zhaohg.redis.test;

/**
 * Created by zhaohg on 2018/4/20.
 */
public class ThreadA extends Thread {
    private Service service;

    public ThreadA(Service service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.seckill();
    }
}
