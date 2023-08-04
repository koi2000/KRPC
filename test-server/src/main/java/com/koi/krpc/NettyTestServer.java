package com.koi.krpc;

import com.koi.krpc.netty.server.NettyServer;
import com.koi.krpc.registry.DefaultServiceRegistry;
import com.koi.krpc.registry.ServiceRegistry;

// 测试用Netty服务提供者（服务端）
public class NettyTestServer {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }

}
