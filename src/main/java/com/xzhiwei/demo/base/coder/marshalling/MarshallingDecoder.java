package com.xzhiwei.demo.base.coder.marshalling;

import io.netty.buffer.ByteBuf;
import com.xzhiwei.demo.base.ChannelBufferByteInput;
import com.xzhiwei.demo.base.coder.Decoder;
import org.jboss.marshalling.ByteInput;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

public class MarshallingDecoder implements Decoder {

    private final Unmarshaller unmarshaller;

    public MarshallingDecoder() throws IOException {
        this.unmarshaller = MarshallingCodecFactory.buildUnmarshalling();
    }


    public Object decode(ByteBuf in) throws IOException, ClassNotFoundException {
        // 读取四位的msg对象长度信息
        int objectSize = in.readInt();
        // 读取对象内容，不会改变readerIndex，需要手工校正
        ByteBuf buf = in.slice(in.readerIndex(),objectSize);
        ByteInput input = new ChannelBufferByteInput(buf);
        try{
            unmarshaller.start(input);
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            // 手工校正readerIndex位置
            in.readerIndex(in.readerIndex() + objectSize);
            buf.release();
            return obj;
        } finally {
            unmarshaller.close();
        }
    }



}
