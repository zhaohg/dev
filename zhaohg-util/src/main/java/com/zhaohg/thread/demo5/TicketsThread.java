package com.zhaohg.thread.demo5;

/**
 * @author zhaohg
 * @date 2018/08/05.
 */
public class TicketsThread {

    public static void main(String[] args) {

        MyThread thread1 = new MyThread("窗口1");
        MyThread thread2 = new MyThread("窗口2");
        MyThread thread3 = new MyThread("窗口3");

        thread1.start();
        thread2.start();
        thread3.start();

    }
}

class MyThread extends Thread {

    public  String name;  //买票的窗口名 线程名
    private int    ticketsCount = 5; //一共5张票

    public MyThread(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (ticketsCount > 0) {
            ticketsCount--; //卖出一张
            System.out.println(Thread.currentThread().getName() + " " + name + " 卖出一张，还剩  " + ticketsCount);
        }
    }
}