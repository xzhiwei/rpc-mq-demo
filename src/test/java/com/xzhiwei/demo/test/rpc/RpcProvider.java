package com.xzhiwei.demo.test.rpc;

import io.netty.util.ResourceLeakDetector;
import com.xzhiwei.demo.execute.rpc.handler.ProviderInvokeHandler;
import com.xzhiwei.demo.server.NettyServer;

public class RpcProvider {
    public static void main(String[] args) {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        ProviderInvokeHandler providerInvokeHandler = new ProviderInvokeHandler();
        providerInvokeHandler.registerService(SayHello.class.getName(),new SayHelloImpl());
        NettyServer nettyServer = new NettyServer();
        nettyServer.addHandlers(providerInvokeHandler);
        nettyServer.start("localhost",8099);
    }
}
