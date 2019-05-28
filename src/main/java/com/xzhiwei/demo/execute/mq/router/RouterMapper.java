package com.xzhiwei.demo.execute.mq.router;

import com.xzhiwei.demo.execute.mq.common.MQConfig;

import java.util.List;
import java.util.Objects;

public class RouterMapper {

    private MQConfig.SubscribeType  subscribeType;

    private String group;

    private List<RouterConfig> routerConfigList;

    public MQConfig.SubscribeType getSubscribeType() {
        return subscribeType;
    }

    public void setSubscribeType(MQConfig.SubscribeType subscribeType) {
        this.subscribeType = subscribeType;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<RouterConfig> getRouterConfigList() {
        return routerConfigList;
    }

    public void setRouterConfigList(List<RouterConfig> routerConfigList) {
        this.routerConfigList = routerConfigList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RouterMapper that = (RouterMapper) o;
        return subscribeType == that.subscribeType &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscribeType, group);
    }

}
