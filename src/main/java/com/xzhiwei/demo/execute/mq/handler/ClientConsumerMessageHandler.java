package com.xzhiwei.demo.execute.mq.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.xzhiwei.demo.base.message.Header;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.dispatch.SubscribeInfo;
import com.xzhiwei.demo.execute.mq.entry.MQStatus;
import com.xzhiwei.demo.execute.mq.service.ConsumerMessage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class ClientConsumerMessageHandler extends ChannelInboundHandlerAdapter {

    Logger logger = LoggerFactory.getLogger(ClientConsumerMessageHandler.class);

    ConsumerMessage consumerMessage;

    SubscribeInfo subscribeInfo;

    public ClientConsumerMessageHandler(ConsumerMessage consumerMessage, String group, MQConfig.SubscribeType subscribeType, String topic, String tag) {
        this.consumerMessage = consumerMessage;
        subscribeInfo = new SubscribeInfo();
        subscribeInfo.setGroup(group);
        subscribeInfo.setSubscribeType(subscribeType);
        subscribeInfo.setTopic(topic);
        subscribeInfo.setTag(tag);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if(StringUtils.equalsIgnoreCase("AUTH_SUCCESS",evt.toString())){
            sendClientSubscribeInfo(ctx);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if(message.getHeader().getType() == MessageType.MQ_DISPATCH_TO_CLIENT){
            logger.info("consumer message ...messageId: {}, data: {}",message.getHeader().getMessageId(),message.getBody().toString());
            /**
             * 通知Center发送端消息接受成功
             */
            sendMqStatusMessage(ctx,message.getHeader().getMessageId(), MQStatus.Status.RECEIVE_SUCCESS);
            try {
                consumerMessage.consumer((String) message.getBody());
                sendMqStatusMessage(ctx,message.getHeader().getMessageId(), MQStatus.Status.CONSUMER_SUCCESS);
            } catch (Exception e){
                sendMqStatusMessage(ctx,message.getHeader().getMessageId(), MQStatus.Status.CONSUMER_FAIL);
            }
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
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

    private void sendClientSubscribeInfo(ChannelHandlerContext ctx){
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.MQ_SUBSCRIBE);
        message.setHeader(header);
        message.setBody(subscribeInfo);
        ctx.writeAndFlush(message);
    }

}
