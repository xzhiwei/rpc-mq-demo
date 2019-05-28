package com.xzhiwei.demo.execute.rpc.handler;

import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import com.xzhiwei.demo.base.message.MessageType;
import com.xzhiwei.demo.base.message.NettyMessage;
import com.xzhiwei.demo.execute.rpc.invoke.MethodBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ChannelHandler.Sharable
public class ProviderInvokeHandler extends ChannelInboundHandlerAdapter {

    private final static Logger logger = LoggerFactory.getLogger(ProviderInvokeHandler.class);

    private final static Map<String,Object> SERVICES = new ConcurrentHashMap<>();

    public void registerService(String name,Object obj){
        SERVICES.put(name,obj);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage nettyMessage = (NettyMessage) msg;
        if(nettyMessage.getHeader().getType() == MessageType.RPC_CALL){
            MethodBean methodBean = (MethodBean) nettyMessage.getBody();
            logger.debug("get rpc call: {}", JSONObject.toJSONString(methodBean));
            if(SERVICES.containsKey(methodBean.getClassName())){
                Object service = SERVICES.get(methodBean.getClassName());
                Method[] methods = service.getClass().getMethods();
                for(Method method:methods){
                    if(StringUtils.equalsIgnoreCase(method.getName(),methodBean.getMethodName())){
                        Object result = method.invoke(service,methodBean.getArgs());
                        sendResult(ctx.channel(),nettyMessage.getHeader().getMessageId(),result);
                    }
                }
            }
            ReferenceCountUtil.release(msg);
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private void sendResult(Channel channel,String messageId, Object result){
        NettyMessage message = new NettyMessage(messageId,MessageType.RPC_RESULT);
        message.setBody(result);
        channel.writeAndFlush(message);
    }
}
