package com.zhaohg.decorator;


public class Decorator implements Sourceable {

    private Sourceable source;

    public Decorator(Sourceable source) {
        super();
        this.source = source;
    }

    @Override
    public void method() {
        System.out.println("before decorator1!");
        source.method();
        System.out.println("after decorator1!");
    }
}
