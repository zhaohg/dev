package com.zhaohg.lock;

/**
 * Created by zhaohg on 2018-11-27.
 * 场景：有一个对象value，需要被另个线程调用，由于是共享数据，存在脏数据的问题，悲观锁可以用
 * 现在用乐观锁来解决这个问题
 */
public class OptimisticLock {
    public static int value = 0; //多线程同时调用的操作对象
    
    public static void invoke1(int Avalue, String i) throws InterruptedException {
        Thread.sleep(1000L);
        if (Avalue != value) {
            System.out.println(Avalue + ":" + value + "   A版本不一致，需重试");
            value--;
        } else {
            Avalue++;//对数据操作
            value = Avalue;
            System.out.println(i + ":" + value);
        }
    }
    
    public static void invoke2(int Bvalue, String i) throws InterruptedException {
        Thread.sleep(1000L);
        if (Bvalue != value) {
            System.out.println(Bvalue + ":" + value + "   B版本不一致，需重试");
        } else {
            System.out.println(i + ":" + value);
        }
    }
    
    public static void main(String[] args) {
        
        for (int i = 0; i < 5; i++) {
            new MyThread().start();
        }
    }
}

class MyThread extends Thread {
    
    @Override
    public void run() {
        int Avalue = OptimisticLock.value;
        try {
            OptimisticLock.invoke1(Avalue, "A");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


