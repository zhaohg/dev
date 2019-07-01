package com.zhaohg.guice;

import com.google.inject.AbstractModule;

/**
 * @program: guice
 * @description:
 * @author: zhaohg
 * @create: 2018-08-29 21:01
 **/
public class HumanModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Person.class).asEagerSingleton();
    }
}
