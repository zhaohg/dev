package com.zhaohg.thread;

/**
 * Created by zhaohg on 2017/2/21.
 */

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {
    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new MyTimerTask2(), 2000);
        while (true) {
            System.out.println(new Date().getSeconds());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    static class MyTimerTask1 extends TimerTask {
        public void run() {
            System.out.println("爆炸！！！");
            new Timer().schedule(new MyTimerTask2(), 2000);
        }
    }

    static class MyTimerTask2 extends TimerTask {
        public void run() {
            System.out.println("爆炸！！！");
            new Timer().schedule(new MyTimerTask1(), 3000);
        }
    }
}