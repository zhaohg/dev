package com.zhaohg.strategy1.impl;


import com.zhaohg.strategy1.FlyingStragety;

public class FlyWithRocket implements FlyingStragety {

    public void performFly() {
        System.out.println("用火箭在太空遨游");
    }

}
