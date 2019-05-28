package com.xzhiwei.demo.base.message;

import java.util.HashMap;
import java.util.Map;

public final class Header {
    /**
     * 4
     */
    private int crcCode = 0xabef0101;

    /**
     * 4
     */
    private int length;

    /**
     * 8
     */
    private String messageId;

    /**
     * 1
     */
    private byte type;

    /**
     * 1
     */
    private byte priority;// 消息优先级

    /**
     * size: 4
     */
    private Map<String,Object> attachment = new HashMap<>();

    public Header(){
        this.messageId = MessageId.getMessageId();
    }

    public Header(String messageId) {
        this.messageId = messageId;
    }

    public final int getCrcCode() {
        return crcCode;
    }

    public void setCrcCode(int crcCode) {
        this.crcCode = crcCode;
    }

    public final int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public final String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public final byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public final byte getPriority() {
        return priority;
    }

    public void setPriority(byte priority) {
        this.priority = priority;
    }

    public Map<String, Object> getAttachment() {
        return this.attachment;
    }

    public void setAttachment(Map<String, Object> attachment) {
        this.attachment = attachment;
    }
}
