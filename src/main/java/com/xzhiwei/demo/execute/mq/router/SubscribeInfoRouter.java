package com.xzhiwei.demo.execute.mq.router;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.dispatch.Dispatcher;
import com.xzhiwei.demo.execute.mq.dispatch.RandomDispatcher;
import com.xzhiwei.demo.execute.mq.dispatch.SubscribeInfo;
import com.xzhiwei.demo.execute.mq.entry.MQMessage;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SubscribeInfoRouter {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeInfoRouter.class);

    private static Lock lock = new ReentrantLock();

    // 当前节点接收到的订阅的channel信息
    private static List<RouterConfig> routerDataList = new CopyOnWriteArrayList<>();

    // 加速消费，按照消息topic-tag 进行分组 -> List<RouterMapper>
    private static Map<String,List<RouterMapper>> analyzeredRouter = new ConcurrentHashMap<>();

    private Dispatcher dispatcher;

    public SubscribeInfoRouter() {
        // 默认cluster类型消息为随机发送
        this.dispatcher = new RandomDispatcher();
    }

    public SubscribeInfoRouter(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void register(SubscribeInfo subscribeInfo, Channel channel) {

        logger.info("register client : {} --> {}",channel.remoteAddress().toString(), JSONObject.toJSONString(subscribeInfo));

        RouterConfig routerData = new RouterConfig();
        routerData.setSubscribeInfo(subscribeInfo);
        routerData.setChannel(channel);
        lock.lock();
        try {
            routerDataList.add(routerData);
            analyzeredRouter.forEach((k,v)->{
                if(k.contains(subscribeInfo.getTopic())){
                    analyzeredRouter.remove(k);
                }
            });
        } finally {
            lock.unlock();
        }
    }

    public void dispatch(String messageID, MQMessage mqMessage) {
        List<RouterMapper> mappers = analyzeredRouter.get(mqMessage.getTopic() + "-" + mqMessage.getTag());
        if(CollectionUtils.isEmpty(mappers)) {
            if (CollectionUtils.isNotEmpty(routerDataList)) {
                List<RouterConfig> routerConfigs = new ArrayList<>();
                routerDataList.forEach(item -> {
                    if(item.getSubscribeInfo().getTopic().equalsIgnoreCase(mqMessage.getTopic())){
                        if ("*".equalsIgnoreCase(item.getSubscribeInfo().getTag()) || haveTag(item.getSubscribeInfo().getTag(), mqMessage.getTag())) {
                            routerConfigs.add(item);
                        }
                    }
                });
                Map<RouterMapper,List<RouterConfig>> mapperListMap = new HashMap<>();
                routerConfigs.forEach(item -> {
                    RouterMapper key = new RouterMapper();
                    key.setGroup(item.getSubscribeInfo().getGroup());
                    key.setSubscribeType(item.getSubscribeInfo().getSubscribeType());
                    
                    if(mapperListMap.containsKey(key)){
                        mapperListMap.get(key).add(item);
                    } else {
                        mapperListMap.put(key,new ArrayList<RouterConfig>(){{
                            add(item);
                        }});
                    }
                });
                List<RouterMapper> routerMappers = new ArrayList<>();
                mapperListMap.forEach((k,v)->{
                    RouterMapper mapper= new RouterMapper();
                    mapper.setSubscribeType(k.getSubscribeType());
                    mapper.setGroup(k.getGroup());
                    mapper.setRouterConfigList(v);
                    routerMappers.add(mapper);
                });
                analyzeredRouter.put(mqMessage.getTopic() + "-" + mqMessage.getTag(),routerMappers);
                mappers = routerMappers;
            }
        }
        if(CollectionUtils.isNotEmpty(mappers)){
            dispatch(mappers,messageID,mqMessage);
        }
    }

    private void dispatch(List<RouterMapper> mappers, String messageID, MQMessage mqMessage) {
        mappers.forEach(mapper -> {
            if(mapper.getSubscribeType() == MQConfig.SubscribeType.broadcast){
                mapper.getRouterConfigList().forEach(routerConfig -> {
                    sendMessage(routerConfig.getChannel(),messageID,mqMessage.getMsg());
                });
            } else {
                sendMessage(dispatcher.dispatch(mapper.getRouterConfigList(),messageID).getChannel(),messageID,mqMessage.getMsg());
            }
        });
    }

    private static boolean haveTag(String tags,String tag){
        String [] _tags = tags.split(",");
        boolean have = false;
        for(String _tag : _tags){
            if(StringUtils.equalsIgnoreCase(tag,_tag)){
                have = true;
                break;
            }
        }
        return have;
    }

    public void unRegister(Channel channel) {
        lock.lock();
        try {
            List<RouterConfig> _routerDataList = new CopyOnWriteArrayList<>();
            Iterator iterator = routerDataList.iterator();
            while (iterator.hasNext()) {
                RouterConfig routerData = (RouterConfig) iterator.next();
                if (routerData.getChannel().isOpen() && routerData.getChannel().isActive() && routerData.getChannel().isWritable() && routerData.getChannel() != channel) {
                    _routerDataList.add(routerData);
                } else {
                    logger.debug("un register client : {}", routerData.getChannel().remoteAddress().toString());
                }
            }
            routerDataList = _routerDataList;
        } finally {
            lock.unlock();
        }
    }


    private void sendMessage(Channel channel,String messageID, String message){
        if(channel.isActive() && channel.isOpen()) {
            NettyMessage nm = new NettyMessage(MessageType.MQ_DISPATCH_TO_CLIENT);
            nm.getHeader().setMessageId(messageID);
            nm.setBody(message);
            channel.writeAndFlush(nm);
            logger.debug("send message to client: {}, messageId: {}", channel.remoteAddress().toString(), messageID);
        } else {
            unRegister(channel);
        }
    }

}
