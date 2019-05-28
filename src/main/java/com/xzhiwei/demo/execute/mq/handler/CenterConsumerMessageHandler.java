package com.xzhiwei.demo.execute.mq.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.xzhiwei.demo.base.message.Header;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.mq.dispatch.SubscribeInfo;
import com.xzhiwei.demo.execute.mq.entry.MQMessage;
import com.xzhiwei.demo.execute.mq.entry.MQStatus;
import com.xzhiwei.demo.execute.mq.feture.MQStatusFuture;
import com.xzhiwei.demo.execute.mq.router.SubscribeInfoRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class CenterConsumerMessageHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(CenterConsumerMessageHandler.class);

    private static SubscribeInfoRouter router = new SubscribeInfoRouter();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        NettyMessage message = (NettyMessage) msg;
        if(message.getHeader().getType() == MessageType.MQ_MESSAGE){
            MQMessage mqMessage = (MQMessage) message.getBody();
            //通知发送端消息接受成功
            sendMqStatusMessage(ctx,message.getHeader().getMessageId(), MQStatus.Status.RECEIVE_SUCCESS);
            // 转发MQ消息
            router.dispatch(message.getHeader().getMessageId(),mqMessage);
            ReferenceCountUtil.release(msg);
        } else if(message.getHeader().getType() == MessageType.MQ_STATUS){
            MQStatus status = (MQStatus) message.getBody();
            MQStatusFuture future = MQStatusFuture.get(status.getMessageId());
            if(future != null) {
                future.setStatus(status);
            }
            ReferenceCountUtil.release(msg);
        } else if(message.getHeader().getType() == MessageType.MQ_SUBSCRIBE){
            SubscribeInfo subscribeInfo =(SubscribeInfo) message.getBody();
            router.register(subscribeInfo,ctx.channel());
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        router.unRegister(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.fireExceptionCaught(cause);
    }

    private void sendMqStatusMessage(ChannelHandlerContext ctx,String messageId,MQStatus.Status status){
        NettyMessage message = new NettyMessage();
        message.setBody(new MQStatus(status,messageId));
        Header header = new Header();
        header.setType(MessageType.MQ_STATUS);
        message.setHeader(header);
        ctx.writeAndFlush(message);
    }

}
