package com.zhaohg.strategy1;

import com.zhaohg.strategy1.impl.FlyWithWin;


public class RedheadDuck extends Duck {

    public RedheadDuck() {
        super();
        super.setFlyingStragety(new FlyWithWin());
    }

    @Override
    public void display() {
        System.out.println("我的头是红色的");
    }

}
