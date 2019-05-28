package com.xzhiwei.demo.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import com.xzhiwei.demo.base.coder.NettyMessageDecoder;
import com.xzhiwei.demo.base.coder.NettyMessageEncoder;
import com.xzhiwei.demo.base.handler.auth.LoginAuthRespHandler;
import com.xzhiwei.demo.base.handler.beat.HeartBeatHandler;
import com.xzhiwei.demo.base.handler.last.LastHandler;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    private EventLoopGroup group = new NioEventLoopGroup();

    private int timeOutSeconds = 10*6*10;

    private boolean reConnect = true;

    private int reConnectSeconds = 5;

    private int maxFrameLength = 1024*1024*10;

    private String encoderClassName = "com.xzhiwei.demo.base.coder.marshalling.MarshallingEncoder";

    private String decoderClassName = "com.xzhiwei.demo.base.coder.marshalling.MarshallingDecoder";

    List<ChannelHandler> handlers = new ArrayList<>();

    public ChannelFuture connect(String ip, int port) throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new IdleStateHandler(30,30,30, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new NettyMessageEncoder(encoderClassName));
                        ch.pipeline().addLast(new ReadTimeoutHandler(timeOutSeconds));
                        ch.pipeline().addLast(new NettyMessageDecoder(maxFrameLength, 4, 4, decoderClassName));
                        ch.pipeline().addLast(new LoginAuthRespHandler());
                        if(handlers.size() > 0){
                            handlers.forEach(item -> {
                                ch.pipeline().addLast(item);
                            });
                        }
                        ch.pipeline().addLast(new HeartBeatHandler());
                        ch.pipeline().addLast(new LastHandler());
                    }
                });

        ChannelFuture future = b.connect(new InetSocketAddress(ip, port)).sync();
        System.out.println("start finish.....");
        return future;
    }

    public NettyClient setTimeOutSeconds(int timeOutSeconds) {
        this.timeOutSeconds = timeOutSeconds;
        return this;
    }

    public NettyClient setReConnect(boolean reConnect) {
        this.reConnect = reConnect;
        return this;
    }

    public NettyClient setReConnectSeconds(int reConnectSeconds) {
        this.reConnectSeconds = reConnectSeconds;
        return this;
    }

    public NettyClient setMaxFrameLength(int maxFrameLength) {
        this.maxFrameLength = maxFrameLength;
        return this;
    }

    public NettyClient setEncoderClassName(String encoderClassName) {
        this.encoderClassName = encoderClassName;
        return this;
    }

    public NettyClient setDecoderClassName(String decoderClassName) {
        this.decoderClassName = decoderClassName;
        return this;
    }

    public NettyClient addHandlers(ChannelHandler handler) {
        this.handlers.add(handler);
        return this;
    }
}
