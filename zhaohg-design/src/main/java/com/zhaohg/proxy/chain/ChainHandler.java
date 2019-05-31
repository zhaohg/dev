package com.zhaohg.proxy.chain;

/**
 * Created by cat on 2018-02-28.
 */
public abstract class ChainHandler {

    public void execute(Chain chain) {
        handleProcess();
        chain.proceed();
    }

    protected abstract void handleProcess();
}
