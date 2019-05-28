package com.xzhiwei.demo.test.mq;

import com.xzhiwei.demo.execute.mq.service.ConsumerMessage;

import java.util.Random;

public class MyMessageConsumer implements ConsumerMessage {

    Random random = new Random();

    @Override
    public void consumer(String msg) {
            throw new RuntimeException("error");
    }
}