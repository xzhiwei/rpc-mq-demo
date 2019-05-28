package com.xzhiwei.demo.execute.mq.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import com.xzhiwei.demo.execute.mq.handler.CenterConsumerMessageHandler;
import com.xzhiwei.demo.server.NettyClient;
import com.xzhiwei.demo.server.NettyServer;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterCenter {

    private String cluster;

    public static Map<String, Channel> SERVER_CHANNEL_MAP = new ConcurrentHashMap<>();

    public RegisterCenter(){}

    public void start(String ip,int port) throws InterruptedException {
        NettyServer nettyServer = new NettyServer();
        nettyServer.addHandlers(new CenterConsumerMessageHandler());
        nettyServer.start(ip, port);
        if(StringUtils.isNotBlank(cluster)){
            String [] clusters = cluster.split(",");
            for(String node:clusters){
                NettyClient client = new NettyClient();
                String [] ipAndPort = node.split(":");
                ChannelFuture channelFuture = client.connect(ipAndPort[0],Integer.parseInt(ipAndPort[1]));
                SERVER_CHANNEL_MAP.put(node,channelFuture.channel());
            }
        }
        System.out.println("mq center service start success!");
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
