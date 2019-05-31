package com.zhaohg.zookeeper;

/**
 * Created by zhaohg on 2018/4/20.
 */
public class Test {
    
    static int n = 500;
    
    public static void secsKill() {
        System.out.println(--n);
    }
    
    public static void main(String[] args) {
        
        
        Runnable runnable = () -> {
            DistributedLock lock = null;
            try {
                lock = new DistributedLock("127.0.0.1:2181", "test1");
                lock.lock();
                secsKill();
                System.out.println(Thread.currentThread().getName() + "正在运行");
            } finally {
                if (lock != null) {
                    lock.unlock();
                }
            }
        };
        
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}
