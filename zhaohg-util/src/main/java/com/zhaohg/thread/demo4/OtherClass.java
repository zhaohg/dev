package com.zhaohg.thread.demo4;

public class OtherClass {
//    实现Runnable接口方式实现多线程
//    如果自己的类已经extends另一个类，就无法直接extends Thread，此时，必须实现一个Runnable接口，如下：
//    public class MyThread extends OtherClass implements Runnable {
//        public void run() {
//           System.out.println("MyThread.run()");
//        }
//    }
//    为了启动MyThread，需要首先实例化一个Thread，并传入自己的MyThread实例：
//    MyThread myThread = new MyThread();
//    Thread thread = new Thread(myThread);
//    thread.start();
//    事实上，当传入一个Runnable target参数给Thread后，Thread的run()方法就会调用target.run()，参考JDK源代码：
//    public void run() {
//        if (target != null) {
//            target.run();
//        }
//    }
}
