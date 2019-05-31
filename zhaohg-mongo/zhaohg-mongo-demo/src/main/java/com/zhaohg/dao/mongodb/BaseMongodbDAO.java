package com.zhaohg.dao.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.zhaohg.dao.mongodb.util.MongodbManager;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class BaseMongodbDAO implements IMongodbDAO {

    @Autowired
    protected MongodbManager mongodbManager;

    public void setMongodbManager(MongodbManager mongodbManager) {
        this.mongodbManager = mongodbManager;
    }


    public List<Long> getFieldFromDbList(List<DBObject> dbObjects, String field) {
        List<Long> uids = new ArrayList<Long>();
        for (DBObject dbObject : dbObjects) {
            if (dbObject instanceof BasicDBObject) {
                uids.add(((BasicDBObject) dbObject).getLong(field));
            }
        }
        return uids;
    }

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
        dbCollection.insert(basicDBObject);
    }

    public void insertAll(Collection<?> collection, String collName) {
        if (CollectionUtils.isEmpty(collection)) {
            return;
        }
        DBCollection dbCollection = mongodbManager.getDbCollection(collName);
        List<DBObject> list = new ArrayList<DBObject>();
        for (Object object : collection) {
            BasicDBObject basicDBObject = new BasicDBObject();
            basicDBObject.putAll(new BeanMap(object));
            if (basicDBObject.containsField("class")) { // 去除class属性
                basicDBObject.remove("class");
            }
            list.add(basicDBObject);
        }
        dbCollection.insert(list);
    }


    public void remove(Map<String, Object> query, String collName) {
        if (query == null) {
            return;
        }
        DBCollection dbCollection = mongodbManager.getDbCollection(collName);
        dbCollection.remove(new BasicDBObject(query));
    }

    public void update(Map<String, Object> findMap,
                       Map<String, Object> updateMap, String collName) {
        if (findMap == null || updateMap == null) {
            return;
        }
        DBCollection dbCollection = mongodbManager.getDbCollection(collName);
        BasicDBObject findDBObject = new BasicDBObject(findMap);
        BasicDBObject updateDBObject = new BasicDBObject(updateMap);
        dbCollection.update(findDBObject, new BasicDBObject("$set", updateDBObject), false, true);
    }

    public void updateAll(BasicDBObject findDbObject,
                          BasicDBObject updateDBObject, String collName) {
        DBCollection dbCollection = mongodbManager.getDbCollection(collName);
//		BasicDBObject findDbObject = new BasicDBObject(findMap);
//		BasicDBObject updateDBObject = new BasicDBObject(updateMap);
        WriteResult result = dbCollection.update(findDbObject, new BasicDBObject("$set", updateDBObject), false, true);
        System.out.println("update mongodb,count=" + result.getN() + ",findDbObject=" + findDbObject.toString() + ",updateDBObject=" + updateDBObject.toString());
    }

}
