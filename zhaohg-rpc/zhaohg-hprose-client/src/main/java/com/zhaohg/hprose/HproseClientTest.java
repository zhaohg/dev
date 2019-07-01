package com.zhaohg.hprose;

import com.zhaohg.hprose.client.IHelloService;
import hprose.client.HproseClient;

import java.io.IOException;
import java.net.URISyntaxException;


public class HproseClientTest {

    public static void main(String[] args) throws IOException, URISyntaxException {
//        HproseHttpClient client = new HproseHttpClient();
//        client.useService("http://localhost:8080/hello");
//        String result = (String) client.invoke("sayHello", new com.zhaohg.object[]{"Hprose"});
//        System.out.println(result);
//        result = (String) client.invoke("sayHello", new com.zhaohg.object[]{"中国"});
//        System.out.println(result);

        HproseClient client = HproseClient.create("tcp://127.0.0.1:4321");
        IHelloService helloService = client.useService(IHelloService.class);
        System.out.println(helloService.sayHello());
//        List<Order> list = helloService.getOrderList();
//        System.out.println(list.get(0).getPrice());
//        Threads.runShutdownHandler();
    }
}
