package com.xzhiwei.demo.test.rpc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.util.ResourceLeakDetector;
import com.xzhiwei.demo.execute.rpc.ServiceCenter;
import com.xzhiwei.demo.execute.rpc.handler.ClientMethodInvokeHandler;
import com.xzhiwei.demo.server.NettyClient;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RpcClient {

    private static final java.util.concurrent.ExecutorService executors = new ThreadPoolExecutor(5,5,30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(Integer.MAX_VALUE),
            new ThreadFactoryBuilder().setNameFormat("client-test-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    private static Lock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();

     static AtomicInteger count = new AtomicInteger(0);

     public static int getAndAdd(){
         while (true){
             int now = count.get();
             int next = now + 1;
             if(count.compareAndSet(now,next)){
                 return next;
             }
         }
     }

    public static void main(String[] args) throws InterruptedException {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        NettyClient client = new NettyClient();
        client.addHandlers(new ClientMethodInvokeHandler(lock,condition));
        client.connect("localhost",8099);

        // 等待连接结果
        lock.lock();
        try {
            // 服务调度
            condition.await();
        } finally {
            lock.unlock();
        }
        // 注册服务
        ServiceCenter.add(SayHello.class);

        Random random = new Random();

        // 服务调度
        for(int i = 0;i<100;i++){
            // 获取服务
            executors.submit(()->{
                int num = getAndAdd();
                SayHello sayHello = (SayHello) ServiceCenter.get(SayHello.class);
                String name = "name :" + num;
                String result = sayHello.sayHello(name);
                System.out.println("name:" + name + " result:" + result);
                if(!StringUtils.equalsIgnoreCase(result.split(":")[1],num + "")){
                    throw new RuntimeException("error!");
                }
            });

        }
        executors.shutdown();
        while (!executors.isTerminated()){
            Thread.sleep(1000);
        }
        System.out.println(count.get());

    }
}
