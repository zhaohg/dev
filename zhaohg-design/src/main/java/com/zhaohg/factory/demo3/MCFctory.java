package com.zhaohg.factory.demo3;

/**
 * 圣诞系列加工厂
 * @author Administrator
 */
public class MCFctory implements PersonFactory {

    @Override
    public Boy getBoy() {
        return new MCBoy();
    }

    @Override
    public Girl getGirl() {
        return new MCGirl();
    }

}
