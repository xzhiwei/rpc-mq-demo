package com.xzhiwei.demo.test.netty;

import com.xzhiwei.demo.server.NettyServer;

public class Server {
    public static void main(String[] args) throws InterruptedException {
        NettyServer server = new NettyServer();
        server.start("127.0.0.1",8090);
    }
}
