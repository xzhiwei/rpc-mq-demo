package com.xzhiwei.demo.base.coder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import com.xzhiwei.demo.base.message.Header;
import com.xzhiwei.demo.base.message.NettyMessage;

import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    Decoder decode;

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, String decode) throws Exception {
        this(maxFrameLength,lengthFieldLength,lengthFieldLength,-8,0,decode);
    }

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, String decode) throws Exception{
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
        this.decode = CodecConfig.getDecoder(decode);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx,in);
        if(frame == null){
            return null;
        }
        NettyMessage nettyMessage = null;
        if(frame.readableBytes() > 0) {
            nettyMessage = new NettyMessage();
            Header header = new Header();
            if(header.getCrcCode() !=  frame.readInt()){
                return null;
            }
            header.setLength(frame.readInt());
            byte [] messageId = new byte[32];
            frame.readBytes(messageId);
            header.setMessageId(new String(messageId,"utf-8"));
            header.setType(frame.readByte());
            header.setPriority(frame.readByte());
            int size = frame.readInt();
            if (size > 0) {
                Map<String, Object> attch = new HashMap<>(size);
                int keySize = 0;
                byte[] keyArray = null;
                String key = null;
                for (int i = 0; i < size; i++) {
                    // 去读key，字符串需要先确定长度
                    keySize = frame.readInt();
                    keyArray = new byte[keySize];
                    frame.readBytes(keyArray);
                    key = new String(keyArray, "utf-8");
                    attch.put(key, decode.decode(frame));
                }
                keyArray = null;
                key = null;
                header.setAttachment(attch);
            }
            if (frame.readableBytes() > 4) {
                nettyMessage.setBody(decode.decode(frame));
            }
            nettyMessage.setHeader(header);
        }
        if(frame.refCnt() > 0) {
            frame.release(frame.refCnt());
        }
        return nettyMessage;

    }
}
