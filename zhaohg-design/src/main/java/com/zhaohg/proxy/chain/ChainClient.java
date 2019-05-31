package com.zhaohg.proxy.chain;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cat on 2018-02-28.
 */
public class ChainClient {
    public static void main(String[] args) {
        List<ChainHandler> handlers = Arrays.asList(
                new ChainHandlerA(),
                new ChainHandlerB(),
                new ChainHandlerC()
        );
        Chain chain = new Chain(handlers);
        chain.proceed();
    }

    static class ChainHandlerA extends ChainHandler {
        @Override
        protected void handleProcess() {
            System.out.println("handle by chain a");
        }
    }

    static class ChainHandlerB extends ChainHandler {
        @Override
        protected void handleProcess() {
            System.out.println("handle by chain b");
        }
    }

    static class ChainHandlerC extends ChainHandler {
        @Override
        protected void handleProcess() {
            System.out.println("handle by chain c");
        }
    }
}
