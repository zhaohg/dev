package com.zhaohg.singleton;

/**
 * 单例模式Singleton
 * 应用场合：有些对象只需要一个就足够了，如古代皇帝、老婆
 * 作用：保证整个应用程序中某个实例有且只有一个
 * 类型：饿汉模式、懒汉模式
 */
public class Singleton4 {
    /* 私有构造方法，防止被实例化 */
    private Singleton4() {
    }

    /* 获取实例 */
    public static Singleton4 getInstance() {
        return SingletonFactory.instance;
    }

    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */
    public Object readResolve() {
        return getInstance();
    }

    /* 此处使用一个内部类来维护单例 */
    private static class SingletonFactory {
        private static Singleton4 instance = new Singleton4();
    }

}
