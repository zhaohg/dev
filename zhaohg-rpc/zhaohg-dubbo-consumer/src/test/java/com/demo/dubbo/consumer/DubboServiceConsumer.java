package com.demo.dubbo.consumer;

import com.alibaba.dubbo.common.logger.Logger;
import com.alibaba.dubbo.common.logger.LoggerFactory;
import com.zhaohg.service.DubboService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/dubbo-consumer-example.xml")
public class DubboServiceConsumer {
    private static final Logger       logger = LoggerFactory.getLogger(DubboServiceConsumer.class);
    @Autowired
    private              DubboService dubboService;

    @Test
    public void consumer() {
        int a = 4, b = 5;
        logger.info("消费dubbo服务....................");
        logger.info(String.format("a = %d, b = %d, a+b = %d", a, b, dubboService.calculate(a, b)));

        System.out.println("aaaaaaaaaaaaaaaaa=" + dubboService.calculate(a, b));
    }
}
