package com.xzhiwei.demo.execute.rpc.server;

import com.xzhiwei.demo.execute.rpc.handler.ProviderInvokeHandler;
import com.xzhiwei.demo.server.NettyServer;

public class RpcProvider {

    private static ProviderInvokeHandler providerInvokeHandler = new ProviderInvokeHandler();

    private String host;

    private int port;

    public RpcProvider(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcProvider start() {
        NettyServer nettyServer = new NettyServer();
        nettyServer.addHandlers(providerInvokeHandler);
        nettyServer.start(this.host, this.port);
        return this;
    }

    public RpcProvider registerService(String name, Object obj) {
        providerInvokeHandler.registerService(name, obj);
        return this;
    }
}
