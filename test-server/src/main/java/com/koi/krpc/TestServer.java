package com.koi.krpc;

import com.koi.krpc.registry.DefaultServiceRegistry;
import com.koi.krpc.registry.ServiceRegistry;
import com.koi.krpc.server.RpcServer;

public class TestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new RpcServer(serviceRegistry);
        rpcServer.start(9000);
    }
}
