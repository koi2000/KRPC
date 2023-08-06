package com.koi.krpc.netty.client;

import com.koi.krpc.registry.NacosServiceDiscovery;
import com.koi.krpc.registry.ServiceDiscovery;
import com.koi.krpc.transport.RpcClient;
import com.koi.krpc.entity.RpcRequest;
import com.koi.krpc.entity.RpcResponse;
import com.koi.krpc.enumeration.RpcError;
import com.koi.krpc.exception.RpcException;
import com.koi.krpc.registry.NacosServiceRegistry;
import com.koi.krpc.registry.ServiceRegistry;
import com.koi.krpc.serializer.CommonSerializer;
import com.koi.krpc.util.RpcMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final EventLoopGroup group;
    private static final Bootstrap bootstrap;
    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;

    public NettyClient() {
        this.serviceDiscovery = new NacosServiceDiscovery();
    }

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()){
                group.shutdownGracefully();
                return null;
            }
            channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
            RpcResponse rpcResponse = channel.attr(key).get();
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            result.set(rpcResponse.getData());
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生: ", e);
            Thread.currentThread().interrupt();
        }
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
