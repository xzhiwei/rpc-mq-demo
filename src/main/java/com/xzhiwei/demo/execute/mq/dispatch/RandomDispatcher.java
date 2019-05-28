package com.xzhiwei.demo.execute.mq.dispatch;

import com.xzhiwei.demo.execute.mq.router.RouterConfig;

import java.util.List;
import java.util.Random;

public class RandomDispatcher implements Dispatcher {

    private static Random random = new Random();

    @Override
    public RouterConfig dispatch(List<RouterConfig> routers, String messageID) {
        return routers.get(random.nextInt(routers.size()));
    }
}
