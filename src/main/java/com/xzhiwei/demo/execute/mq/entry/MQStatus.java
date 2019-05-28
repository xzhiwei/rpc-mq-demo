package com.xzhiwei.demo.execute.mq.entry;

import java.io.Serializable;

public class MQStatus implements Serializable {

    public static enum Status implements Serializable{
        SEND_SUCCESS,
        RECEIVE_SUCCESS,
        CONSUMER_SUCCESS,
        CONSUMER_FAIL
    }

    private Status status;

    private String messageId;

    public MQStatus() {
    }

    public MQStatus(Status status, String messageId) {
        this.status = status;
        this.messageId = messageId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
