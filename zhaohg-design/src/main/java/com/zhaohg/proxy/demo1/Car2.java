package com.zhaohg.proxy.demo1;

import com.zhaohg.proxy.Car;

public class Car2 extends Car {

    @Override
    public void move() {
        long startTime = System.currentTimeMillis();
        System.out.println("汽车开始行驶....");
        super.move();
        long endTime = System.currentTimeMillis();
        System.out.println("汽车结束行驶....  汽车行驶时间：" + (endTime - startTime) + "毫秒！");
    }


}
