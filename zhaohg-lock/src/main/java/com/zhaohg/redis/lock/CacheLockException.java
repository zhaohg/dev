package com.zhaohg.redis.lock;

public class CacheLockException extends Throwable {
    private String msg;

    public CacheLockException(String msg) {
        this.msg = msg;
    }

    public CacheLockException() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
