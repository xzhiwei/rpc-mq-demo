package com.xzhiwei.demo.base.coder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import com.xzhiwei.demo.base.message.NettyMessage;

import java.util.List;

public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    Encoder encode;

    public NettyMessageEncoder(String encode) throws Exception {
        this.encode = CodecConfig.getEncoder(encode);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {
        if(msg == null || msg.getHeader() == null){
            throw new Exception("the encode message is null");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(msg.getHeader().getCrcCode());
        sendBuf.writeInt(msg.getHeader().getLength());
        sendBuf.writeBytes(msg.getHeader().getMessageId().getBytes());
        sendBuf.writeByte(msg.getHeader().getType());
        sendBuf.writeByte(msg.getHeader().getPriority());
        sendBuf.writeInt(msg.getHeader().getAttachment().size());
        msg.getHeader().getAttachment().forEach((k,v)->{
            try {
                byte [] keyArray = k.getBytes("utf-8");
                // 可变长内容需要先写入长度信息
                sendBuf.writeInt(keyArray.length);
                sendBuf.writeBytes(keyArray);
                encode.encode(v,sendBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        if(msg.getBody() != null){
            encode.encode(msg.getBody(),sendBuf);
        }
        // 设置消息总长度，初始化设置为0，需要重新设置值
        sendBuf.setInt(4,sendBuf.readableBytes());
        out.add(sendBuf);
    }
}
