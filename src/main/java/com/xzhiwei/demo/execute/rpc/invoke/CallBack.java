package com.xzhiwei.demo.execute.rpc.invoke;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CallBack {

    private String messageId;

    private Lock lock;

    private Condition condition;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Lock getLock() {
        return lock;
    }

    public void setLock(Lock lock) {
        this.lock = lock;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
