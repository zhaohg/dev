package com.zhaohg.io.netty.ticket;

import java.io.Serializable;
import java.util.Date;

/**
 * 订票成功与否反馈信息
 * <p>
 * Created by zhaohg on 2017-11-21.
 */

public class BookResponseMsg implements Serializable {
    private static final long    serialVersionUID = -4984721370227929766L;
    private              boolean success;//是否操作成功
    private              User    user;//请求用户
    private              String  msg;//反馈信息
    private              int     code;//请求指令
    private              Train   train;//火车车次
    private              Date    startTime;//出发时间
    private              Ticket  ticket;//订票成功后具体出票票据

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}