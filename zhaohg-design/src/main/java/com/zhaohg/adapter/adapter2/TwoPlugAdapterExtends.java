package com.zhaohg.adapter.adapter2;

/*
 * 采用继承方式的插座适配器
 */
public class TwoPlugAdapterExtends extends GBTwoPlug implements ThreePlugIf {

    @Override
    public void powerWithThree() {
        System.out.print("借助继承适配器");
        this.powerWithTwo();

    }

}
