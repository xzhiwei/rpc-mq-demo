package com.xzhiwei.demo.test.mq;

import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.server.MQClient;

public class Consumer2Test {

    public static void main(String[] args) throws Exception {
        MQClient consumer = new MQClient("127.0.0.1",8091);
        consumer.addSubscribeMessage("my", MQConfig.SubscribeType.broadcast,"test","*", new MyMessageConsumer());
        consumer.connect();
        consumer.sendMessage("test","a","test2");
    }
}
