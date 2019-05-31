package com.zhaohg.io.netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhaohg on 2017-11-21.
 */

public class TimeClientHandler extends ChannelHandlerAdapter {

//    private ByteBuf buf;

//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) {
//        buf = ctx.alloc().buffer(4); // (1)
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) {
//        buf.release(); // (1)
//        buf = null;
//    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

//        ByteBuf m = (ByteBuf) msg;
//        buf.writeBytes(m); // (2)
//        m.release();
//
//        if (buf.readableBytes() >= 4) { // (3)
//            long currentTimeMillis = (buf.readInt() - 2208988800L) * 1000L;
//            System.out.println(new Date(currentTimeMillis));
//            ctx.close();
//        }
        
        ByteBuf m = (ByteBuf) msg; // (1)
        try {
            long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            Date currentTime = new Date(currentTimeMillis);
            System.out.println("Default Date Format:" + currentTime.toString());
            
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateString = formatter.format(currentTime);
            // 转换一下成中国人的时间格式
            System.out.println("Date Format:" + dateString);
            ctx.close();
        } finally {
            m.release();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}