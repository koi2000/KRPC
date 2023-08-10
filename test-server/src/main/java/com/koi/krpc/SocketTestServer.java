package com.koi.krpc;

import com.koi.krpc.annotation.ServiceScan;
import com.koi.krpc.serializer.CommonSerializer;
import com.koi.krpc.serializer.HessianSerializer;
import com.koi.krpc.socket.server.SocketServer;
import com.koi.krpc.transport.RpcServer;

@ServiceScan
public class SocketTestServer {
    public static void main(String[] args) {
        RpcServer socketServer = new SocketServer("127.0.0.1", 9998, CommonSerializer.HESSIAN_SERIALIZER);
        socketServer.start();
    }
}
