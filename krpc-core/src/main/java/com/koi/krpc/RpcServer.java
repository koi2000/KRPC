package com.koi.krpc;

import com.koi.krpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

// 远程方法调用的提供者（服务端）
public interface RpcServer {
    void start(int port);
}
