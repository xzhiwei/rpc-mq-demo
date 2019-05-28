package com.xzhiwei.demo.base.coder;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface Decoder {
    Object decode(ByteBuf in) throws IOException, ClassNotFoundException;
}
