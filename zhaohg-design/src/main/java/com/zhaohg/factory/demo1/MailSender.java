package com.zhaohg.factory.demo1;

/**
 * Created by zhaohg on 2018/10/9.
 */
public class MailSender implements Sender {
    @Override
    public void Send() {
        System.out.println("this is mail sender!");
    }
}
