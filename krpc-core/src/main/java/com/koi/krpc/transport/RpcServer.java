package com.koi.krpc.transport;

import com.koi.krpc.registry.ServiceRegistry;
import com.koi.krpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

// 远程方法调用的提供者（服务端）
public interface RpcServer {
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    <T> void publishService(T service,String serviceName);
}
