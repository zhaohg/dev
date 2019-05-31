package com.zhaohg.redis.demo1;

import com.zhaohg.redis.demo1.config.AppConfig;
import com.zhaohg.redis.demo1.model.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringStarter {
    public static void main(String[] args) {
        final ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        final IRedisService service = context.getBean(IRedisService.class);

        service.doSomeHashes(new User(1L, "Tom", "user@example.com"));
        service.doSomeValues();
        service.doSomeExpirations();
        service.doSomeBits();
    }
}
