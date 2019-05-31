package com.zhaohg.hessian.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by zhaohg on 2018/8/30.
 */
public class Order implements Serializable {

    private Long       orderId;
    private String     name;
    private BigDecimal price;
    private boolean    boolea;
    private Float      floa;
    private Double     doubl;
    private Integer    num;
    private Short      shor;

    public Order(Long orderId, String name, BigDecimal price, boolean boolea, Float floa, Double doubl, Integer num, Short shor) {
        this.orderId = orderId;
        this.name = name;
        this.price = price;
        this.boolea = boolea;
        this.floa = floa;
        this.doubl = doubl;
        this.num = num;
        this.shor = shor;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isBoolea() {
        return boolea;
    }

    public void setBoolea(boolean boolea) {
        this.boolea = boolea;
    }

    public Float getFloa() {
        return floa;
    }

    public void setFloa(Float floa) {
        this.floa = floa;
    }

    public Double getDoubl() {
        return doubl;
    }

    public void setDoubl(Double doubl) {
        this.doubl = doubl;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Short getShor() {
        return shor;
    }

    public void setShor(Short shor) {
        this.shor = shor;
    }
}
