package com.zhaohg.thread.demo5;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author zhaohg
 * @date 2018/08/05.
 */
public class DaemonThreadDemo {

    public static void main(String[] args) {

        Thread thread = new Thread(new DaemonThread());
        thread.setDaemon(true);
        thread.start();

    }
}

class DaemonThread implements Runnable {


    @Override
    public void run() {
        System.out.println("程序进入守护线程" + Thread.currentThread().getName());
        try {
            writeFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("程序退出守护线程" + Thread.currentThread().getName());

    }

    private void writeFile() throws Exception {

        File filename = new File("/Users/zhaohg/" + File.separator + "Daemon.txt");
        OutputStream os = new FileOutputStream(filename, false);//true表示每次是追加；false表示每次覆写
        int count = 0;
        while (count < 9999) {
            os.write(("\r\nword" + ++count).getBytes());
            System.out.println(count);
        }
    }
}