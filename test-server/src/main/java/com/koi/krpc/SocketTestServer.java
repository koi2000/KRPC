package com.koi.krpc;

import com.koi.krpc.registry.DefaultServiceRegistry;
import com.koi.krpc.registry.ServiceRegistry;
import com.koi.krpc.serializer.HessianSerializer;
import com.koi.krpc.socket.server.SocketServer;

public class SocketTestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.start(9999);
    }
}
