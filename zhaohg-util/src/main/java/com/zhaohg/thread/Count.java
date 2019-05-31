package com.zhaohg.thread;

/**
 * Created by zhaohg on 2017/2/21.
 */
public class Count {
    private int num = 0;

    public void count() {
        for (int i = 1; i <= 10; i++) {
            num += i;
        }
        System.out.println(Thread.currentThread().getName() + "-" + num);
    }
}

class ThreadTest {
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            Count count = new Count();

            public void run() {
                //Count count = new Count(); //每次启动一个线程使用不同的线程类
                count.count();
            }
        };
        for (int i = 0; i < 10; i++) {
            new Thread(runnable).start();
        }
    }
}

class Test {
    static int i = 0, j = 0;

    static synchronized void one() {
        i++;
        j++;
    }

    static synchronized void two() {
        System.out.println("i=" + i + " j=" + j);
    }
}

class Test1 {
    static volatile int i = 0, j = 0;

    static void one() {
        i++;
        j++;
    }

    static void two() {
        System.out.println("i=" + i + " j=" + j);
    }
}