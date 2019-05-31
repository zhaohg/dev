package com.zhaohg.dao.impl;

import com.alibaba.fastjson.JSON;
import com.mongodb.BulkWriteResult;
import com.zhaohg.dao.IUserDao;
import com.zhaohg.model.Account;
import com.zhaohg.model.MessageBatch;
import com.zhaohg.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Circle;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicUpdate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao implements IUserDao {
    
    private static final String        COLLECTION_NAME = "user";
    @Resource
    private              MongoTemplate mongoTemplate;
    
    @Override
    public void insert(User user) {
        mongoTemplate.insert(user, COLLECTION_NAME);

//        IndexOperations indexOps = mongoTemplate.indexOps(User.class);
//
////        indexOps.dropIndex("");//删除索引
//        Index index = new Index();
//        index.on("nickname", Sort.Direction.ASC);//为nickname属性加上 索引
//
//        new Sort.Order(Sort.Direction.DESC, "nickname");
//        index.background();
//        index.unique(Index.Duplicates.RETAIN);//唯一索引
//        index.named("name_1");
//
//        indexOps.ensureIndex(index);//复合索引
    
    }
    
    @Override
    public void insertUserList(List<User> users) {
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, COLLECTION_NAME);
        for (User user : users) {
            //注意此处的obj必须是一个DBObject，可以是json对象也可以的bson对象，entity没有试过
            ops.insert(user);
            
            //更新
            Update update = new BasicUpdate("");
            //.....
            ops.updateOne(new Query(Criteria.where("id").is(user.getId())), update);
            
        }
        //循环插完以后批量执行提交一下ok！
        BulkWriteResult a = ops.execute();
        
        
    }
    
    @Override
    public void createCollection() {
        mongoTemplate.createCollection(COLLECTION_NAME);
        
    }
    
    @Override
    public void remove(Map params) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(params.get("id")).and("")), User.class, COLLECTION_NAME);
    }
    
    @Override
    public void update(Map params) {
        mongoTemplate.updateMulti(new Query(Criteria.where("id").is(params.get("id"))), new Update().set("nickname", params.get("name")), User.class, COLLECTION_NAME);
    }
    
    @Override
    public User findOne(Map params) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(params.get("id"))), User.class, COLLECTION_NAME);
    }
    
    @Override
    public List<User> findAll(Map params) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(1));
        query.fields().include("name");
        query.with(new Sort(Sort.Direction.ASC, "id"));
        Circle circle = new Circle(-73.99171, 40.738868, 0.01);
        List<String> user = mongoTemplate.find(query, String.class, COLLECTION_NAME);
        
        //聚合
        Aggregation aggregation = Aggregation.newAggregation(Aggregation
                .group("name").sum("age").as("totalAge")
                .sum("gender").as("TotalGender")
        );
        
        AggregationResults<Object> aggRes = mongoTemplate.aggregate(aggregation, COLLECTION_NAME, Object.class);
        List<Object> listRes = aggRes.getMappedResults();
        
        for (Object obj : listRes) {
            System.out.println(JSON.toJSONString(obj));
        }
        System.out.println(listRes.size());
        
        Aggregation aggregation1 = Aggregation.newAggregation(Aggregation
                .group("name").sum("age").as("totalAge")
                .sum("gender").as("TotalGender")
        );
        
        return null;
    }
    
    
    @Override
    public Object findOne1() {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(1)), Object.class, COLLECTION_NAME);
    }
    
    public void insertAccount(Account accounts) {
        mongoTemplate.insert(accounts);
    }
    
    public List<User> findUserAccountById() {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("account.id").is(1L));
        
        Sort sort = new Sort(Sort.Direction.DESC, "id ,account.id");
        
        Query query = new Query();
        query.addCriteria(criteria);
        query.with(sort);
        query.skip(0);
        query.limit(10);
        
        return mongoTemplate.find(query, User.class);
    }
    
    public void insertMessages(List<MessageBatch> messageBatches) {
        mongoTemplate.insertAll(messageBatches);
    }
}