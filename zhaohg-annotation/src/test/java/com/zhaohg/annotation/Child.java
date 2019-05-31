package com.zhaohg.annotation;

@Description(anthor = "I am zhaohg", desc = "boy", age = 27)
public class Child implements Person {

    @Override
    public String name() {
        return null;
    }

    @Override
    public int age() {
        return 0;
    }

    @Override
    public void sing() {

    }

    @Description(anthor = "I am eyeColor", desc = "boy", age = 10)
    public String eyeColor() {
        return "red";
    }
}
