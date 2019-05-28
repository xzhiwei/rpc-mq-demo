package com.xzhiwei.demo.test.netty;

import com.xzhiwei.demo.server.NettyClient;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        NettyClient client = new NettyClient();
        client.connect("127.0.0.1",8090);
    }
}
