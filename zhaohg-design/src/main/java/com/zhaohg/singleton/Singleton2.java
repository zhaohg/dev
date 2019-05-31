package com.zhaohg.singleton;

/**
 * 懒汉模式
 */
public class Singleton2 {
    //2.声明类的唯一实例，使用private static修饰
    private static Singleton2 instance;

    //1.将构造方式私有化，不允许外边直接创建对象
    private Singleton2() {
    }

    //3.提供一个用于获取实例的方法，使用public static修饰
    public static Singleton2 getInstance() {
        if (instance == null) {
            instance = new Singleton2();
        }
        return instance;
    }
}