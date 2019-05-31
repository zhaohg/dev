package com.zhaohg.thread.demo5;

/**
 * @author zhaohg
 * @date 2018/08/05.
 */
public class TicketsRunnable {

    public static void main(String[] args) {
        RunAbleThread runAbleThread = new RunAbleThread();

        Thread thread1 = new Thread(runAbleThread, "窗口1");
        Thread thread2 = new Thread(runAbleThread, "窗口2");
        Thread thread3 = new Thread(runAbleThread, "窗口3");

        thread1.start();
        thread2.start();
        thread3.start();
    }

}

class RunAbleThread implements Runnable {

    private int ticketsCount = 5; //一共5张票
//    public String name ;  //买票的窗口名 线程名
//
//    public RunAbleThread(String name){
//        this.name = name;
//    }

    @Override
    public void run() {
        while (ticketsCount > 0) {
            ticketsCount--; //卖出一张
            System.out.println(Thread.currentThread().getName() + " 卖出一张，还剩  " + ticketsCount);
        }
    }
}