package com.xzhiwei.demo.base.message;

public class NettyMessage {

    public NettyMessage() {
    }

    public NettyMessage(byte type) {
        this.header = new Header();
        this.header.setType(type);
    }

    public NettyMessage(String messageId, byte type) {
        this.header = new Header();
        this.header.setType(type);
        this.header.setMessageId(messageId);
    }

    private Header header;

    private Object body;

    public Header getHeader() {

        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
