package com.zhaohg.factory.demo2;

public class SendMailFactory implements Provider {

    public Sender produce() {
        return new MailSender();
    }
}