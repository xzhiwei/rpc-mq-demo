package com.xzhiwei.demo.execute.mq.common;

public class MQConfig {

    /**
     * 错误重试次数
     */
    public static int retry = 3;

    public static enum  SubscribeType {
        broadcast,
        cluster
    }

}
