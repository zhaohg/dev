package com.zhaohg.io.netty.demo1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by zhaohg on 2017-11-21.
 */

public class TimeClient {
    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8000;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             * 如果你只指定了一个EventLoopGroup，
             * 那他就会即作为一个‘boss’线程，
             * 也会作为一个‘workder’线程，
             * 尽管客户端不需要使用到‘boss’线程。
             */
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            /**
             * 代替NioServerSocketChannel的是NioSocketChannel,这个类在客户端channel被创建时使用
             */
            b.channel(NioSocketChannel.class); // (3)
            /**
             * 不像在使用ServerBootstrap时需要用childOption()方法，
             * 因为客户端的SocketChannel没有父channel的概念。
             */
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            //用connect()方法代替了bind()方法
            ChannelFuture f = b.connect(host, port).sync();
            //等到运行结束，关闭
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}