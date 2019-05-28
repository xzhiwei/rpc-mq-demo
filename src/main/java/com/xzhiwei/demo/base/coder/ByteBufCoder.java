package com.xzhiwei.demo.base.coder;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

public interface ByteBufCoder {

    void encode(Object msg, ByteBuf out) throws Exception;

    Object decode(ByteBuf in) throws IOException, ClassNotFoundException;
}
