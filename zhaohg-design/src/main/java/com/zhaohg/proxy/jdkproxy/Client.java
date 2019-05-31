package com.zhaohg.proxy.jdkproxy;


import com.zhaohg.proxy.common.RealSubject;
import com.zhaohg.proxy.common.Subject;

import java.lang.reflect.Proxy;

/**
 * System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
 * Created by cat on 2018-02-27.
 */
public class Client {

    public static void main(String[] args) {
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
        Subject subject = (Subject) Proxy.newProxyInstance(Client.class.getClassLoader(), new Class[]{Subject.class}, new JdkProxySubject(new RealSubject()));
        subject.hello();

    }
}
