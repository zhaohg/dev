package com.zhaohg.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by com.zhaohg on 2017/8/8.
 */
@Document(collection = "message_batch")
public class MessageBatch {

    @Indexed
    private String     batch_no;
    private String     send_user_id;
    private String     temp_id;
    private String     shop_id;
    private String     send_name;
    private Integer    send_type;
    private String     message_type;
    private Integer    receiver_size;
    private Integer    free_send_num;
    private BigDecimal unit_price;
    private Integer    is_link;
    private String     link_url;
    private String     app_show;
    private String     title;
    private String     content;
    private String     params;
    private Date       send_time;


    public String getBatch_no() {
        return batch_no;
    }

    public void setBatch_no(String batch_no) {
        this.batch_no = batch_no;
    }

    public String getSend_user_id() {
        return send_user_id;
    }

    public void setSend_user_id(String send_user_id) {
        this.send_user_id = send_user_id;
    }

    public String getTemp_id() {
        return temp_id;
    }

    public void setTemp_id(String temp_id) {
        this.temp_id = temp_id;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public Integer getSend_type() {
        return send_type;
    }

    public void setSend_type(Integer send_type) {
        this.send_type = send_type;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public Integer getReceiver_size() {
        return receiver_size;
    }

    public void setReceiver_size(Integer receiver_size) {
        this.receiver_size = receiver_size;
    }

    public Integer getFree_send_num() {
        return free_send_num;
    }

    public void setFree_send_num(Integer free_send_num) {
        this.free_send_num = free_send_num;
    }

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    public Integer getIs_link() {
        return is_link;
    }

    public void setIs_link(Integer is_link) {
        this.is_link = is_link;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
    }

    public String getApp_show() {
        return app_show;
    }

    public void setApp_show(String app_show) {
        this.app_show = app_show;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Date getSend_time() {
        return send_time;
    }

    public void setSend_time(Date send_time) {
        this.send_time = send_time;
    }
}
