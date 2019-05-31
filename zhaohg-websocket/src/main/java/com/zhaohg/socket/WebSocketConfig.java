package com.zhaohg.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Created by zhaohg on 2018/9/5.
 */
@Configuration
//@EnableWebMvc
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {
    
    @Autowired
    private SocketHandler socketHandler;
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 用来注册websocket server实现类，第二个参数是访问websocket的地址
        registry.addHandler(socketHandler, "/socketServer").addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("http://localhost:8080");
        // 使用Sockjs的注册方法
        registry.addHandler(socketHandler, "/sockjs/socketServer").addInterceptors(new WebSocketInterceptor()).withSockJS();
        System.out.println("registry spring websocket success!");
    }
    
}