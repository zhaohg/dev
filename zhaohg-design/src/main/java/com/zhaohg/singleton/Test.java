package com.zhaohg.singleton;

/**
 * 区别：饿汉模式的特点是加载类时比较慢，但运行时获取对象的速度比较快，线程安全
 * 懒汉模式的特点是加载类时比较快，但运行时获取对象的速度比较慢，线程不安全
 * <p>
 * <p>
 * <p>
 * 单例对象（Singleton1）是一种常用的设计模式。在Java应用中，单例对象能保证在一个JVM中，
 * 该对象只有一个实例存在。这样的模式有几个好处：
 * 1、某些类创建比较频繁，对于一些大型的对象，这是一笔很大的系统开销。
 * 2、省去了new操作符，降低了系统内存的使用频率，减轻GC压力。
 * 3、有些类如交易所的核心交易引擎，控制着交易流程，如果该类可以创建多个的话，系统完全乱了。
 * (比如一个军队出现了多个司令员同时指挥，肯定会乱成一团)，所以只有使用单例模式，才能保证核心交易服务器独立控制整个流程。
 * 这个类可以满足基本要求，但是，像这样毫无线程安全保护的类，如果我们把它放入多线程的环境下，肯定就会出现问题了，
 * 如何解决？ 创建实例时加synchronized public static Singleton3 getInstance(){}
 * 但是，synchronized关键字锁住的是这个对象，这样的用法，在性能上会有所下降，
 * 因为每次调用getInstance()，都要对对象上锁，事实上，只有在第一次创建对象的时候需要加锁，之后就不需要了，
 * 所以，这个地方需要改进。
 * public static Singleton3 getInstance() {
 * if (instance == null) {
 * synchronized (instance) {//第一次创建时加锁
 * if (instance == null) {
 * instance = new Singleton4();
 * }
 * }
 * }
 * return instance;
 * }
 * <p>
 * 似乎解决了之前提到的问题，将synchronized关键字加在了内部，也就是说当调用的时候是不需要加锁的，只有在instance为null，
 * 并创建对象的时候才需要加锁，性能有一定的提升。但是，这样的情况，还是有可能有问题的，看下面的情况：
 * 在Java指令中创建对象和赋值操作是分开进行的，也就是说instance = new Singleton1();语句是分两步执行的。
 * 但是JVM并不保证这两个操作的先后顺序，也就是说有可能JVM会为新的Singleton实例分配空间，然后直接赋值给instance成员，
 * 然后再去初始化这个Singleton实例。这样就可能出错了，我们以A、B两个线程为例：
 * a>A、B线程同时进入了第一个if判断
 * b>A首先进入synchronized块，由于instance为null，所以它执行instance = new Singleton1();
 * c>由于JVM内部的优化机制，JVM先画出了一些分配给Singleton实例的空白内存，并赋值给instance成员（注意此时JVM没有开始初始化这个实例），然后A离开了synchronized块。
 * d>B进入synchronized块，由于instance此时不是null，因此它马上离开了synchronized块并将结果返回给调用该方法的程序。
 * e>此时B线程打算使用Singleton实例，却发现它没有被初始化，于是错误发生了。
 * 所以程序还是有可能发生错误，其实程序在运行过程是很复杂的，从这点我们就可以看出，尤其是在写多线程环境下的程序更有难度，有挑战性。我们对该程序做进一步优化：
 * private static class SingletonFactory{
 * private static Singleton1 instance = new Singleton1();
 * }
 * public static Singleton1 getInstance(){
 * return SingletonFactory.instance;
 * }
 * 实际情况是，单例模式使用内部类来维护单例的实现，JVM内部的机制能够保证当一个类被加载的时候，这个类的加载过程是线程互斥的。
 * 这样当我们第一次调用getInstance的时候，JVM能够帮我们保证instance只被创建一次，并且会保证把赋值给instance的内存初始化完毕，
 * 这样我们就不用担心上面的问题。同时该方法也只会在第一次调用的时候使用互斥机制，这样就解决了低性能问题。
 * 这样我们暂时总结一个完美的单例模式Singleton4
 */

public class Test {
    public static void main(String[] args) {
        //饿汉模式
        Singleton1 s1 = Singleton1.getInstance();
        Singleton1 s2 = Singleton1.getInstance();
        if (s1 == s2) {
            System.out.println("s1和s2是同一个实例");
        } else {
            System.out.println("s1和s2不是同一个实例");
        }

        //懒汉模式
        Singleton2 s3 = Singleton2.getInstance();
        Singleton2 s4 = Singleton2.getInstance();
        if (s3 == s4) {
            System.out.println("s3和s4是同一个实例");
        } else {
            System.out.println("S3和s4不是同一个实例");
        }


        Singleton3 s5 = Singleton3.getInstance();
        Singleton3 s6 = Singleton3.getInstance();
        if (s5 == s6) {
            System.out.println("s5和s6是同一个实例");
        } else {
            System.out.println("s5和s6不是同一个实例");
        }


        Singleton4 s7 = Singleton4.getInstance();
        Singleton4 s8 = Singleton4.getInstance();
        if (s7 == s8) {
            System.out.println("s7和s8是同一个实例");
        } else {
            System.out.println("s7和s8不是同一个实例");
        }
    }
}
