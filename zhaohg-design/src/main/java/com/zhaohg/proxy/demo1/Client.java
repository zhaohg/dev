package com.zhaohg.proxy.demo1;

import com.zhaohg.proxy.Car;
import com.zhaohg.proxy.Moveable;

public class Client {

    /**
     * 测试类
     */
    public static void main(String[] args) {
        Car car1 = new Car();
        car1.move();
        //使用集成方式
        Moveable m1 = new Car2();
        m1.move();
        //使用聚合方式实现
        Car car2 = new Car();
        Moveable m2 = new Car3(car2);
        m2.move();
    }

}
