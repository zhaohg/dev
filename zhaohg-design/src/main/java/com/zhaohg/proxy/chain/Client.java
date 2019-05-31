package com.zhaohg.proxy.chain;

/**
 * Created by cat on 2018-02-28.
 */
public class Client {
    public static void main(String[] args) {
        Handler handlerA = new HandlerA();
        Handler handlerB = new HandlerB();
        Handler handlerC = new HandlerC();

        handlerA.setSucessor(handlerB);
        handlerB.setSucessor(handlerC);

        handlerA.execute();
    }

    static class HandlerA extends Handler {
        @Override
        protected void handleProcess() {
            System.out.println("handle by a");
        }
    }

    static class HandlerB extends Handler {
        @Override
        protected void handleProcess() {
            System.out.println("handle by b");
        }
    }

    static class HandlerC extends Handler {
        @Override
        protected void handleProcess() {
            System.out.println("handle by c");
        }
    }
}
