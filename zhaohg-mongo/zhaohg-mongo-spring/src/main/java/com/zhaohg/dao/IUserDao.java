package com.zhaohg.dao;

import com.zhaohg.model.User;

import java.util.List;
import java.util.Map;

/**
 * Created by com.zhaohg on 2018/7/21.
 */
public interface IUserDao {


    void insert(User object);

    void insertUserList(List<User> list);

    void createCollection();

    void remove(Map params);

    void update(Map params);

    User findOne(Map params);

    List<User> findAll(Map params);

    Object findOne1();
}
