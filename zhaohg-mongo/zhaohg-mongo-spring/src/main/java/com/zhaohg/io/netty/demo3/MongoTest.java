package com.zhaohg.io.netty.demo3;


import com.alibaba.fastjson.JSONObject;
import com.zhaohg.dao.impl.UserDao;
import com.zhaohg.model.Account;
import com.zhaohg.model.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by com.zhaohg on 2018/7/20.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring-mongo.xml"})
public class MongoTest {

    @Autowired
    private UserDao userDao;

    @Test
    public void testMongodbInsert() throws Exception {
        User user = new User();
        user.setId(2L);
        user.setName("zhaohg2");
        user.setEmail("zhaohg2@163.com");
        user.setMobile("15088655458");
        user.setAge(20);
        user.setGender(0);
        user.setCreateTime(new Date());
        userDao.insert(user);
    }

    @Test
    public void testMongodbDBRef() throws Exception {

        Account account = new Account();
        account.setId(2L);
        account.setCard("1111111111111");
        account.setAmount(new BigDecimal(10));
        account.setCreateTime(new Date());
        userDao.insertAccount(account);

        User user = new User();
        user.setId(3L);
        user.setName("zhaohg1");
        user.setEmail("zhaohg1@163.com");
        user.setMobile("15088655357");
        user.setAge(21);
        user.setGender(3);
        user.setCreateTime(new Date());
        user.setAccount(account);
        userDao.insert(user);
    }

    @Test
    public void testFind() {
        List<User> users = userDao.findUserAccountById();
        System.out.println(users);
    }

    @Test
    public void testMongodbfind() throws Exception {
        Map map = new HashMap<>();
        map.put("id", 1);

        User findUser = userDao.findOne(map);
        System.out.println(findUser.getCreateTime());
        System.out.println(JSONObject.toJSONString(findUser));
    }

    @Test
    public void testMongodbfind1() throws Exception {

        Object object = userDao.findOne1();
        System.out.println(JSONObject.toJSONString(object));
    }

    @Test
    public void testMongodbUpdate() throws Exception {
        Map map = new HashMap<>();
        map.put("id", 1);
        map.put("name", "com.zhaohg");

        userDao.update(map);
    }

    @Test
    public void testMongodbRemove() throws Exception {
        Map map = new HashMap<>();
        map.put("id", 1L);

        userDao.remove(map);
    }

    @Test
    public void testMongodbfindAll() throws Exception {
        Map map = new HashMap<>();
        map.put("name", "nickname1");

        List<User> list = userDao.findAll(map);
        System.out.println(list != null ? list.size() : 0);
    }

    @Test
    public void test() {

    }

}
