package com.zhaohg.io.netty.ticket;

/**
 * 指令集
 * <p>
 * Created by zhaohg on 2017-11-21.
 */
public class Code {
    public static final int CODE_SEARCH = 1;//查询车票余量
    public static final int CODE_BOOK   = 2;//订票
    public static final int CODE_NONE   = -1;//错误指令 无法处理
}