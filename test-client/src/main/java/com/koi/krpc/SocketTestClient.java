package com.koi.krpc;

import com.koi.krpc.serializer.CommonSerializer;
import com.koi.krpc.serializer.KryoSerializer;
import com.koi.krpc.socket.client.SocketClient;
import com.koi.krpc.transport.RpcClientProxy;

// 测试用 消费者
public class SocketTestClient {

    public static void main(String[] args) {
        SocketClient client = new SocketClient(CommonSerializer.KRYO_SERIALIZER);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        for (int i = 0; i < 20; i++) {
            String res = helloService.hello(object);
            System.out.println(res);
        }
    }

}
