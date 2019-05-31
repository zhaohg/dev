package com.zhaohg.proxy.cglibproxy;

import com.zhaohg.proxy.common.Subject;

/**
 * Created by cat on 2018-02-27.
 */
public class Client {

    public static void main(String[] args) {

        DemoMethodInterceptor cglibProxy = new DemoMethodInterceptor();
        Subject subject = (Subject) cglibProxy.getProxy(Subject.class);
        subject.hello();
    }
}
