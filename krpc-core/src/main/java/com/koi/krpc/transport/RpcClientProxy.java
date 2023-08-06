package com.koi.krpc.transport;

import com.koi.krpc.entity.RpcRequest;
import com.koi.krpc.entity.RpcResponse;
import com.koi.krpc.netty.client.NettyClient;
import com.koi.krpc.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

// RPC客户端动态代理
public class RpcClientProxy implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RpcClient client;

    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }

    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RpcRequest rpcRequest = new RpcRequest(UUID.randomUUID().toString(), method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes(), false);
        Object result = null;
        if (client instanceof NettyClient) {
            CompletableFuture<RpcResponse> completableFuture =
                    (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
            try {
                result = completableFuture.get().getData();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            RpcResponse rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
            result = rpcResponse.getData();
        }
        return result;
    }
}
