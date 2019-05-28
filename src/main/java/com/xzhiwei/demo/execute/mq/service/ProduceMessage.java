package com.xzhiwei.demo.execute.mq.service;

import io.netty.channel.ChannelFuture;
import com.xzhiwei.demo.execute.mq.entry.MQMessage;

public interface ProduceMessage {

    ChannelFuture produce(String messageId, MQMessage object);

}
