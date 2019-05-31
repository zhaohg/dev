package com.zhaohg.guice.impl;

import com.zhaohg.guice.ITool;

/**
 * @program: guice
 * @description:
 * @author: 赖键锋
 * @create: 2018-08-29 20:51
 **/
public class IToolImpl implements ITool {
    @Override
    public void doWork() {
        System.out.println("use tool to work");
    }
}
