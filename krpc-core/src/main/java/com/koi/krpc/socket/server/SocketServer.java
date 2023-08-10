package com.koi.krpc.socket.server;

import com.koi.krpc.handler.RequestHandler;
import com.koi.krpc.hook.ShutdownHook;
import com.koi.krpc.provider.ServiceProvider;
import com.koi.krpc.provider.ServiceProviderImpl;
import com.koi.krpc.registry.NacosServiceRegistry;
import com.koi.krpc.transport.AbstractRpcServer;
import com.koi.krpc.transport.RpcServer;
import com.koi.krpc.enumeration.RpcError;
import com.koi.krpc.exception.RpcException;
import com.koi.krpc.registry.ServiceRegistry;
import com.koi.krpc.serializer.CommonSerializer;
import com.koi.krpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

// Socket方式远程方法调用的提供者（服务端）
public class SocketServer extends AbstractRpcServer {
    private final ExecutorService threadPool;
    private RequestHandler requestHandler = new RequestHandler();
    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;
    private final CommonSerializer serializer;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serializer = CommonSerializer.getByCode(serializer);
        scanService();
    }

    @Override
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器启动……");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket,
                        requestHandler, serviceRegistry, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生:", e);
        }
    }
}
