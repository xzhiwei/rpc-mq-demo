package com.xzhiwei.demo.base.handler.auth;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.xzhiwei.demo.base.message.Header;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(LoginAuthReqHandler.class);

    // IP + 端口 只能建立一个通道
    private static Map<String,Boolean> NODE_CHECK = new ConcurrentHashMap<>();

    private byte LOGIN_SUCCESS = 0;

    private byte LOGIN_FAIL = -1;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        if(nettyMessage.getHeader().getType() == MessageType.LOGIN_REQ){
            ctx.writeAndFlush(buildLoginAuthRespMessage(checkRequest(ctx)));
            ctx.fireUserEventTriggered("AUTH_SUCCESS");
            ReferenceCountUtil.release(msg);
        }  else {
            ctx.fireChannelRead(msg);
        }
    }

    private byte checkRequest(ChannelHandlerContext ctx) {
        byte result = LOGIN_SUCCESS;
        String nodeIndex = ctx.channel().remoteAddress().toString();
        if(NODE_CHECK.containsKey(nodeIndex)){
            result = LOGIN_FAIL;
        } else {
            NODE_CHECK.put(nodeIndex,true);
        }
        return result;
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NODE_CHECK.remove(ctx.channel().remoteAddress().toString());
        ctx.fireExceptionCaught(cause);
    }

    private NettyMessage buildLoginAuthRespMessage(byte result) {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.LOGIN_RESP);
        nettyMessage.setHeader(header);
        nettyMessage.setBody(result);
        return nettyMessage;
    }
}
