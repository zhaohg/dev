package com.zhaohg.request.check.conf;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.zhaohg.request.check.interceptor,com.zhaohg.request.check.properties")

//是否有 redis 配置的校验，如果没有配置则不会加载改配置，也就是当前插件并不会生效
@Conditional(CheckReqCondition.class)
public class CheckReqConf {
}
