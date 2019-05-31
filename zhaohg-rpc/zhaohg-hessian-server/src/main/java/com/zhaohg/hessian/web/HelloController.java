package com.zhaohg.hessian.web;

import com.zhaohg.hessian.server.IHelloService;
import com.zhaohg.hessian.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by zhaohg on 2018/8/29.
 */
@RestController
public class HelloController {

    @Autowired
    public IHelloService helloService;

    @RequestMapping("/say_hello")
    public Result sayHello() {
        System.out.println("-----------");
        return new Result(helloService.sayHello());
    }
}
