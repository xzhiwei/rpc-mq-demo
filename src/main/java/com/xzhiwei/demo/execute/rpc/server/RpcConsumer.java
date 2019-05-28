package com.xzhiwei.demo.execute.rpc.server;

import com.xzhiwei.demo.execute.rpc.ServiceCenter;
import com.xzhiwei.demo.execute.rpc.handler.ClientMethodInvokeHandler;
import com.xzhiwei.demo.server.NettyClient;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcConsumer {

    private static Lock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();

    private String host;

    private int port;

    public RpcConsumer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcConsumer connect() throws InterruptedException {
        NettyClient client = new NettyClient();
        client.addHandlers(new ClientMethodInvokeHandler(lock, condition));
        client.connect(host, port);
        // 等待连接结果
        lock.lock();
        try {
            // 服务调度
            condition.await();
        } finally {
            lock.unlock();
        }
        // 注册服务
        return this;
    }

    public Object getService(Class clz){
        return ServiceCenter.get(clz);
    }

    public RpcConsumer registerConsumer(Class clz){
        ServiceCenter.add(clz);
        return this;
    }
}
