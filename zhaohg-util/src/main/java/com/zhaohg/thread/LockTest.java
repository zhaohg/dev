package com.zhaohg.thread;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

    private volatile Integer  count = 0;
    public           Runnable run1  = new Runnable() {
        @Override
        public void run() {
//			lock.lock();
            synchronized (count) {
                while (count < 1000) {
                    try {
                        Thread.sleep(2000);
                        System.out.println(Thread.currentThread().getName() + " run1: " + count++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

//			lock.unlock();
        }
    };
    public           Runnable run2  = new Runnable() {
        @Override
        public void run() {
//				lock.lock();
            synchronized (count) {
                while (count < 1000) {
                    try {
                        Thread.sleep(2000);
                        System.out.println(Thread.currentThread().getName() + " run2: " + count++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//				lock.unlock();
        }
    };
    public           Runnable run3  = new Runnable() {
        @Override
        public void run() {
//			lock.lock();
            synchronized (count) {
                while (count < 1000) {
                    try {
                        Thread.sleep(2000);
                        System.out.println(Thread.currentThread().getName() + " run3: " + count++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//			lock.unlock();
        }
    };
    public           Runnable run4  = new Runnable() {
        @Override
        public void run() {
//				lock.lock();
            synchronized (count) {
                while (count < 1000) {
                    try {
                        Thread.sleep(2000);
                        System.out.println(Thread.currentThread().getName() + " run4: " + count++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
//				lock.unlock();
        }
    };
    private          Lock     lock  = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        LockTest t = new LockTest();
        new Thread(t.run1).start();
        Thread.sleep(1000);
        new Thread(t.run2).start();
        Thread.sleep(1000);
        new Thread(t.run3).start();
        Thread.sleep(1000);
        new Thread(t.run4).start();
    }

}
