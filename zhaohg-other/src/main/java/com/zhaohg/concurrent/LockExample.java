package com.zhaohg.concurrent;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhaohg on 2018-11-14.
 */
public class LockExample {

    public static void main(String[] args) {
        //18.锁 Lock 是一个类似于 synchronized 块的线程同步机制。但是 Lock 比 synchronized 块更加灵活、精细
        //Lock 是一个接口，在你的程序里需要使用它的实现类之一来使用它
        Lock lock = new ReentrantLock();
        lock.lock();
        //critical section
        lock.unlock();

        //19.读写锁 ReadWriteLock
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readWriteLock.readLock().lock();
        // multiple readers can enter this section
        // if not locked for writing, and not writers waiting
        // to lock for writing.
        readWriteLock.readLock().unlock();
        readWriteLock.writeLock().lock();
        // only one writer can enter this section,
        // and only if no threads are currently reading.
        readWriteLock.writeLock().unlock();

        //Volatile 粒度小，性能比同步锁高，但是不容易使用，容易造成死锁


    }
}
