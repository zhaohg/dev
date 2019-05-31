package com.zhaohg.concurrent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaohg on 2018-11-14.
 */
public class ConcurrentExample {

    public static void main(String[] args) throws Exception {

        //1、阻塞队列
        //2、延迟队列 DelayQueue 实现了 BlockingQueue 接口
        //3、数组阻塞队列 ArrayBlockingQueue 类实现了 BlockingQueue 接口
        BlockingQueue<String> arrayQueue = new ArrayBlockingQueue<>(1024);
        Producer producer = new Producer(arrayQueue);
        Consumer consumer = new Consumer(arrayQueue);
        new Thread(producer).start();
        new Thread(consumer).start();

        Thread.sleep(4000);


        //4、链阻塞队列 LinkedBlockingQueue 类实现了 BlockingQueue 接口
        BlockingQueue<String> linkedQueue = new LinkedBlockingQueue<>(1024);
        linkedQueue.put("Value1");
        String value1 = linkedQueue.take();
        System.out.println(value1);

        //5、优先级阻塞队列 PriorityBlockingQueue 类实现了 BlockingQueue 接口
        BlockingQueue<String> priorityQueue = new PriorityBlockingQueue<>();
        //String implements java.lang.Comparable
        priorityQueue.put("Value2");
        String value2 = priorityQueue.take();
        System.out.println(value2);

        //6、同步队列 SynchronousQueue 类实现了 BlockingQueue 接口
        SynchronousQueue<String> synchronousQueue = new SynchronousQueue<>();
        synchronousQueue.put("synchronousQueue");
        String value3 = synchronousQueue.take();
        System.out.println(value3);

        //7、阻塞双端队列 BlockingDeque 接口表示一个线程安放入和提取实例的双端队列
        //7、链阻塞双端队列 LinkedBlockingDeque 类实现了 BlockingDeque 接口
        BlockingDeque<String> deque = new LinkedBlockingDeque<>();
        deque.addFirst("1");
        deque.addLast("2");
        String two = deque.takeLast();
        String one = deque.takeFirst();
        System.out.println("one=" + one + "two=" + two);

        //8、并发 Map(映射) ConcurrentMap 接口表示了一个能够对别人的访问(插入和提取)进行并发处理的java.util.Map。
        //ConcurrentMap 除了从其父接口 java.util.Map 继承来的方法之外还有一些额外的原子性方法

        //ConcurrentMap 的实现
//        既然 ConcurrentMap 是个接口，你想要使用它的话就得使用它的实现类之一。java.util.concurrent 包具备 ConcurrentMap 接口的以下实现类：ConcurrentHashMap
//
//        ConcurrentHashMap 和 java.util.HashTable 类很相似，但 ConcurrentHashMap 能够提供比 HashTable 更好的并发性能。
//        在你从中读取对象的时候 ConcurrentHashMap 并不会把整个 Map 锁住。此外，在你向其中写入对象的时候，ConcurrentHashMap 也不会锁住整个 Map。
//        它的内部只是把 Map 中正在被写入的部分进行锁定。
//        另外一个不同点是，在被遍历的时候，即使是 ConcurrentHashMap 被改动，它也不会抛 ConcurrentModificationException。尽管 Iterator 的设计不是为多个线程的同时使用。
        ConcurrentMap<String, String> concurrentMap = new ConcurrentHashMap<>();
        concurrentMap.put("key", "value");
        Object value4 = concurrentMap.get("key");
        System.out.println(value4);

        //9、并发导航映射 ConcurrentNavigableMap是一个支持并发访问的 java.util.NavigableMap，它还能让它的子 map 具备并发访问的能力。
        //所谓的 "子 map" 指的是诸如 headMap()，subMap()，tailMap() 之类的方法返回的 map
        //headMap()  headMap(T toKey) 方法返回一个包含了小于给定 toKey 的 key 的子 map。如果你对原始 map 里的元素做了改动，这些改动将影响到子 map 中的元素(译者注：map 集合持有的其实只是对象的引用)
        //headMap 将指向一个只含有键 "1" 的 ConcurrentNavigableMap，因为只有这一个键小于 "2"
        ConcurrentNavigableMap<String, String> map1 = new ConcurrentSkipListMap<>();
        map1.put("1", "one");
        map1.put("2", "two");
        map1.put("3", "three");
        ConcurrentNavigableMap headMap = map1.headMap("2");

        //tailMap() tailMap(T fromKey) 方法返回一个包含了不小于给定 fromKey 的 key 的子 map。如果你对原始 map 里的元素做了改动，这些改动将影响到子 map 中的元素(译者注：map 集合持有的其实只是对象的引用)
        //tailMap 将拥有键 "2" 和 "3"，因为它们不小于给定键 "2"
        ConcurrentNavigableMap<String, String> map2 = new ConcurrentSkipListMap<>();
        map2.put("1", "one");
        map2.put("2", "two");
        map2.put("3", "three");
        ConcurrentNavigableMap tailMap = map2.tailMap("2");
        //subMap() 方法返回原始 map 中，键介于 from(包含) 和 to (不包含) 之间的子 map.submap 只包含键 "2"，因为只有它满足不小于 "2"，比 "3" 小
        ConcurrentNavigableMap<String, String> map3 = new ConcurrentSkipListMap<>();
        map3.put("1", "one");
        map3.put("2", "two");
        map3.put("3", "three");
        ConcurrentNavigableMap subMap = map3.subMap("2", "3");
        //更多方法 ConcurrentNavigableMap 接口还有其他一些方法可供使用，比如：
//        descendingKeySet()
//        descendingMap()
//        navigableKeySet()

        //10、闭锁 CountDownLatch是一个并发构造，它允许一个或多个线程等待一系列指定操作的完成。
        //CountDownLatch 以一个给定的数量初始化。countDown() 每被调用一次，这一数量就减一。通过调用 await() 方法之一，线程可以阻塞等待这一数量到达零。
        //以下是一个简单示例。Decrementer 三次调用 countDown() 之后，等待中的 Waiter 才会从 await() 调用中释放出来。
        CountDownLatch latch = new CountDownLatch(3);
        Waiter waiter = new Waiter(latch);
        Decrementer decrementer = new Decrementer(latch);
        new Thread(waiter).start();
        new Thread(decrementer).start();
        Thread.sleep(4000);

        //11、栅栏 CyclicBarrier类是一种同步机制，它能够对处理一些算法的线程实现同步。
        // 换句话讲，它就是一个所有线程必须等待的一个栅栏，直到所有线程都到达这里，然后所有线程才可以继续做其他事情
        CyclicBarrier barrier = new CyclicBarrier(2);
        barrier.await();
        //可以为等待线程设定一个超时时间。等待超过了超时时间之后，即便还没有达成 N 个线程等待 CyclicBarrier 的条件，该线程也会被释放出来
        //barrier.await(10, TimeUnit.SECONDS);
//        满足以下任何条件都可以让等待 CyclicBarrier 的线程释放：
//        最后一个线程也到达 CyclicBarrier(调用 await())
//        当前线程被其他线程打断(其他线程调用了这个线程的 interrupt() 方法)
//        其他等待栅栏的线程被打断
//        其他等待栅栏的线程因超时而被释放
//        外部线程调用了栅栏的 CyclicBarrier.reset() 方法
        // CyclicBarrier 支持一个栅栏行动，栅栏行动是一个 Runnable 实例，一旦最后等待栅栏的线程抵达，该实例将被执行。
        // 可以在 CyclicBarrier 的构造方法中将 Runnable 栅栏行动传给它
//        Runnable barrierAction = new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("do....");
//            }
//        } ;
//        CyclicBarrier barrier2 = new CyclicBarrier(2, barrierAction);
        Runnable barrier1Action = new Runnable() {
            public void run() {
                System.out.println("BarrierAction 1 executed ");
            }
        };
        Runnable barrier2Action = new Runnable() {
            public void run() {
                System.out.println("BarrierAction 2 executed ");
            }
        };
        CyclicBarrier barrier1 = new CyclicBarrier(2, barrier1Action);
        CyclicBarrier barrier2 = new CyclicBarrier(2, barrier2Action);
        CyclicBarrierRunnable barrierRunnable1 = new CyclicBarrierRunnable(barrier1, barrier2);
        CyclicBarrierRunnable barrierRunnable2 = new CyclicBarrierRunnable(barrier1, barrier2);
        new Thread(barrierRunnable1).start();
        new Thread(barrierRunnable2).start();


        //12、交换机 Exchanger 类表示一种两个线程可以进行互相交换对象的会和点
        Exchanger exchanger = new Exchanger();
        ExchangerRunnable exchangerRunnable1 = new ExchangerRunnable(exchanger, "A");
        ExchangerRunnable exchangerRunnable2 = new ExchangerRunnable(exchanger, "B");
        new Thread(exchangerRunnable1).start();
        new Thread(exchangerRunnable2).start();

        //13、信号量 Semaphore 类是一个计数信号量,这就意味着它具备两个主要方法： acquire() release()
        //计数信号量由一个指定数量的 "许可" 初始化。每调用一次 acquire()，一个许可会被调用线程取走。每调用一次 release()，一个许可会被返还给信号量。
        //因此，在没有任何 release() 调用时，最多有 N 个线程能够通过 acquire() 方法，N 是该信号量初始化时的许可的指定数量。
        //Semaphore 用法 信号量主要有两种用途： 保护一个重要(代码)部分防止一次超过 N 个线程进入。 在两个线程之间发送信号
        //---保护重要部分
        //如果你将信号量用于保护一个重要部分，试图进入这一部分的代码通常会首先尝试获得一个许可，然后才能进入重要部分(代码块)，执行完之后，再把许可释放掉。比如这样：
        Semaphore semaphore = new Semaphore(1);
        //critical section
        semaphore.acquire();
        // do things....
        semaphore.release();
        //---在线程之间发送信号
//        如果你将一个信号量用于在两个线程之间传送信号，通常你应该用一个线程调用 acquire() 方法，而另一个线程调用 release() 方法。
//        如果没有可用的许可，acquire() 调用将会阻塞，直到一个许可被另一个线程释放出来。同理，如果无法往信号量释放更多许可时，一个 release() 调用也会阻塞。
//        通过这个可以对多个线程进行协调。比如，如果线程 1 将一个对象插入到了一个共享列表(list)之后之后调用了 acquire()，
//        而线程 2 则在从该列表中获取一个对象之前调用了 release()，这时你其实已经创建了一个阻塞队列。信号量中可用的许可的数量也就等同于该阻塞队列能够持有的元素个数
        //----公平
//        没有办法保证线程能够公平地可从信号量中获得许可。也就是说，无法担保掉第一个调用 acquire() 的线程会是第一个获得一个许可的线程。如果第一个线程在等待一个许可时发生阻塞，而第二个线程前来索要一个许可的时候刚好有一个许可被释放出来，那么它就可能会在第一个线程之前获得许可。
//        如果你想要强制公平，Semaphore 类有一个具有一个布尔类型的参数的构造子，通过这个参数以告知 Semaphore 是否要强制公平。强制公平会影响到并发性能，所以除非你确实需要它否则不要启用它。
//        以下是如何在公平模式创建一个 Semaphore 的示例：Semaphore semaphore = new Semaphore(1, true);
        //---更多方法
//        java.util.concurrent.Semaphore 类还有很多方法，比如：
//        availablePermits() acquireUninterruptibly() drainPermits() hasQueuedThreads() getQueuedThreads() tryAcquire()   等等

        //14、执行器服务 ExecutorService 接口表示一个异步执行机制，使我们能够在后台执行任务。因此一个 ExecutorService 很类似于一个线程池。
        // 实际上，存在于 java.util.concurrent 包里的 ExecutorService 实现就是一个线程池实现
        //---execute(Runnable) 方法要求一个 java.lang.Runnable 对象，然后对它进行异步执行
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        executorService.execute(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task");
            }
        });
        executorService.shutdown();
        //创建ExecutorService实例方法：
        //ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        //ExecutorService executorService2 = Executors.newFixedThreadPool(10);
        //ExecutorService executorService3 = Executors.newScheduledThreadPool(10);

        //---submit(Runnable) 方法也要求一个 Runnable 实现类，但它返回一个 Future 对象。这个 Future 对象可以用来检查 Runnable 是否已经执行完毕。
        Future future1 = executorService.submit(new Runnable() {
            public void run() {
                System.out.println("Asynchronous task");
            }
        });
        future1.get();  //returns null if the task has finished correctly.
        //---submit(Callable) 方法类似于 submit(Runnable) 方法，除了它所要求的参数类型之外。
        // Callable 实例除了它的 call() 方法能够返回一个结果之外和一个 Runnable 很相像。Runnable.run() 不能够返回一个结果。
        //Callable 的结果可以通过 submit(Callable) 方法返回的 Future 对象进行获取。
        Future future2 = executorService.submit(new Callable() {
            public Object call() throws Exception {
                System.out.println("Asynchronous Callable");
                return "Callable Result";
            }
        });
        System.out.println("future2.get() = " + future2.get());
        //---invokeAny() 方法要求一系列的 Callable 或者其子接口的实例对象。调用这个方法并不会返回一个 Future，但它返回其中一个 Callable 对象的结果。
        // 无法保证返回的是哪个 Callable 的结果 - 只能表明其中一个已执行结束。 如果其中一个任务执行结束(或者抛了一个异常)，其他 Callable 将被取消。
        ExecutorService executorService1 = Executors.newSingleThreadExecutor();
        Set<Callable<String>> callables1 = new HashSet<>();
        callables1.add(new Callable<String>() {
            public String call() throws Exception {
                return "Task 1";
            }
        });
        callables1.add(new Callable<String>() {
            public String call() throws Exception {
                return "Task 2";
            }
        });
        callables1.add(new Callable<String>() {
            public String call() throws Exception {
                return "Task 3";
            }
        });
        String result = executorService1.invokeAny(callables1);
        System.out.println("result = " + result);
        executorService1.shutdown();
        //---invokeAll() 方法将调用你在集合中传给 ExecutorService 的所有 Callable 对象。invokeAll() 返回一系列的 Future 对象，通过它们你可以获取每个 Callable 的执行结果。
        //一个任务可能会由于一个异常而结束，因此它可能没有 "成功"。无法通过一个 Future 对象来告知我们是两种结束中的哪一种。
        ExecutorService executorService2 = Executors.newSingleThreadExecutor();
        Set<Callable<String>> callables2 = new HashSet<>();
        callables2.add(new Callable<String>() {
            public String call() throws Exception {
                return "Task 1";
            }
        });
        callables2.add(new Callable<String>() {
            public String call() throws Exception {
                return "Task 2";
            }
        });
        callables2.add(new Callable<String>() {
            public String call() throws Exception {
                return "Task 3";
            }
        });
        List<Future<String>> futures = executorService2.invokeAll(callables2);
        for (Future<String> future : futures) {
            System.out.println("future.get = " + future.get());
        }
        executorService2.shutdown();//立即关闭shutdownNow()

        //15、线程池执行者 ThreadPoolExecutor 是 ExecutorService 接口的一个实现。ThreadPoolExecutor 使用其内部池中的线程执行给定任务(Callable 或者 Runnable)。
        //ThreadPoolExecutor 包含的线程池能够包含不同数量的线程。池中线程的数量由corePoolSize maximumPoolSize变量决定,
        // 当一个任务委托给线程池时，如果池中线程数量低于 corePoolSize，一个新的线程将被创建，即使池中可能尚有空闲线程。
        // 如果内部任务队列已满，而且有至少 corePoolSize 正在运行，但是运行线程的数量低于 maximumPoolSize，一个新的线程将被创建去执行该任务
        int corePoolSize = 5;
        int maxPoolSize = 10;
        long keepAliveTime = 5000;
        ExecutorService threadPoolExecutor =
                new ThreadPoolExecutor(
                        corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<Runnable>()
                );

        //16、定时执行者服务 ScheduledExecutorService 是一个 ExecutorService， 它能够将任务延后执行，或者间隔固定时间多次执行。
        // 任务由一个工作者线程异步执行，而不是由提交任务给 ScheduledExecutorService 的那个线程执行
        // 首先一个内置 5 个线程的 ScheduledExecutorService 被创建。之后一个 Callable 接口的匿名类示例被创建然后传递给 schedule() 方法。
        // 后边的俩参数定义了 Callable 将在 5 秒钟之后被执行
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
        ScheduledFuture scheduledFuture = scheduledExecutorService.schedule(
                new Callable() {
                    public Object call() throws Exception {
                        System.out.println("Executed!");
                        return "Called!";
                    }
                },
                5,
                TimeUnit.SECONDS);
        System.out.println("result = " + scheduledFuture.get());
        scheduledExecutorService.shutdown();


    }
}

class Producer implements Runnable {

    protected BlockingQueue<String> queue = null;

    public Producer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            queue.put("1");
            Thread.sleep(1000);
            queue.put("2");
            Thread.sleep(1000);
            queue.put("3");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Consumer implements Runnable {

    protected BlockingQueue<String> queue = null;

    public Consumer(BlockingQueue<String> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            System.out.println(queue.take());
            System.out.println(queue.take());
            System.out.println(queue.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Waiter implements Runnable {

    CountDownLatch latch = null;

    public Waiter(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Waiter Released");
    }
}

class Decrementer implements Runnable {

    CountDownLatch latch = null;

    public Decrementer(CountDownLatch latch) {
        this.latch = latch;
    }

    public void run() {

        try {
            Thread.sleep(1000);
            this.latch.countDown();

            Thread.sleep(1000);
            this.latch.countDown();

            Thread.sleep(1000);
            this.latch.countDown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class CyclicBarrierRunnable implements Runnable {

    CyclicBarrier barrier1 = null;
    CyclicBarrier barrier2 = null;

    public CyclicBarrierRunnable(CyclicBarrier barrier1, CyclicBarrier barrier2) {
        this.barrier1 = barrier1;
        this.barrier2 = barrier2;
    }

    public void run() {
        try {
            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() +
                    " waiting at barrier 1");
            this.barrier1.await();

            Thread.sleep(1000);
            System.out.println(Thread.currentThread().getName() +
                    " waiting at barrier 2");
            this.barrier2.await();

            System.out.println(Thread.currentThread().getName() +
                    " done!");

        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

class ExchangerRunnable implements Runnable {
    Exchanger exchanger = null;
    Object    object    = null;

    public ExchangerRunnable(Exchanger exchanger, Object object) {
        this.exchanger = exchanger;
        this.object = object;
    }

    public void run() {
        try {
            Object previous = this.object;

            this.object = this.exchanger.exchange(this.object);

            System.out.println(Thread.currentThread().getName() + " exchanged " + previous + " for " + this.object);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}