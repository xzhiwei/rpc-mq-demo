package com.xzhiwei.demo.execute.rpc.invoke;

import java.io.Serializable;

public class MethodBean implements Serializable {

    private String  className;

    private String methodName;

    private Object[] args;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
