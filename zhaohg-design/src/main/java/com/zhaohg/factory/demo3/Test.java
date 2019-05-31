package com.zhaohg.factory.demo3;

public class Test {

    public static void main(String[] args) {

        HairInterface left1 = new LeftHair();
        left1.draw();

        HairFactory factory1 = new HairFactory();
        HairInterface right = factory1.getHair("right");
        right.draw();

        HairFactory factory2 = new HairFactory();
        HairInterface left2 = factory2.getHairByClass("com.zhaohg.factory.demo3");
        left2.draw();

        HairFactory factory3 = new HairFactory();
        HairInterface hair = factory3.getHairByClassKey("in");
        hair.draw();

        PersonFactory facoty4 = new MCFctory();
        Girl girl = facoty4.getGirl();
        girl.drawWomen();

        PersonFactory facoty5 = new HNFactory();
        Boy boy = facoty5.getBoy();
        boy.drawMan();
    }
}
