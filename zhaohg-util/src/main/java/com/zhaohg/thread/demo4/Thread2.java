package com.zhaohg.thread.demo4;

public class Thread2 extends OtherClass implements Runnable {
    public static void main(String[] args) {
        Thread2 thread2 = new Thread2();
        Thread thread = new Thread(thread2);
        thread.start();
    }

    public void run() {
        System.out.println("MyThread.run()");
    }
}