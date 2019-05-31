package com.zhaohg.redis;

import java.io.Serializable;

/**
 * @param <T>
 * @author zhaohg
 * @Date 2016-8-22
 * @Time 上午10:05:44
 */
public class VO<T> implements Serializable {
    private T value;

    public VO(T value) {
        this.value = value;
    }

    public VO() {
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "VO{" +
                "value=" + value +
                '}';
    }
}