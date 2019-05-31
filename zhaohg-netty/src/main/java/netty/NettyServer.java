package netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaohg
 * @date 2019/04/28.
 */
public class NettyServer {
    private final int port;
    
    @Resource
    private WebSocketHandler   webSocketHandler;
    @Resource
    private HttpRequestHandler httpRequestHandler;
    
    public NettyServer(int port) {
        this.port = port;
    }
    
    public void start(InetSocketAddress address) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap sb = new ServerBootstrap();
            sb.option(ChannelOption.SO_BACKLOG, 1024);
            sb.group(group, bossGroup) // 绑定线程池
                    .channel(NioServerSocketChannel.class) // 指定使用的channel
                    .localAddress(this.port)// 绑定监听端口
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            System.out.println("收到新连接");
                            //处理心跳
                            pipeline.addLast(new IdleStateHandler(0, 0, 1800, TimeUnit.SECONDS));
                            //websocket协议本身是基于http协议的，所以这边也要使用http解编码器
                            pipeline.addLast("http-codec", new HttpServerCodec()); // HTTP编码解码器
                            pipeline.addLast("http-chunked", new ChunkedWriteHandler()); // 分块，方便大文件传输，不过实质上都是短的文本数据
                            pipeline.addLast("aggregator", new HttpObjectAggregator(64 * 1024));// 把HTTP头、HTTP体拼成完整的HTTP请求
                            pipeline.addLast("http-handler", httpRequestHandler);
                            pipeline.addLast("websocket-handler", webSocketHandler);
                        }
                    });
            ChannelFuture future = sb.bind(address).sync(); // 服务器异步创建绑定
            Channel channel = future.channel();
            System.out.println(NettyServer.class + " 启动正在监听： " + channel.localAddress());
            channel.closeFuture().sync(); // 关闭服务器通道
        } finally {
            group.shutdownGracefully().sync(); // 释放线程池资源
            bossGroup.shutdownGracefully().sync();
        }
    }
}