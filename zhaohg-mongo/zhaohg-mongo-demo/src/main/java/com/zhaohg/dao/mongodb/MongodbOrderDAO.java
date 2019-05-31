package com.zhaohg.dao.mongodb;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.zhaohg.statistic.monodb.MongodbCollections;
import com.zhaohg.statistic.monodb.Order;
import org.apache.commons.beanutils.BeanMap;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
@Repository
public class MongodbOrderDAO extends BaseMongodbDAO {


    /**
     * 获取一条订单
     * @param name
     * @param sort -1.倒叙 1.升序
     * @return
     */
    public Order getOrder(String name, int sort) {

        BasicDBObject query = new BasicDBObject(2);
        query.put("name", name);

        DBObject orderBy = new BasicDBObject();
        orderBy.put("time", sort);

        DBCursor cursor = super.mongodbManager.getCollection(MongodbCollections.ORDER_DATA).find(query)
                .sort(orderBy).limit(1);
        System.out.println("----" + JSONObject.toJSONString(cursor));
        while (cursor.hasNext()) {
            return new Order(cursor.next().toMap());
        }
        return null;
    }

    public int countOrder(BasicDBObject query) {
        return mongodbManager.getCollection(MongodbCollections.ORDER_DATA).find(query).count();
    }

    public List<Order> queryOrderList(BasicDBObject query, int start, int limit) {
        DBObject orderBy = new BasicDBObject();
        orderBy.put("time", -1);
        DBCursor cursor = mongodbManager.getCollection(MongodbCollections.ORDER_DATA).find(query).sort(orderBy)
                .skip(start).limit(limit);
        ArrayList<Order> liveStreamModels = new ArrayList<>(limit);
        while (cursor.hasNext()) {
            liveStreamModels.add(new Order(cursor.next().toMap()));
        }

        System.out.println("----" + liveStreamModels);

        return liveStreamModels;
    }

    @Override
    public void insert(Object object, String collName) {
        if (object == null) {
            return;
        }
        DBCollection dbCollection = mongodbManager.getDbCollection(collName);
        BasicDBObject basicDBObject = new BasicDBObject();
        basicDBObject.putAll(new BeanMap(object));
        if (basicDBObject.containsField("class")) { // 去除class属性
            basicDBObject.remove("class");
        }
        if (basicDBObject.containsField("empty")) {
            basicDBObject.remove("empty");
        }
        dbCollection.insert(basicDBObject);
    }

}
