package com.zhaohg.thread;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

//获取线程的返回值
//通过FutureTask包装一个Callable的实例，再通过Thread包装FutureTask的实例，然后调用Thread的start()方法，且看示例如下 
public class ThreadTest01 implements Callable {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Object call() throws Exception {
        for (int i = 0; i <= 100; i++) {
            //Do something what U want
            atomicInteger.set(atomicInteger.get() + 1);
        }
        return atomicInteger.get();
    }

    @org.junit.Test
    public void test() throws ExecutionException, InterruptedException {
        FutureTask future = new FutureTask(new ThreadTest01());
        Thread thread = new Thread(future);
        thread.start();
        if (future.isDone()) {
            future.cancel(true);
        }
        System.out.println(future.get());
    }
}

//通过ExecutorService执行线程
class ThreadTest02 implements Callable {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Object call() throws Exception {
        for (int i = 0; i <= 100; i++) {
            //Do something what U want
            atomicInteger.set(atomicInteger.get() + 1);
        }
        return atomicInteger.get();
    }

    @org.junit.Test
    public void test() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future submit = executorService.submit(new ThreadTest02());
        System.out.println(submit.get());
    }
}

//批量提交任务
class ThreadTest03<T> implements Callable {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Object call() throws Exception {
        synchronized (this) {
            for (int i = 0; i <= 100; i++) {
                //Do something what U want
                atomicInteger.set(atomicInteger.get() + 1);
            }
            TimeUnit.SECONDS.sleep(1);
        }
        return atomicInteger.get();
    }


    @org.junit.Test
    public void test() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<T>> tasks = new ArrayList<>();
        ThreadTest03 threadTest = new ThreadTest03();
        for (int i = 0; i < 5; i++) {
            tasks.add(threadTest);
        }
        //该方法返回某个完成的任务
        Object o = executorService.invokeAny(tasks);
        System.out.println(o);
        System.out.println("One completed!");
        long start = System.currentTimeMillis();
        threadTest = new ThreadTest03();
        tasks.clear();
        for (int i = 0; i < 5; i++) {
            tasks.add(threadTest);
        }
        List<Future<T>> futures = executorService.invokeAll(tasks);
        long end = System.currentTimeMillis();
        System.out.println(end - start + " ms之后，返回运行结果！");
        for (int i = 0; i < 5; i++) {
            System.out.println(futures.get(i).get());
        }
    }
}

//通过CompletionService提交多组任务并获取返回值
class ThreadTest04 implements Callable {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Object call() throws Exception {
        synchronized (this) {
            for (int i = 0; i <= 100; i++) {
                //Do something what U want
                atomicInteger.set(atomicInteger.get() + 1);
            }
            TimeUnit.SECONDS.sleep(1);
        }
        return atomicInteger.get();
    }

    @org.junit.Test
    public void test() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<Integer> completionService = new ExecutorCompletionService(executorService);
        ThreadTest04 threadTest = new ThreadTest04();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; ++i) {
            completionService.submit(threadTest);//提交五组任务
        }
        long end = System.currentTimeMillis();
        System.out.println(end - start + " ms之后，返回运行结果！");
        for (int i = 0; i < 5; ++i) {
            Integer res = completionService.take().get();//通过take
            System.out.println(res);
        }
    }
}

//通过自维护列表管理多组任务并获取返回值
class ThreadTest05 implements Callable {
    private AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public Object call() throws Exception {
        for (int i = 0; i <= 100; i++) {
            //Do something what U want
            atomicInteger.set(atomicInteger.get() + 1);
        }
        return atomicInteger.get();
    }


    @org.junit.Test
    public void test() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        List<Future> futures = new ArrayList<>();
        ThreadTest05 threadTest05 = new ThreadTest05();
        for (int i = 0; i < 5; ++i) {
            Future submit = executorService.submit(threadTest05);
            futures.add(submit);
        }
        Iterator<Future> iterator = futures.iterator();
        while (iterator.hasNext()) {
            Future next = iterator.next();
            System.out.println(next.get());
        }
    }
}

//ScheduledExecutor任务调度
//ScheduledExecutor提供了基于开始时间与重复间隔的任务调度，可以实现简单的任务调度需求。
// 每一个被调度的任务都会由线程池中一个线程去执行，因此任务是并发执行的，相互之间不会受到干扰。
// 需要注意的是，只有当任务的执行时间到来时，ScheduedExecutor才会真正启动一个线程，
// 其余时间ScheduledExecutor都是在轮询任务的状态。
class ScheduledExecutorTest implements Runnable {
    private String jobName = "";

    public ScheduledExecutorTest(String jobName) {
        super();
        this.jobName = jobName;
    }

    public static void main(String[] args) {
        ScheduledExecutorService service = Executors.newScheduledThreadPool(10);

        long initialDelay1 = 1;
        long period1 = 1;
        // 从现在开始1秒钟之后，每隔1秒钟执行一次job1
        service.scheduleAtFixedRate(
                new ScheduledExecutorTest("job" + period1), initialDelay1,
                period1, TimeUnit.SECONDS);
        long initialDelay2 = 2;
        long period2 = 2;
        // 从现在开始2秒钟之后，每隔2秒钟执行一次job2
        service.scheduleWithFixedDelay(
                new ScheduledExecutorTest("job" + period2), initialDelay2,
                period2, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        System.out.println("execute " + jobName);
    }
}
//取消向线程池提交的某个任务
//    在ExecutorService中提供了submit()方法，用于向线程池提交任务，该方法返回一个包含结果集的Future实例。
// 而Future提供了cancel(boolean mayInterruptIfRunning)方法用于取消提交的运行任务，如果向该函数传递true，
// 那么不管该任务是否运行结束，立即停止，如果向该函数传递false，那么等待该任务运行完成再结束之。
// 同样Future还提供了isDone()用于测试该任务是否结束，isCancelled()用于测试该任务是否在运行结束前已取消。


//关闭线程池
//    在ExecutorService中提供了shutdown()和List<Runnable> shutdownNow()方法用来关闭线程池，
// 前一个方法将启动一次顺序关闭，有任务在执行的话，则等待该任务运行结束，同时不再接受新任务运行。
// 后一个方法将取消所有未开始的任务并且试图中断正在执行的任务，返回从未开始执行的任务列表，不保证能够停止正在执行的任务，
// 但是会尽力尝试。例如，通过Thread.interrupt()来取消正在执行的任务，但是对于任何无法响应中断的任务，都可能永远无法终止
