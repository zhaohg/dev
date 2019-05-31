package com.zhaohg.socket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * Created by zhaohg on 2018/9/5.
 */
public class WebSocketInterceptor extends HttpSessionHandshakeInterceptor {

    //将HttpSession中对象放入WebSocketSession中
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> map) throws Exception {
        System.out.println("握手前...");
        return super.beforeHandshake(request, response, wsHandler, map);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        System.out.println("握手后...");
        super.afterHandshake(request, response, wsHandler, ex);
    }

}