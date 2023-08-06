package com.koi.krpc;

import com.koi.krpc.netty.client.NettyClient;
import com.koi.krpc.serializer.ProtobufSerializer;
import com.koi.krpc.transport.RpcClient;
import com.koi.krpc.transport.RpcClientProxy;

// 测试用Netty消费者
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new ProtobufSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12,"Hello World");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
