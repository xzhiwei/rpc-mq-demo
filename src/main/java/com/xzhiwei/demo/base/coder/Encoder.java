package com.xzhiwei.demo.base.coder;

import io.netty.buffer.ByteBuf;

public interface Encoder {
    void encode(Object msg, ByteBuf out) throws Exception;
}
