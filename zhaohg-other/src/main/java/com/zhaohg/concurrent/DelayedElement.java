package com.zhaohg.concurrent;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaohg on 2018-11-14.
 */
public class DelayedElement implements Delayed {
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.toSeconds(1000);
    }

    @Override
    public int compareTo(Delayed delayed) {
        return 0;
    }
}
