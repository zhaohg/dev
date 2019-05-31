package com.zhaohg.concurrent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by zhaohg on 2018-11-14.
 */
public class AtomicExample {
    public static void main(String[] args) {

        //20.原子性布尔 AtomicBoolean类为我们提供了一个可以用原子方式进行读和写的布尔值，它还拥有一些先进的原子性操作，比如 compareAndSet()。
        // AtomicBoolean 类位于 java.util.concurrent.atomic 包，完整类名是为 java.util.concurrent.atomic.AtomicBoolean。
        // 本小节描述的 AtomicBoolean 是 Java 8 版本里的，而不是它第一次被引入的 Java 5 版本
        //想要为 AtomicBoolean 实例设置一个显式的初始值，那么你可以将初始值传给 AtomicBoolean 的构造子
        AtomicBoolean atomicBoolean1 = new AtomicBoolean(true);
        System.out.println(atomicBoolean1.get());
        atomicBoolean1.set(false);
        System.out.println(atomicBoolean1.get());

        //交换 AtomicBoolean 的值
        //你可以通过 getAndSet() 方法来交换一个 AtomicBoolean 实例的值。getAndSet() 方法将返回 AtomicBoolean 当前的值，
        // 并将为 AtomicBoolean 设置一个新值
        AtomicBoolean atomicBoolean2 = new AtomicBoolean(true);
        boolean value = atomicBoolean2.getAndSet(false);
        System.out.println(value);

        //比较并设置 AtomicBoolean 的值
        //compareAndSet() 方法允许你对 AtomicBoolean 的当前值与一个期望值进行比较，如果当前值等于期望值的话，将会对 AtomicBoolean 设定一个新值。
        //compareAndSet() 方法是原子性的，因此在同一时间之内只有单个线程执行它。因此 compareAndSet() 方法可被用于一些类似于锁的同步的简单实现。
        AtomicBoolean atomicBoolean3 = new AtomicBoolean(true);
        boolean expectedValue1 = true;
        boolean newValue1 = false;
        boolean wasNewValueSet = atomicBoolean3.compareAndSet(expectedValue1, newValue1);
        System.out.println(wasNewValueSet);
        System.out.println(atomicBoolean3.get());

        //21.原子性整型 AtomicInteger类为我们提供了一个可以进行原子性读和写操作的 int 变量，它还包含一系列先进的原子性操作，比如 compareAndSet()。
        AtomicInteger atomicInteger1 = new AtomicInteger(123);
        System.out.println(atomicInteger1.get());
        atomicInteger1.set(234);
        System.out.println(atomicInteger1.get());

        AtomicInteger atomicInteger2 = new AtomicInteger(123);
        int expectedValue2 = 123;
        int newValue2 = 234;
        atomicInteger2.compareAndSet(expectedValue2, newValue2);

        AtomicInteger atomicInteger3 = new AtomicInteger();
        //拿到的是加 10 之前的 AtomicInteger 的值 ,该值为0
        System.out.println(atomicInteger3.getAndAdd(10));
        //将 AtomicInteger 的值再加 10，并返回加操作之后的值。该值现在是为 20
        System.out.println(atomicInteger3.addAndGet(10));
        //getAndIncrement() 和 incrementAndGet(), 每次只将 AtomicInteger 的值加 1
        System.out.println(atomicInteger3.getAndIncrement());
        System.out.println(atomicInteger3.incrementAndGet());
        //decrementAndGet() 将 AtomicInteger 的值减一，并返回减一后的值。
        //getAndDecrement() 也将 AtomicInteger 的值减一，但它返回的是减一之前的值。
        System.out.println(atomicInteger3.getAndDecrement());
        System.out.println(atomicInteger3.decrementAndGet());

        //22.原子性长整型 AtomicLong提供了一个可以进行原子性读和写操作的 long 变量，它还包含一系列先进的原子性操作，
        // 比如 compareAndSet()
        AtomicLong atomicLong1 = new AtomicLong(123);
        System.out.println(atomicLong1.get());
        atomicLong1.set(234);
        System.out.println(atomicLong1.get());

        AtomicLong atomicLong2 = new AtomicLong(123);
        long expectedValue = 123;
        long newValue = 234;
        atomicLong2.compareAndSet(expectedValue, newValue);

        AtomicLong atomicLong3 = new AtomicLong();
        //拿到的是加 10 之前的 AtomicLong 的值 ,该值为0
        System.out.println(atomicLong3.getAndAdd(10));
        //将 AtomicLong 的值再加 10，并返回加操作之后的值。该值现在是为 20
        System.out.println(atomicLong3.addAndGet(10));
        //getAndIncrement() 和 incrementAndGet(), 每次只将 AtomicLong 的值加 1
        System.out.println(atomicLong3.getAndIncrement());
        System.out.println(atomicLong3.incrementAndGet());
        //decrementAndGet() 将 AtomicLong 的值减一，并返回减一后的值。
        //getAndDecrement() 也将 AtomicLong 的值减一，但它返回的是减一之前的值。
        System.out.println(atomicLong3.getAndDecrement());
        System.out.println(atomicLong3.decrementAndGet());

        //23.原子性引用型 AtomicReference提供了一个可以被原子性读和写的对象引用变量。
        // 原子性的意思是多个想要改变同一个 AtomicReference 的线程不会导致 AtomicReference 处于不一致的状态。
        // AtomicReference 还有一个 compareAndSet() 方法，通过它你可以将当前引用于一个期望值(引用)进行比较，如果相等，
        // 在该 AtomicReference 对象内部设置一个新的引用
        String initialReference = "the initially referenced string";
        //创建泛型 AtomicReference
        AtomicReference<String> atomicReference = new AtomicReference<>(initialReference);
        System.out.println(atomicReference.get());
        atomicReference.set("New object referenced");
        System.out.println(atomicReference.get());
        //创建了一个带有一个初始引用的泛型化的 AtomicReference。之后两次调用 comparesAndSet()来对存储值和期望值进行对比，
        // 如果二者一致，为 AtomicReference 设置一个新的引用。第一次比较，存储的引用(initialReference)和期望的引用(initialReference)一致，
        // 所以一个新的引用(newReference)被设置给 AtomicReference，compareAndSet() 方法返回 true。第二次比较时，
        // 存储的引用(newReference)和期望的引用(initialReference)不一致，因此新的引用没有被设置给 AtomicReference，compareAndSet() 方法返回 false
        AtomicReference<String> atomicStringReference = new AtomicReference<>(initialReference);
        String newReference = "new value referenced";
        boolean exchanged = atomicStringReference.compareAndSet(initialReference, newReference);
        System.out.println("exchanged: " + exchanged);
        exchanged = atomicStringReference.compareAndSet(initialReference, newReference);
        System.out.println("exchanged: " + exchanged);

    }
}
