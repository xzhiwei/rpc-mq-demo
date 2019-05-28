# rpc-mq-demo

本项目基于netty，通过自定义协议，实现简单的rpc调度和mq消息发送
可作为学习研究项目

实现了通过自定义协议，实现如下功能：
* 消息encode
* decode
* netty拆包
* socket授权
* 心跳检测
* 自动重连
* rpc远程调度
* mq消息发送（消息消费状态跟踪、失败重试）
* 订阅消费（集群消费、广播消费）