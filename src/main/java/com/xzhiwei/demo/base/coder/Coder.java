package com.xzhiwei.demo.base.coder;

import java.io.IOException;

public interface Coder {

    byte [] encode(Object msg) throws Exception;

    Object decode(byte[] bytes) throws IOException, ClassNotFoundException;
}
