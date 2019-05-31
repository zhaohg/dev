package com.zhaohg.hessian.server;

import com.zhaohg.hessian.model.Order;

import java.util.List;

/**
 * Created by com.zhaohg on 2018/8/29.
 */
public interface IHelloService {
    public String sayHello();

    List<Order> getOrderList();
}