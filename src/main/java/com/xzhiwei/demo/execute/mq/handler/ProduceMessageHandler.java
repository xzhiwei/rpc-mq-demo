package com.xzhiwei.demo.execute.mq.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.xzhiwei.demo.base.message.Header;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.mq.entry.MQMessage;
import com.xzhiwei.demo.execute.mq.feture.MQStatusFuture;
import com.xzhiwei.demo.execute.mq.service.ProduceMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@ChannelHandler.Sharable
public class ProduceMessageHandler extends ChannelInboundHandlerAdapter implements ProduceMessage {

    Logger logger = LoggerFactory.getLogger(ProduceMessageHandler.class);

    ChannelHandlerContext ctx;

    Condition condition;

    Lock lock;

    public ProduceMessageHandler(Lock lock, Condition condition) {
        this.lock = lock;
        this.condition = condition;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if(StringUtils.equalsIgnoreCase("AUTH_SUCCESS",evt.toString())){
            this.ctx = ctx;
            logger.info("fire mq userEventTriggered");
            lock.lock();
            try {
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public ChannelFuture produce(String messageId, MQMessage msg) {
        NettyMessage nettyMessage = new NettyMessage();
        Header header = new Header(messageId);
        header.setType(MessageType.MQ_MESSAGE);
        nettyMessage.setBody(msg);
        nettyMessage.setHeader(header);
        logger.info("send message.... id: {}" , messageId);
        ChannelFuture future = ctx.writeAndFlush(nettyMessage);
        MQStatusFuture mqStatusFuture = new MQStatusFuture(future.channel());
        mqStatusFuture.setNettyMessage(nettyMessage);
        return mqStatusFuture;
    }
}
