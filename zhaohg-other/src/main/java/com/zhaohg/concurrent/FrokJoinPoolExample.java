package com.zhaohg.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Created by zhaohg on 2018-11-14.
 */
public class FrokJoinPoolExample {

    public static void main(String[] args) {

        //17.使用 ForkJoinPool 进行分叉和合并
        //ForkJoinPool 在 Java 7 中被引入。它和 ExecutorService 很相似，除了一点不同。ForkJoinPool 让我们可以很方便地把任务分裂成几个更小的任务，
        //这些分裂出来的任务也将会提交给 ForkJoinPool。任务可以继续分割成更小的子任务，只要它还能分割。
        //可能听起来有些抽象，ForkJoinPool 是如何工作的，还有任务分割是如何进行的。
        //这个示例创建了一个并行级别为 4 的 ForkJoinPool
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        //RecursiveAction 是一种没有任何返回值的任务。它只是做一些工作，比如写数据到磁盘，然后就退出了。
        //一个 RecursiveAction 可以把自己的工作分割成更小的几块，这样它们可以由独立的线程或者 CPU 执行。
        MyRecursiveAction1 myRecursiveAction1 = new MyRecursiveAction1(24);
        forkJoinPool.invoke(myRecursiveAction1);

        //RecursiveTask 是一种会返回结果的任务。它可以将自己的工作分割为若干更小任务，并将这些子任务的执行结果合并到一个集体结果。可以有几个水平的分割和合并
        MyRecursiveTask myRecursiveTask = new MyRecursiveTask(128);
        long mergedResult = forkJoinPool.invoke(myRecursiveTask);
        System.out.println("mergedResult = " + mergedResult);

    }

}

class MyRecursiveAction1 extends RecursiveAction {

    private long workLoad = 0;

    MyRecursiveAction1(long workLoad) {
        this.workLoad = workLoad;
    }

    @Override
    protected void compute() {

        //if work is above threshold, break tasks up into smaller tasks
        if (this.workLoad > 16) {
            System.out.println("Splitting workLoad1 : " + this.workLoad);

            List<MyRecursiveAction1> subtasks = new ArrayList<>();

            subtasks.addAll(createSubtasks());

            for (RecursiveAction subtask : subtasks) {
                subtask.fork();
            }

        } else {
            System.out.println("Doing workLoad myself1: " + this.workLoad);
        }
    }

    private List<MyRecursiveAction1> createSubtasks() {
        List<MyRecursiveAction1> subtasks = new ArrayList<>();

        MyRecursiveAction1 subtask1 = new MyRecursiveAction1(this.workLoad / 2);
        MyRecursiveAction1 subtask2 = new MyRecursiveAction1(this.workLoad / 2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;
    }
}

class MyRecursiveTask extends RecursiveTask<Long> {

    private long workLoad = 0;

    public MyRecursiveTask(long workLoad) {
        this.workLoad = workLoad;
    }

    protected Long compute() {

        //if work is above threshold, break tasks up into smaller tasks
        if (this.workLoad > 16) {
            System.out.println("Splitting workLoad2 : " + this.workLoad);

            List<MyRecursiveTask> subtasks = new ArrayList<>();
            subtasks.addAll(createSubtasks());

            for (MyRecursiveTask subtask : subtasks) {
                subtask.fork();
            }

            long result = 0;
            for (MyRecursiveTask subtask : subtasks) {
                result += subtask.join();
            }
            return result;

        } else {
            System.out.println("Doing workLoad myself2: " + this.workLoad);
            return workLoad * 3;
        }
    }

    private List<MyRecursiveTask> createSubtasks() {
        List<MyRecursiveTask> subtasks = new ArrayList<>();

        MyRecursiveTask subtask1 = new MyRecursiveTask(this.workLoad / 2);
        MyRecursiveTask subtask2 = new MyRecursiveTask(this.workLoad / 2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;
    }
}