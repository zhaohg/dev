package com.zhaohg.guice.impl;

import com.zhaohg.guice.IAnimal;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 20:53
 **/
public class IAnimalImpl implements IAnimal {
    @Override
    public void work() {
        System.out.println("animals can also do work");
    }
}
