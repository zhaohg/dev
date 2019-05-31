package com.zhaohg.proxy.jdkproxy2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TimeHandler implements InvocationHandler {

    private Object target;

    public TimeHandler(Object target) {
        super();
        this.target = target;
    }

    /**
     * 参数：
     * jdkproxy2  被代理对象
     * method  被代理对象的方法
     * args 方法的参数
     * <p>
     * 返回值：
     * com.zhaohg.Object  方法的返回值
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        long starttime = System.currentTimeMillis();
        System.out.println("汽车开始行驶....");
        method.invoke(target);
        long endtime = System.currentTimeMillis();
        System.out.println("汽车结束行驶....  汽车行驶时间：" + (endtime - starttime) + "毫秒！");

        Object result = null;
        try {
            result = method.invoke(proxy, args);
        } catch (Exception e) {
            System.out.println("ex:" + e.getMessage());
            throw e;
        } finally {
            System.out.println("after");
        }
        return result;
    }

}
