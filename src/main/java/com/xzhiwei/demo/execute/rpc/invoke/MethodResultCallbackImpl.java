package com.xzhiwei.demo.execute.rpc.invoke;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MethodResultCallbackImpl implements MethodResultCallback {

    // 定义为private，防止外部操作发生内存泄漏
    private static final Map<String,CallBack> callBackMap = new ConcurrentHashMap<>();

    // 定义为private，防止外部操作发生内存泄漏
    private static final Map<String,Object> resultMap = new ConcurrentHashMap<>();

    private static MethodResultCallback callback;

    private MethodResultCallbackImpl(){};

    public static MethodResultCallback getInstance(){
        if(callback != null){
            return callback;
        } else {
            synchronized (MethodResultCallbackImpl.class){
                if(callback != null){
                    return callback;
                }
                callback = new MethodResultCallbackImpl();
                return callback;
            }
        }
    }

    @Override
    public void addCallBack(String messageId, Lock lock,Condition condition){
        CallBack callBack = new CallBack();
        callBack.setMessageId(messageId);
        callBack.setLock(lock);
        callBack.setCondition(condition);
        callBackMap.put(messageId,callBack);
    }

    @Override
    public void cleanCache(String messageId) {
        callBackMap.remove(messageId);
        resultMap.remove(messageId);
    }

    @Override
    public void callBack(String messageId,Object data) {
        CallBack callBack =  callBackMap.get(messageId);
        callBack.getLock().lock();
        try{
            resultMap.put(messageId,data);
            callBack.getCondition().signal();
        } finally {
            callBack.getLock().unlock();
        }
    }

    @Override
    public Object getResult(String messageId) {
        Object result =  resultMap.get(messageId);
        // 清理map信息，防止内存泄漏
        this.cleanCache(messageId);
        return result;
    }
}
