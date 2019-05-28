package com.xzhiwei.demo.execute.mq.router;

import io.netty.channel.Channel;
import com.xzhiwei.demo.execute.mq.dispatch.SubscribeInfo;

public class RouterConfig {
    private SubscribeInfo subscribeInfo;

    private Channel channel;

    public SubscribeInfo getSubscribeInfo() {
        return subscribeInfo;
    }

    public void setSubscribeInfo(SubscribeInfo subscribeInfo) {
        this.subscribeInfo = subscribeInfo;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
