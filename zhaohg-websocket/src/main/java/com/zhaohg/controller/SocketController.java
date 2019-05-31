package com.zhaohg.controller;

import com.alibaba.fastjson.JSON;
import com.zhaohg.model.Message;
import com.zhaohg.socket.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zhaohg on 2018/9/5.
 */
@Controller
public class SocketController {

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private SocketHandler socketHandler;

    @RequestMapping(value = "/login")
    public String login(HttpSession session) {
        logger.info("login ...");
        session.setAttribute("user", "zhaohg");

        return "home";
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public String message() {
        Message message = new Message(1111111111, "zhaohg", new Date(), new BigDecimal(123214.133), 234234.34f);
        socketHandler.sendMessageToUsers(new TextMessage(JSON.toJSONString(message)));
        return "message";
    }
}