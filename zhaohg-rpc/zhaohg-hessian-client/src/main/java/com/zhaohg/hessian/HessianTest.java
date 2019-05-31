package com.zhaohg.hessian;

import com.caucho.hessian.client.HessianProxyFactory;
import com.zhaohg.hessian.client.IHelloService;
import com.zhaohg.hessian.model.Order;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by zhaohg on 2018/8/29.
 */

public class HessianTest {
    
    @Test
    public void HessianProxyBeanTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        IHelloService helloService = (IHelloService) context.getBean("helloService");
        System.out.println(helloService.sayHello());
        List<Order> list = helloService.getOrderList();
        for (Order order : list) {
            System.out.println(order.getPrice().toString());
            System.out.println(order.getDoubl());
            System.out.println(order.getName());
        }
//        在 hessian.jar 的 META-INF/hessian 目录下加入 serializers 和 deserializers 这两个文件,  两个文件的内容如下：
//        serializers
//        java.math.BigDecimal=com.caucho.hessian.io.StringValueSerializer

//        deserializers
//        java.math.BigDecimal=com.caucho.hessian.io.BigDecimalDeserializer
    }
    
    @Test
    public void HessianClientTest() throws MalformedURLException {
        String url = "http://localhost:8080/helloService";
        HessianProxyFactory factory = new HessianProxyFactory();
        IHelloService helloService = (IHelloService) factory.create(IHelloService.class, url);
        System.out.println(helloService.sayHello());
        List<Order> list = helloService.getOrderList();
        for (Order order : list) {
            System.out.println(order.getPrice());
            
        }
    }
    
    
}
