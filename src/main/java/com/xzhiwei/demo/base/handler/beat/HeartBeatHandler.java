package com.xzhiwei.demo.base.handler.beat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import com.xzhiwei.demo.base.message.Header;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(HeartBeatHandler.class);

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            writePingMessage(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        if(nettyMessage.getHeader().getType() == MessageType.PING){
            writePongMessage(ctx);
            ReferenceCountUtil.release(msg);
        } else if(nettyMessage.getHeader().getType() == MessageType.PONG){
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    private void writePingMessage(ChannelHandlerContext ctx){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.PING);
        message.setHeader(header);
        ctx.writeAndFlush(message);
    }

    private void writePongMessage(ChannelHandlerContext ctx){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.PONG);
        message.setHeader(header);
        ctx.writeAndFlush(message);
    }

}
