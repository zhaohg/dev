package com.zhaohg.thread.demo4;

public class Thread1 extends Thread {
    public static void main(String[] args) {
        Thread1 thread1 = new Thread1();
        Thread1 thread2 = new Thread1();
        thread1.start();
        thread2.start();
    }

    public void run() {
        System.out.println("MyThread.run()");
    }
}
