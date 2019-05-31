package com.zhaohg.strategy1.impl;

import com.zhaohg.strategy1.FlyingStragety;

public class FlyNoWay implements FlyingStragety {

    public void performFly() {
        System.out.println("我不会飞行！");
    }

}
