package com.zhaohg.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by com.zhaohg on 2017/8/8.
 */
@Document(collection = "message_receiver")
public class MessageReceiver {

    private String  batch_no;
    @Indexed
    private String  receive_user_id;
    private Integer status;
    private Date    see_time;

    @DBRef
    private MessageBatch messageBatch;

    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getReceive_user_id() {
        return receive_user_id;
    }

    public void setReceive_user_id(String receive_user_id) {
        this.receive_user_id = receive_user_id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getSee_time() {
        return see_time;
    }

    public void setSee_time(Date see_time) {
        this.see_time = see_time;
    }

    public MessageBatch getMessageBatch() {
        return messageBatch;
    }

    public void setMessageBatch(MessageBatch messageBatch) {
        this.messageBatch = messageBatch;
    }
}
