package com.zhaohg.io.netty.ticket;

import java.io.Serializable;
import java.util.Date;

/**
 * 订票人发送查询余票和订票使用的请求信息
 * Created by zhaohg on 2017-11-21.
 */

public class BookRequestMsg implements Serializable {
    private static final long   serialVersionUID = -7335293929249462183L;
    private              User   user;//发送订票信息用户
    private              String trainNumber;//火车车次
    private              int    code;//查询命令
    private              Date   startTime;//开车时间

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(String trainNumber) {
        this.trainNumber = trainNumber;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
