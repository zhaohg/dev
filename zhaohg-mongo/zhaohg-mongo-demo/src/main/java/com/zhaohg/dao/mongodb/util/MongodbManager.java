package com.zhaohg.dao.mongodb.util;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * Created by zhaohg on 2018/7/20.
 */
public class MongodbManager {

    private MongoClient             mongoClient;
    private String                  host = "127.0.0.1";
    private int                     port = 27017;
    private String                  dbName;
    private MongoClientOptionsBuild options;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setOptions(MongoClientOptionsBuild options) {
        this.options = options;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    private synchronized void getInstance() {
        if (mongoClient == null) {
            try {
                mongoClient = new MongoClient(new ServerAddress(host, port), options.getMongoClientOptions());
            } catch (Exception e) {
                throw new RuntimeException("mongodb 配置错误", e);
            }
        }
    }

    private MongoClient getMongoClient() {
        if (mongoClient == null) {
            getInstance();
        }
        return mongoClient;
    }

    public DB getDB(String dbName) {
        return getMongoClient().getDB(dbName);
    }

    public DBCollection getCollection(String collName) {
        return getDB(dbName).getCollection(collName);
    }

    public DBCollection getDbCollection(String collName) {
        return getCollection(collName);
    }

    public DBCollection getCollection(String dbName, String collName) {
        return getDB(dbName).getCollection(collName);
    }

}
