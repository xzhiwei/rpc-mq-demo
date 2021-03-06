package com.xzhiwei.demo.execute.rpc.invoke;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.rpc.analyzer.SystemAnalyzer;
import com.xzhiwei.demo.execute.rpc.handler.ClientMethodInvokeHandler;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ExecutorServiceImpl implements ExecutorService {

    private static Logger logger = LoggerFactory.getLogger(ExecutorService.class);

    // 执行线程池，决定客户端的吞吐量
    private static final java.util.concurrent.ExecutorService executors = new ThreadPoolExecutor(5,5,30L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("rpc-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());


    // 线程锁，等待rpc调用结果
    private static Lock lock = new ReentrantLock();

    // 异步调用callback处理类
    private static final MethodResultCallback callback = MethodResultCallbackImpl.getInstance();

    @Override
    public Object execute(String className, String methodName, Object[] args) throws ExecutionException, InterruptedException, TimeoutException {
        long start = System.currentTimeMillis();
        if(logger.isDebugEnabled()) {
            logger.debug("execute className: {}, methodName: {}, args: {}", className, methodName, JSONObject.toJSONString(args));
        }
        Future<Object> resultFuture = executors.submit(()->{
            // 生成新的condition，处理异步返回
            Condition condition = lock.newCondition();
            // 获取通道，此处优化，实现动态路由
            Channel channel = ClientMethodInvokeHandler.ctx.channel();
            // 组装rpc报文
            MethodBean methodBean = new MethodBean();
            methodBean.setClassName(className);
            methodBean.setMethodName(methodName);
            methodBean.setArgs(args);
            NettyMessage nettyMessage = new NettyMessage(MessageType.RPC_CALL);
            nettyMessage.setBody(methodBean);
            // 注册回调
            callback.addCallBack(nettyMessage.getHeader().getMessageId(),lock,condition);
            // 发起rpc调用
            channel.writeAndFlush(nettyMessage);
            // 当前线程锁定，等待返回结果，此处异步操作会转化为同步操作
            try {
                lock.lock();
                try {
                    // 等待执行，最多等待3秒
                    condition.await(3, TimeUnit.SECONDS);
                } finally {
                    lock.unlock();
                    condition = null;
                }
                // 取得执行结果
                return callback.getResult(nettyMessage.getHeader().getMessageId());
            } finally {
                // 防止抛出异常时，发生内存泄漏
                callback.cleanCache(nettyMessage.getHeader().getMessageId());
            }
        });
        Object result =  resultFuture.get(3,TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        if(logger.isDebugEnabled()){
            logger.debug("rpc cost [{}]ms",(System.currentTimeMillis() - start));
        }
        SystemAnalyzer.rpcTimeAnalyzer(className,methodName,end-start);
        return result;
    }
}
