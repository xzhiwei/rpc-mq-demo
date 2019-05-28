package com.xzhiwei.demo.base.coder.marshalling;

import io.netty.buffer.ByteBuf;
import com.xzhiwei.demo.base.ChannelBufferByteOutput;
import com.xzhiwei.demo.base.coder.Encoder;
import org.jboss.marshalling.ByteOutput;
import org.jboss.marshalling.Marshaller;

import java.io.IOException;

public class MarshallingEncoder implements Encoder {
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private Marshaller marshaller;

    /**
     * Creates a new encoder.
     */
    public MarshallingEncoder() throws IOException {
        marshaller = MarshallingCodecFactory.buildMarshalling();
    }

    public void encode(Object msg, ByteBuf out) throws Exception {
        int lengthPos = out.writerIndex();
        out.writeBytes(LENGTH_PLACEHOLDER);
        ByteOutput output = new ChannelBufferByteOutput(out);
        marshaller.start(output);
        marshaller.writeObject(msg);
        marshaller.finish();
        marshaller.close();
        // 在占位符位置设置msg对象的长度
        out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
    }
}
