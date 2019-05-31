package com.zhaohg.jvm.heap;

import org.junit.Test;

public class HeapTest {
    @Test
    public void testHeap() {
        Object obj = new Object();
        System.gc();
        System.out.println();
        obj = new Object();
        obj = new Object();
        System.gc();
        System.out.println();
    }
}
