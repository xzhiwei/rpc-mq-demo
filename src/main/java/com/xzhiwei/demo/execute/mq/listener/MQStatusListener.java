package com.xzhiwei.demo.execute.mq.listener;

import io.netty.util.concurrent.GenericProgressiveFutureListener;
import com.xzhiwei.demo.execute.mq.feture.MQStatusFuture;

public class MQStatusListener implements GenericProgressiveFutureListener<MQStatusFuture> {

    @Override
    public void operationComplete(MQStatusFuture future) throws Exception {
        System.out.println("operationComplete......" + future.isSuccess());
    }

    @Override
    public void operationProgressed(MQStatusFuture future, long progress, long total) throws Exception {
        System.out.println("operationProgressed...... process: " + progress + " total:" + total);
    }
}
