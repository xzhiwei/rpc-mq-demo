package com.xzhiwei.demo.execute.mq.entry;

import java.io.Serializable;

public class MQMessage implements Serializable {

    String topic;

    String tag;

    String msg;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
