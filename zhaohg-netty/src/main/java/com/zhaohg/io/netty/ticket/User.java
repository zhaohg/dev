package com.zhaohg.io.netty.ticket;

import java.io.Serializable;

/**
 * 用户POJO对象
 * <p>
 * Created by zhaohg on 2017-11-21.
 */

public class User implements Serializable {
    private static final long   serialVersionUID = -3845514510571408376L;
    private              String userId;//身份证
    private              String userName;//姓名
    private              String phone;//电话
    private              String email;//邮箱

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}