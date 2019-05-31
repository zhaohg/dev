package com.zhaohg.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by com.zhaohg on 2018/7/21.
 */
public interface BaseMongo<T> {
    //添加
    public void insert(T object);

    //根据条件查找
    public T findOne(Map<String, Object> params);

    //查找所有
    public List<T> findAll(Map<String, Object> params);

    //修改
    public void update(Map<String, Object> params);

    //创建集合
    public void createCollection();

    //根据条件删除
    public void remove(Map<String, Object> params);

}
