package com.xzhiwei.demo.execute.mq.server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import com.xzhiwei.demo.base.message.MessageId;
import com.xzhiwei.demo.execute.mq.common.MQConfig;
import com.xzhiwei.demo.execute.mq.common.MQException;
import com.xzhiwei.demo.execute.mq.entry.MQMessage;
import com.xzhiwei.demo.execute.mq.feture.MQStatusFuture;
import com.xzhiwei.demo.execute.mq.handler.ClientConsumerMessageHandler;
import com.xzhiwei.demo.execute.mq.handler.ConnectLostReConnectHandler;
import com.xzhiwei.demo.execute.mq.handler.ProduceMessageHandler;
import com.xzhiwei.demo.execute.mq.listener.MQStatusListener;
import com.xzhiwei.demo.execute.mq.service.Connect;
import com.xzhiwei.demo.execute.mq.service.ConsumerMessage;
import com.xzhiwei.demo.execute.mq.service.MessageService;
import com.xzhiwei.demo.execute.mq.service.ProduceMessage;
import com.xzhiwei.demo.server.NettyClient;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MQClient implements MessageService,Connect {

    Logger logger = LoggerFactory.getLogger(MQClient.class);

    private static Lock lock = new ReentrantLock();

    private static Condition condition = lock.newCondition();

    private ProduceMessage produceMessage = new ProduceMessageHandler(lock,condition);

    private List<ClientConsumerMessageHandler> handlers = new ArrayList<>();

    private String ip;

    private int port;

    boolean connected = false;

    @Override
    public String sendMessage(String topic,String tag,String message) throws MQException {
        if(!connected){
            throw new MQException("lost connect to register center, can not send message now!");
        }
        String messageId = MessageId.getMessageId();
        MQMessage mqMessage = new MQMessage();
        mqMessage.setTopic(topic);
        mqMessage.setTag(tag);
        mqMessage.setMsg(message);
        ChannelFuture future = produceMessage.produce(messageId,mqMessage);
        future.addListener(new MQStatusListener());
        MQStatusFuture.addFuture(messageId, (MQStatusFuture) future);
        return messageId;
    }

    @Override
    public void addSubscribeMessage(String group, MQConfig.SubscribeType subscribeType, String topic, String tag, ConsumerMessage consumerMessage) {
        handlers.add(new ClientConsumerMessageHandler(consumerMessage,group,subscribeType,topic,tag));
    }

    public MQClient(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    @Override
    public boolean connect(){
        connected = false;
        lock.lock();
        try {
            NettyClient nettyClient = new NettyClient();
            nettyClient.addHandlers((ChannelHandler) produceMessage);
            if (CollectionUtils.isNotEmpty(handlers)) {
                handlers.forEach(nettyClient::addHandlers);
            }
            nettyClient.addHandlers(new ConnectLostReConnectHandler(this));
            nettyClient.connect(ip, port);
            condition.await();
            System.out.println("mq client start success!");
            connected = true;
        } catch (Exception e){
            logger.warn("mq client start fail : {}",e.getMessage());
            throw new RuntimeException("connect to server: "+ ip + ":" + port +" error!");
        } finally {
            lock.unlock();
        }
        return connected;
    }

}
