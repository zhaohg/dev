package com.zhaohg.dao.mongodb.util;

import com.mongodb.MongoClientOptions;

/**
 * Created by zhaohg on 17-07-20.
 */
public class MongoClientOptionsBuild {

    private MongoClientOptions mongoClientOptions;

    private int     connectionsPerHost;         //每个主机的连接数
    private int     connectTimeout;             //连接超时的毫秒
    private int     socketTimeout;              //socket超时
    private int     maxWaitTime;                //线程等待链接可用的最大等待毫秒数
    private boolean socketKeepAlive;        //用于控制socket保持活动的功能
    private int     threadsAllowed;             // 此参数跟connectionsPerHost的乘机为一个线程变为可用的最大阻塞数

    public MongoClientOptions getMongoClientOptions() {
        if (mongoClientOptions == null) {
            mongoClientOptions = MongoClientOptions.builder()
                    .connectionsPerHost(connectionsPerHost)
                    .connectTimeout(connectTimeout)
                    .socketTimeout(socketTimeout)
                    .maxWaitTime(maxWaitTime)
                    .socketKeepAlive(socketKeepAlive)
                    .threadsAllowedToBlockForConnectionMultiplier(threadsAllowed)
                    .build();
        }
        return mongoClientOptions;
    }

    public void setConnectionsPerHost(int connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public void setThreadsAllowed(int threadsAllowed) {
        this.threadsAllowed = threadsAllowed;
    }
}
