package com.xzhiwei.demo.execute.rpc.invoke;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface ExecutorService {

    Object execute(String className, String methodName, Object[] args) throws ExecutionException, InterruptedException, TimeoutException;
}
