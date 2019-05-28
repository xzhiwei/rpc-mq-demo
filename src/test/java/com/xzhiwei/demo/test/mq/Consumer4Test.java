package com.xzhiwei.demo.test.mq;

import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.server.MQClient;

public class Consumer4Test {

    public static void main(String[] args) throws Exception {
        MQClient consumer = new MQClient("127.0.0.1",8091);
        consumer.addSubscribeMessage("my2", MQConfig.SubscribeType.cluster,"test","*", new MyMessageConsumer());
        consumer.connect();
        consumer.sendMessage("test","a","test");
        consumer.sendMessage("test","a","test1");
        consumer.sendMessage("test","a","test2");
    }
}
