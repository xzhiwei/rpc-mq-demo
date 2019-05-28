package com.xzhiwei.demo.execute.mq.common;

public class MQException extends Exception {

    private String code;

    public MQException(String msg){
        super(msg);
    }

    public MQException(String code,String msg){
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
