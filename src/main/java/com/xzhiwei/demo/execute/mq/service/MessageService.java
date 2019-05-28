package com.xzhiwei.demo.execute.mq.service;

import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.common.MQException;

public interface MessageService {

    String sendMessage(String topic, String tag, String message) throws MQException;

//    void onConsumerMessageSuccess();

//    void onConsumerMessageFail(Object message);

    void addSubscribeMessage(String group, MQConfig.SubscribeType subscribeType, String topic, String tag, ConsumerMessage consumerMessage);

}
