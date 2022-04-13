package com.haust.minitalk.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * @Auther: csp1999
 * @Date: 2020/09/23/9:51
 * @Description: websocket 服务端
 */
@Component
public class WebSocketServer {

    private static class SingletionWSServer {
        static final WebSocketServer instance = new WebSocketServer();
    }

    public static WebSocketServer getInstance() {
        return SingletionWSServer.instance;
    }

    private EventLoopGroup mainGroup;// 主线程池
    private EventLoopGroup subGroup;// 从线程池
    private ServerBootstrap serverBootstrap;//服务器驱动类
    private ChannelFuture future;
    private static final int PORT = 8088;// 端口
    private static final String HOST = "192.168.111";// IP

    public WebSocketServer() {
        mainGroup = new NioEventLoopGroup();// 主线程组
        subGroup = new NioEventLoopGroup();// 从线程组
        serverBootstrap = new ServerBootstrap();// 定义驱动类
        serverBootstrap.group(mainGroup, subGroup)// 绑定主从线程组
                .channel(NioServerSocketChannel.class)// 绑定nio双向通道
                .childHandler(new WebSocketServerInitialzer());// 初始化器
    }

    public void start() {
        this.future = serverBootstrap.bind(HOST, PORT);// 驱动类绑定端口和IP
        if (future.isSuccess()) {
            System.out.println("启动 Netty 成功...");
        }
    }
    /*
    public static void main(String[] args) throws InterruptedException {
        // 创建主从线程池
        EventLoopGroup mainGroup = new NioEventLoopGroup();
        EventLoopGroup subGroup = new NioEventLoopGroup();

        try {
            // 创建服务器驱动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(mainGroup,subGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketServerInitialzer());

            // 绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();

            // 同步关闭 channelFuture
            channelFuture.channel().closeFuture().sync();
        }finally {
            // 优雅关闭线程池
            mainGroup.shutdownGracefully();
            subGroup.shutdownGracefully();
        }
    }
     */
}
