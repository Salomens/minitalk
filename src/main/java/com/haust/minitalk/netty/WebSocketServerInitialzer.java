package com.haust.minitalk.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @Auther: csp1999
 * @Date: 2020/09/23/9:59
 * @Description: 初始化器 channel 注册之后 会执行里面的相应初始化方法
 */
public class WebSocketServerInitialzer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 获取客户端与服务端建立的管道(pipeline)
        ChannelPipeline channelPipeline = socketChannel.pipeline();

        // websocket 基于http 协议所需要的 http编解码器
        channelPipeline.addLast(new HttpServerCodec());
        /**
         * 在http 上有一些数据流产生(数据流有大有小) 我们需要对其进行处理
         * 因此 我们需要使用netty 对该大数据流的读写提供支持
         * 这个类是：ChunkedWriteHandler
         */
        channelPipeline.addLast(new ChunkedWriteHandler());
        // 对httpMessage 进行聚合处理 , 聚合成 response 或者 request
        channelPipeline.addLast(new HttpObjectAggregator(1024 * 64));

        // ===========================增加心跳支持==============================
        /**
         * netty 自带的读写handler
         * 针对客户端，如果在1分钟时间内没有向服务端发送读写心跳（ALL），则主动断开连接
         * 如果有读空闲和写空闲，则不做任何处理
         * 参数1：读的空闲时间 单位s
         * 参数1：写的空闲时间 单位s
         * 参数1：读写的空闲时间 单位s
         */
        channelPipeline.addLast(new IdleStateHandler(8,16,32));

        // 自定义的空闲状态监测的handler
        channelPipeline.addLast(new HeartBeatHandler());

        /**
         * 基于http websocket 添加 路由地址
         * WebSocketServerProtocolHandler 会帮助处理一些繁重复杂的事情
         * 比如处理握手动作：handshaking(close、ping、ping)：ping+ping=心跳
         * 对于websocket来说 都是以frams 进行传输的 不同的数据类型对应的frams也不同
         */
        channelPipeline.addLast(new WebSocketServerProtocolHandler("/websocket"));

        // 自定义的handler
        channelPipeline.addLast(new ChatHandler());
    }
}
