package com.xzhiwei.demo.execute.rpc;

import com.xzhiwei.demo.execute.rpc.invoke.JDKMethodInvoke;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceCenter {

    private final static Map<String,Object> SERVICES = new ConcurrentHashMap<>();

    public static Object get(Class clz){
        return SERVICES.get(clz.getName());
    }

    public static void add(Class clz){
        if(!SERVICES.containsKey(clz.getName())){
            SERVICES.put(clz.getName(),JDKMethodInvoke.getProxyObject(clz));
        }
    }
}
