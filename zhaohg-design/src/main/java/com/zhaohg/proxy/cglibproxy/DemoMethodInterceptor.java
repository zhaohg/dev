package com.zhaohg.proxy.cglibproxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by cat on 2018-02-27.
 */
public class DemoMethodInterceptor implements MethodInterceptor {

    private Enhancer enhancer = new Enhancer();

    public Object getProxy(Class clazz) {
        //设置创建子类的类
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);

        return enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("before in cglib");
        Object result = null;
        try {
            result = proxy.invokeSuper(obj, args);
        } catch (Exception e) {
            System.out.println("get ex:" + e.getMessage());
            throw e;
        } finally {
            System.out.println("after in cglib");
        }
        return result;
    }
}
