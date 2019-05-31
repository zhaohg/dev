package com.zhaohg.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by zhaohg on 2018/9/5.
 */
public class Message {
    
    private long       id;
    private String     name;
    private Date       dataTime;
    private BigDecimal price;
    private Float      total;
    
    public Message(long id, String name, Date dataTime, BigDecimal price, Float total) {
        this.id = id;
        this.name = name;
        this.dataTime = dataTime;
        this.price = price;
        this.total = total;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Date getDataTime() {
        return dataTime;
    }
    
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Float getTotal() {
        return total;
    }
    
    public void setTotal(Float total) {
        this.total = total;
    }
}
