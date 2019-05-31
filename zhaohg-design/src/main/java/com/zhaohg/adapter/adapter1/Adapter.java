package com.zhaohg.adapter.adapter1;

/**
 * Created by zhaohg on 2018/10/9.
 */
public class Adapter extends Source implements Targetable {

    @Override
    public void method2() {
        System.out.println("this is the targetable method!");
    }
}