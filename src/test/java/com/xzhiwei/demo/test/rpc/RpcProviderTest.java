package com.xzhiwei.demo.test.rpc;

import com.xzhiwei.demo.execute.rpc.server.RpcProvider;
import io.netty.util.ResourceLeakDetector;
import com.xzhiwei.demo.execute.rpc.handler.ProviderInvokeHandler;
import com.xzhiwei.demo.server.NettyServer;

public class RpcProviderTest {
    public static void main(String[] args) {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        new RpcProvider("localhost",8099)
                .registerService(SayHello.class.getName(),new SayHelloImpl())
                .start();
    }
}
