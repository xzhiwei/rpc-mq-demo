package com.xzhiwei.demo.execute.mq.dispatch;

import com.xzhiwei.demo.execute.mq.common.MQConfig;

import java.io.Serializable;
import java.util.Objects;

public class SubscribeInfo implements Serializable {

    String group;

    MQConfig.SubscribeType subscribeType;

    String topic;

    String tag;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public MQConfig.SubscribeType getSubscribeType() {
        return subscribeType;
    }

    public void setSubscribeType(MQConfig.SubscribeType subscribeType) {
        this.subscribeType = subscribeType;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribeInfo that = (SubscribeInfo) o;
        return Objects.equals(group, that.group) &&
                subscribeType == that.subscribeType &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(tag, that.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, subscribeType, topic, tag);
    }
}
