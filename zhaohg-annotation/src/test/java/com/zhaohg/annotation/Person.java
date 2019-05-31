package com.zhaohg.annotation;

public interface Person {

    String name();

    int age();

    @Deprecated
    void sing();
}
