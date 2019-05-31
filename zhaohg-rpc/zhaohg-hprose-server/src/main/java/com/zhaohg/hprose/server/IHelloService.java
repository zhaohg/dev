package com.zhaohg.hprose.server;

import com.zhaohg.hprose.model.Order;

import java.util.List;

/**
 * Created by com.zhaohg on 2018/8/29.
 */
public interface IHelloService {
    public String sayHello();

    List<Order> getOrderList();
}