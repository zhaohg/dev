package com.zhaohg.redis.demo1;

import com.zhaohg.redis.demo1.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RedisPubSubStarter {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(AppConfig.class);
    }
}
