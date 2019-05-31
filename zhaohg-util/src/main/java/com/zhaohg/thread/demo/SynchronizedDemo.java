package com.zhaohg.thread.demo;

/**
 * Synchronized 内存可见性
 * 线程不能直接操作主内存
 * 线程不能直接用其他线程工作内存中的变量
 * 线程先改变工作内存中的变量，然后刷新到主内存，其他线程在把主内存中的变量刷新到自己的工作内存中
 * @author zhaohg
 * @date 2018/08/05.
 */
public class SynchronizedDemo {
    //共享变量
    private boolean ready  = false;
    private int     result = 0;
    private int     number = 1;

    public static void main(String[] args) {
        SynchronizedDemo synDemo = new SynchronizedDemo();
        //启动线程执行写操作
        synDemo.new ReadWriteThread(true).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //启动线程执行读操作
        synDemo.new ReadWriteThread(false).start();
    }

    //写操作
    public void write() {
        ready = true;                         //1.1
        number = 2;                            //1.2
    }

    //读操作
    public void read() {
        if (ready) {                             //2.1
            result = number * 3;        //2.2
        }
        System.out.println("result的值为：" + result);
    }

    //内部线程类
    private class ReadWriteThread extends Thread {
        //根据构造方法中传入的flag参数，确定线程执行读操作还是写操作
        private boolean flag;

        public ReadWriteThread(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            if (flag) {
                //构造方法中传入true，执行写操作
                write();
            } else {
                //构造方法中传入false，执行读操作
                read();
            }
        }
    }
}

