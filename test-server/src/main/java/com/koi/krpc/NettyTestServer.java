package com.koi.krpc;

import com.koi.krpc.annotation.ServiceScan;
import com.koi.krpc.netty.server.NettyServer;
import com.koi.krpc.serializer.CommonSerializer;
import com.koi.krpc.serializer.ProtobufSerializer;
import com.koi.krpc.transport.RpcServer;

// 测试用Netty服务提供者（服务端）
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer("127.0.0.1", 12888, CommonSerializer.PROTOBUF_SERIALIZER);
        server.start();
    }

}
