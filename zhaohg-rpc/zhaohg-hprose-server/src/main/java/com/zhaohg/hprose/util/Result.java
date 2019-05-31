package com.zhaohg.hprose.util;

/**
 * Created by zhaohg on 2018/8/29.
 */

public class Result {
    private long   time;
    private Object data;
    private int    code;
    private String message;

    public Result() {
        this.time = System.currentTimeMillis();
    }

    public Result(Object data) {
        this.data = data;
        this.code = 200;
        this.time = System.currentTimeMillis();
    }

    public Result(Object data, String msg) {
        this(data);
        this.message = msg;
    }

    public Result(Object data, StatusCode.Status status) {
        setData(data);
        setCode(status.getCode());
        setMessage(status.getMsg());
        setTime(System.currentTimeMillis());
    }


    public Result build(int code, String msg) {
        this.code = code;
        this.message = msg;
        return this;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
