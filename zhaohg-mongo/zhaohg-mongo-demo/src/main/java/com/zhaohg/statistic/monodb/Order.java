package com.zhaohg.statistic.monodb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaohg on 2018/7/20.
 */
public class Order extends HashMap<String, Object> {

    private Long   id;
    private String name;
    private Date   time;

    public Order() {
        super();
        super.put("id", -1);
        super.put("name", -1);
        super.put("time", 0);
    }

    public Order(Map<String, Object> map) {
        super(map);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
