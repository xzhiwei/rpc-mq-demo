package com.xzhiwei.demo.execute.rpc.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JDKMethodInvoke implements InvocationHandler {

    private Class target;

    private final static ExecutorService executorService = new ExecutorServiceImpl();

    private static final Map<Class,Object> PROXY_OBJECTS = new ConcurrentHashMap<>();

    private JDKMethodInvoke(){};

    public static <T> T getProxyObject(Class target){
        if(PROXY_OBJECTS.containsKey(target)){
            return (T) PROXY_OBJECTS.get(target);
        } else {
            synchronized (JDKMethodInvoke.class){
                if(!PROXY_OBJECTS.containsKey(target)) {
                    JDKMethodInvoke invoke = new JDKMethodInvoke();
                    PROXY_OBJECTS.put(target, invoke.bind(target));
                }
                return (T) PROXY_OBJECTS.get(target);
            }
        }
    }

    private  <T> T bind(Class object){
        this.target = object;
        return (T) Proxy.newProxyInstance(JDKMethodInvoke.class.getClassLoader(),new Class[]{object},this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return executorService.execute(target.getName(),method.getName(),args);
    }
}
