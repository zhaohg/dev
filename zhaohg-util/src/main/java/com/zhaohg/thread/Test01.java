package com.zhaohg.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Java实现多线程的三种方式
 */

/**
 * 继承Thread类
 */
public class Test01 extends Thread {
    public static void main(String[] args) {
        Thread t = new Test01();
        t.start();
    }

    @Override
    public void run() {
        System.out.println("Override run() ...");
    }
}

/**
 * 实现Runnable接口，并覆写run方法
 */
class Test02 implements Runnable {
    public static void main(String[] args) {
        Thread t = new Thread(new Test02());
        t.start();
    }

    @Override
    public void run() {
        System.out.println("Override run() ...");
    }
}

/**
 * 实现Callable接口，并覆写call方法
 */
class Test03 implements Callable {
    public static void main(String[] args) {
        FutureTask futureTask = new FutureTask(new Test03());
        Thread thread = new Thread(futureTask);
        thread.start();
        try {
            Object o = futureTask.get();
            System.out.println(o);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object call() throws Exception {
        return "Override call() ...";
    }
}