package com.koi.krpc;

import com.koi.krpc.registry.ServiceRegistry;
import com.koi.krpc.serializer.HessianSerializer;
import com.koi.krpc.socket.server.SocketServer;

public class SocketTestServer {
    public static void main(String[] args) {
        HelloServiceImpl helloService = new HelloServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1", 9999);
        socketServer.setSerializer(new HessianSerializer());
        socketServer.publishService(helloService, HelloService.class);
    }
}
