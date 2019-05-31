package com.zhaohg.hessian.server;

import com.zhaohg.hessian.model.Order;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by com.zhaohg on 2018/8/29.
 */
@Service
public class HelloService implements IHelloService {

    private String message = "balalala！";

    @Override
    public String sayHello() {
        return message;
    }

    @Override
    public List<Order> getOrderList() {

        List<Order> list = new ArrayList<>();
        list.add(new Order(1L, "name1", new BigDecimal(123.123), false,
                32.03f, 2312.22, 1, (short) 3));

        return list;
    }

}