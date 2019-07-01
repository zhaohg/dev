package com.zhaohg.io.netty.demo1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * TIME服务(时间协议的服务)
 * Created by zhaohg on 2018-11-21.
 * <p>
 * 为了测试我们的时间服务器，我们可以使用UNIX的rdate命令：rdate -o <port> -p <host>
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    /**
     * channelActive()方法将会在连接被建立并且准备进行通信时被调用。
     * 因此让我们在这个方法里完成一个代表当前时间的32位整数消息的构建工作。
     * @param ctx
     */
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        /*Scanner cin=new Scanner(System.in);
        System.out.println("请输入发送信息：");
        String name=cin.nextLine();*/
        String name = "HelloWorld!";
        /**
         * 为了发送一个新的消息，我们需要分配一个包含这个消息的新的缓冲。
         * 因为我们需要写入一个32位的整数，因此我们需要一个至少有4个字节的ByteBuf。
         * 通过ChannelHandlerContext.alloc()得到一个当前的ByteBufAllocator，
         * 然后分配一个新的缓冲。
         */
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeBytes(name.getBytes());
        /***
         * 和往常一样我们需要编写一个构建好的消息
         * 。但是等一等，flip在哪？难道我们使用NIO发送消息时不是调用java.nio.ByteBuffer.flip()吗？
         * ByteBuf之所以没有这个方法因为有两个指针，
         * 一个对应读操作一个对应写操作。
         * 当你向ByteBuf里写入数据的时候写指针的索引就会增加，
         * 同时读指针的索引没有变化。
         * 读指针索引和写指针索引分别代表了消息的开始和结束。
         * 比较起来，NIO缓冲并没有提供一种简洁的方式来计算出消息内容的开始和结尾，
         * 除非你调用flip方法。
         * 当你忘记调用flip方法而引起没有数据或者错误数据被发送时，
         * 你会陷入困境。这样的一个错误不会发生在Netty上，
         * 因为我们对于不同的操作类型有不同的指针。
         * 你会发现这样的使用方法会让你过程变得更加的容易，
         * 因为你已经习惯一种没有使用flip的方式。
         * 另外一个点需要注意的是ChannelHandlerContext.write()(和writeAndFlush())方法会返回一个ChannelFuture对象，
         * 一个ChannelFuture代表了一个还没有发生的I/O操作。
         * 这意味着任何一个请求操作都不会马上被执行，
         * 因为在Netty里所有的操作都是异步的。
         * 因此你需要在write()方法返回的ChannelFuture完成后调用close()方法，
         * 然后当他的写操作已经完成他会通知他的监听者。
         */
        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        /**
         * 当一个写请求已经完成是如何通知到我们？
         * 这个只需要简单地在返回的ChannelFuture上增加一个ChannelFutureListener。
         * 这里我们构建了一个匿名的ChannelFutureListener类用来在操作完成时关闭Channel。
         */
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                /***
                 * 请注意,close()方法也可能不会立马关闭，他也会返回一个ChannelFuture。
                 */
                ctx.close();
            }
        });
    }
    
    //接收结果
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("client:" + buf.toString(CharsetUtil.UTF_8));
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
