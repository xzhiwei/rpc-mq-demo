package com.xzhiwei.demo.execute.rpc.invoke;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface MethodResultCallback {

    void callBack(String messageId, Object data);

    Object getResult(String messageId);

    void addCallBack(String messageId, Lock lock, Condition condition);

    void cleanCache(String messageId);
}
