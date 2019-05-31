package com.zhaohg.io.netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

/**
 * DISCARD服务(丢弃服务，指的是会忽略所有接收的数据的一种协议)
 * 服务端处理通道.这里只是打印一下请求的内容，并不对请求进行任何的响应
 * DiscardServerHandler 继承自 ChannelHandlerAdapter，
 * 这个类实现了ChannelHandler接口，
 * ChannelHandler提供了许多事件处理的接口方法，
 * 然后你可以覆盖这些方法。
 * 现在仅仅只需要继承ChannelHandlerAdapter类而不是你自己去实现接口方法。
 */
public class DiscardServerHandler extends ChannelHandlerAdapter {
    /***
     * 这里我们覆盖了chanelRead()事件处理方法。
     * 每当从客户端收到新的数据时，
     * 这个方法会在收到消息时被调用，
     * 这个例子中，收到的消息的类型是ByteBuf
     * @param ctx 通道处理的上下文信息
     * @param msg 接收的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            while (in.isReadable()) {
                System.out.print((char) in.readByte());
                System.out.flush();
            }
            //这一句和上面注释的的效果都是打印输入的字符
            System.out.println(in.toString(CharsetUtil.US_ASCII));
        } finally {
            /**
             * ByteBuf是一个引用计数对象，这个对象必须显示地调用release()方法来释放。
             * 请记住处理器的职责是释放所有传递到处理器的引用计数对象。
             */
            ReferenceCountUtil.release(msg);
        }
    }
    
    /***
     * 这个方法会在发生异常时触发
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        /***
         * 发生异常后，关闭连接
         */
        cause.printStackTrace();
        ctx.close();
    }
}
