package com.zhaohg.proxy.demo4.cglibproxy;

public class Client {

    /**
     * @param args
     */
    public static void main(String[] args) {

        CglibProxy proxy = new CglibProxy();
        Train train = (Train) proxy.getProxy(Train.class);
        train.move();
    }

}
