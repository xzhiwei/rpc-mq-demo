package com.xzhiwei.demo.base.message;

import java.util.UUID;

public class MessageId {

    public static String getMessageId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }


}
