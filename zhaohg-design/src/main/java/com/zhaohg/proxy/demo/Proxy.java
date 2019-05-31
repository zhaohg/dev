package com.zhaohg.proxy.demo;

public class Proxy implements Sourceable {

    private Source source;

    public Proxy() {
        super();
        this.source = new Source();
    }

    @Override
    public void method() {
        before();
        source.method();
        atfer();
    }

    private void atfer() {
        System.out.println("after jdkproxy2!");
    }

    private void before() {
        System.out.println("before jdkproxy2!");
    }
}
