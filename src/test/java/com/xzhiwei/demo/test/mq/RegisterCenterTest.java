package com.xzhiwei.demo.test.mq;

import com.xzhiwei.demo.execute.mq.server.RegisterCenter;

public class RegisterCenterTest {

    public static void main(String[] args) throws InterruptedException {
        RegisterCenter send = new RegisterCenter();
        send.start("127.0.0.1",8091);
    }

}
