package com.xzhiwei.demo.execute.mq.dispatch;

import com.xzhiwei.demo.execute.mq.router.RouterConfig;

import java.util.List;

public interface Dispatcher {

    RouterConfig dispatch(List<RouterConfig> routers, String messageID);

}
