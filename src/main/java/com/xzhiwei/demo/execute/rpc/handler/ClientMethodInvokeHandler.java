package com.xzhiwei.demo.execute.rpc.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.rpc.invoke.MethodResultCallback;
import com.xzhiwei.demo.execute.rpc.invoke.MethodResultCallbackImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@ChannelHandler.Sharable
public class ClientMethodInvokeHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ClientMethodInvokeHandler.class);

    public static ChannelHandlerContext ctx;

    private Condition condition;

    private Lock lock;

    private MethodResultCallback callback = MethodResultCallbackImpl.getInstance();

    public ClientMethodInvokeHandler(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if(StringUtils.equalsIgnoreCase("AUTH_SUCCESS",evt.toString())){
            ClientMethodInvokeHandler.ctx = ctx;
            logger.debug("fire rpc userEventTriggered");
            lock.lock();
            try {
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        // 获取到rpc调用结果
        if(nettyMessage.getHeader().getType() == MessageType.RPC_RESULT){
            callback.callBack(nettyMessage.getHeader().getMessageId(),nettyMessage.getBody());
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}
