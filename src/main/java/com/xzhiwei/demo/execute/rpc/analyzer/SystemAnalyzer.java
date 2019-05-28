package com.xzhiwei.demo.execute.rpc.analyzer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class SystemAnalyzer {

    private static Logger logger = LoggerFactory.getLogger(SystemAnalyzer.class);

    private static boolean doRpcTimeAnalyzer = false;

    private static ExecutorService executors = null;

    private static void init(){
        if(executors == null){
            executors = new ThreadPoolExecutor(1,1,30L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(4094),
                    new ThreadFactoryBuilder().setNameFormat("analyzer-%d").build(),
                    new ThreadPoolExecutor.AbortPolicy());
        }
    }


    public static void rpcTimeAnalyzer(String className,String methodName,long cost){
        if(doRpcTimeAnalyzer) {
            init();
            executors.submit(new RpcTime(className, methodName, cost));
        }
    }

    static {
        if(logger.isTraceEnabled()) {
            new Thread(() -> {
                while (true) {
                    if (RpcTime.TOTAL_COUNT.get() > 0L && logger.isTraceEnabled()) {
                        logger.trace("total count: {}, total cost: {}, argv: {}", RpcTime.TOTAL_COUNT.get(), RpcTime.TOTAL_COST.get(),
                                RpcTime.TOTAL_COST.get() / RpcTime.TOTAL_COUNT.get());
                    }
                    try {
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "SystemAnalyzer").start();
        }
    }

    static class RpcTime implements Runnable{

        static volatile AtomicLong TOTAL_COST = new AtomicLong(0L);

        static volatile AtomicLong TOTAL_COUNT = new AtomicLong(0L);

        private String className;

        private String methodName;

        long cost;

        public RpcTime(String className, String methodName, long cost) {
            this.className = className;
            this.methodName = methodName;
            this.cost = cost;
        }

        @Override
        public void run() {
            while (true) {
                long now = TOTAL_COST.get();
                if(TOTAL_COST.compareAndSet(now, now + this.cost)){
                    break;
                }
            }
            TOTAL_COUNT.getAndIncrement();
        }
    }

    public static boolean isDoRpcTimeAnalyzer() {
        return doRpcTimeAnalyzer;
    }

    public static void setDoRpcTimeAnalyzer(boolean doRpcTimeAnalyzer) {
        SystemAnalyzer.doRpcTimeAnalyzer = doRpcTimeAnalyzer;
    }
}
