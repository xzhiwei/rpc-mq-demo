package com.xzhiwei.demo.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import com.xzhiwei.demo.base.coder.NettyMessageDecoder;
import com.xzhiwei.demo.base.coder.NettyMessageEncoder;
import com.xzhiwei.demo.base.handler.auth.LoginAuthReqHandler;
import com.xzhiwei.demo.base.handler.beat.HeartBeatHandler;
import com.xzhiwei.demo.base.handler.last.LastHandler;

import java.util.ArrayList;
import java.util.List;

public class NettyServer {

    private int timeOutSeconds = 10*6*30;

    private String encoderClassName = "com.xzhiwei.demo.base.coder.marshalling.MarshallingEncoder";

    private String decoderClassName = "com.xzhiwei.demo.base.coder.marshalling.MarshallingDecoder";


    List<ChannelHandler> handlers = new ArrayList<>();

    public void start(String ip,int port) {
        try {
            NioEventLoopGroup boss = new NioEventLoopGroup();
            NioEventLoopGroup worker = new NioEventLoopGroup();
            ServerBootstrap bbs = new ServerBootstrap();
            bbs.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new IdleStateHandler(30, 30, 30));
                            ch.pipeline().addLast(new NettyMessageEncoder(encoderClassName));
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4, decoderClassName));
                            ch.pipeline().addLast(new ReadTimeoutHandler(timeOutSeconds));
                            ch.pipeline().addLast(new LoginAuthReqHandler());
                            if (handlers.size() > 0) {
                                handlers.forEach(item -> {
                                    ch.pipeline().addLast(item);
                                });
                            }
                            ch.pipeline().addLast(new HeartBeatHandler());
                            ch.pipeline().addLast(new LastHandler());
                        }
                    });

            ChannelFuture future = bbs.bind(ip, port).sync();
            future.channel().closeFuture().sync();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public NettyServer setTimeOutSeconds(int timeOutSeconds) {
        this.timeOutSeconds = timeOutSeconds;
        return this;
    }

    public NettyServer setEncoderClassName(String encoderClassName) {
        this.encoderClassName = encoderClassName;
        return this;
    }

    public NettyServer setDecoderClassName(String decoderClassName) {
        this.decoderClassName = decoderClassName;
        return this;
    }

    public NettyServer addHandlers(ChannelHandler handler) {
        this.handlers.add(handler);
        return this;
    }
}
