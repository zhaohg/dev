package com.zhaohg.statistic.monodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.zhaohg.dao.mongodb.util.MongoClientOptionsBuild;
import com.zhaohg.dao.mongodb.util.MongodbManager;
import org.apache.commons.beanutils.BeanMap;

import java.util.Date;
import java.util.Map;

/**
 * Created by zhaohg on 2018/7/20.
 */
public class mongo {

    public static void main(String[] args) {
        MongoClientOptionsBuild build = new MongoClientOptionsBuild();
        build.setConnectionsPerHost(20);
        build.setConnectTimeout(10000);
        build.setMaxWaitTime(120000);
        build.setSocketKeepAlive(false);
        build.setSocketTimeout(0);
        build.setThreadsAllowed(10);

        MongodbManager manager = new MongodbManager();
        manager.setOptions(build);
        manager.setHost("127.0.0.1");
        manager.setPort(27017);
        manager.setDbName("acaomei");

        DB db = manager.getDB("acaomei");

        Order order = new Order();
        order.setId(1L);
        order.setName("ordername1");
        order.setTime(new Date());

        mongoInsert(manager, order);
        mongoQuery(manager);
    }

    private static void mongoInsert(MongodbManager manager, Object object) {

        DBCollection dbCollection = manager.getDbCollection(MongodbCollections.ORDER_DATA);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.putAll(new BeanMap(object));
        if (basicDBObject.containsField("class")) { // 去除class属性
            basicDBObject.remove("class");
        }
        dbCollection.insert(basicDBObject);
    }

    private static void mongoQuery(MongodbManager manager) {
        DBObject query = new BasicDBObject();
        query.put("id", 1L);

        DBObject orderBy = new BasicDBObject();
        orderBy.put("id", 1);

        DBCursor cursor = manager.getCollection(MongodbCollections.ORDER_DATA).find(query)
                .sort(orderBy);
        while (cursor.hasNext()) {
            Map map = cursor.next().toMap();

            System.out.println(JSON.serialize(map));

        }
    }
}
