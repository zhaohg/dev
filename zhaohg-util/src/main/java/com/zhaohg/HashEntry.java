package com.zhaohg;

/**
 * Created by zhaohg on 2017/8/22.
 */
class HashEntry {
    private final Object    key;
    private       Object    value;
    private       HashEntry next;

    public HashEntry(Object key, Object value, HashEntry next) {
        this.key = key;
        this.value = value;
        this.next = next;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public HashEntry getNext() {
        return next;
    }

    public void setNext(HashEntry next) {
        this.next = next;
    }
}