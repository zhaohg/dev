package com.zhaohg.redis.test;

/**
 * Created by zhaohg on 2018/4/20.
 */
public class Test {

    public static void main(String[] args) {

        Service service = new Service();
        for (int i = 0; i < 50; i++) {
            ThreadA threadA = new ThreadA(service);
            threadA.start();
        }

    }
}
