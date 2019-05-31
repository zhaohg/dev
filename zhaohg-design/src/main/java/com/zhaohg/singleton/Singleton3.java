package com.zhaohg.singleton;


public class Singleton3 {

    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static Singleton3 instance = null;

    /* 私有构造方法，防止被实例化 */
    private Singleton3() {
    }

    /* 静态工程方法，创建实例 */
    public static Singleton3 getInstance() {//
        if (instance == null) {
            instance = new Singleton3();
        }
        return instance;
    }

    /* 如果该对象被用于序列化，可以保证对象在序列化前后保持一致 */
    public Object readResolve() {
        return instance;
    }

}