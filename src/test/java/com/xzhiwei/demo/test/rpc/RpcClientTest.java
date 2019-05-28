package com.xzhiwei.demo.test.rpc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xzhiwei.demo.execute.rpc.server.RpcConsumer;
import io.netty.util.ResourceLeakDetector;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcClientTest {

    private static final java.util.concurrent.ExecutorService executors = new ThreadPoolExecutor(5,5,30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(Integer.MAX_VALUE),
            new ThreadFactoryBuilder().setNameFormat("client-test-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

     static AtomicInteger count = new AtomicInteger(0);

    public static void main(String[] args) throws InterruptedException {

        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);

        RpcConsumer client = new RpcConsumer("localhost",8099)
                .registerConsumer(SayHello.class);

        // 服务调度
        for(int i = 0;i<100;i++){
            // 获取服务
            executors.submit(()->{
                int num = count.incrementAndGet();
                SayHello sayHello = (SayHello) client.getService(SayHello.class);
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
