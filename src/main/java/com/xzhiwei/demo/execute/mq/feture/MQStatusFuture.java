package com.xzhiwei.demo.execute.mq.feture;

import io.netty.channel.Channel;
import io.netty.channel.DefaultChannelProgressivePromise;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.entry.MQStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MQStatusFuture extends DefaultChannelProgressivePromise {

    private NettyMessage nettyMessage;

    private MQStatus status;

    private int retry = MQConfig.retry;

    private long messageTime = System.currentTimeMillis();

    static Map<String,MQStatusFuture> futures = new ConcurrentHashMap<>();

    public MQStatusFuture(Channel channel) {
        super(channel);
    }

    public static void addFuture(String messageId,MQStatusFuture future) {
        futures.put(messageId,future);
    }

    public static MQStatusFuture get(String messageId){
        return futures.get(messageId);
    }


    public NettyMessage getNettyMessage() {
        return nettyMessage;
    }

    public void setNettyMessage(NettyMessage nettyMessage) {
        this.nettyMessage = nettyMessage;
    }

    public MQStatus getStatus() {
        return status;
    }

    public void setStatus(MQStatus status) {
        this.status = status;
        switch (status.getStatus()){
            case RECEIVE_SUCCESS:
                this.setProgress(50,100);
                break;
            case SEND_SUCCESS:
                this.setProgress(0,100);
                break;
            case CONSUMER_FAIL:
                this.setProgress(60,100);
                this.toDoRetry();
                break;
            case CONSUMER_SUCCESS:
                this.setProgress(100,100);
                this.setSuccess();
                futures.remove(this.getNettyMessage().getHeader().getMessageId());
                break;
        }
    }

    private void toDoRetry(){
        if(this.retry > 0){
            this.retry --;
            System.out.println("try to re send message....." + this.nettyMessage.getHeader().getMessageId());
            this.channel().writeAndFlush(this.nettyMessage);
        } else {
            this.setFailure(new RuntimeException("send message error!"));
        }
    }
}
