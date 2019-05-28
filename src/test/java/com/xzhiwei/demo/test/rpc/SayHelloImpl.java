package com.xzhiwei.demo.test.rpc;

public class SayHelloImpl implements SayHello {
    @Override
    public String sayHello(String name) {
        return "say hello to " + name;
    }
}
