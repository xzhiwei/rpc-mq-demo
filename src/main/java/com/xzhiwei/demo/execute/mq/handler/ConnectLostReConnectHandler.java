package com.xzhiwei.demo.execute.mq.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.xzhiwei.demo.execute.mq.service.Connect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ConnectLostReConnectHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ConnectLostReConnectHandler.class);

    Connect connect;

    public ConnectLostReConnectHandler(Connect connect){
        this.connect = connect;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("connect lost , while start reconnect thread...");
        new Thread(()->{
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    boolean result = connect.connect();
                    if (result) {
                        logger.info("reconnect success.....");
                        break;
                    }
                } catch (Exception e) {
                    logger.warn("reconnect fail : {}",e.getMessage());
                }
            }
        },"reconnect").start();
        super.channelInactive(ctx);
    }
}
