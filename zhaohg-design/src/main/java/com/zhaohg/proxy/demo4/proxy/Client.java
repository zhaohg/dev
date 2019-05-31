package com.zhaohg.proxy.demo4.proxy;

import com.zhaohg.proxy.Car;
import com.zhaohg.proxy.Moveable;

public class Client {

    /**
     * 测试类
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Car car = new Car();
        InvocationHandler h = new TimeHandler(car);
        Moveable m = (Moveable) Proxy.newProxyInstance(Moveable.class, h);
        m.move();
    }

}
