package com.koi.krpc.netty.client;

import com.koi.krpc.codec.CommonDecoder;
import com.koi.krpc.codec.CommonEncoder;
import com.koi.krpc.enumeration.RpcError;
import com.koi.krpc.exception.RpcException;
import com.koi.krpc.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author koi
 * @date 2023/8/5 16:45
 */
// 获取Channel对象
public class ChannelProvider {
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static Channel get(InetSocketAddress inetSocketAddress,
                              CommonSerializer serializer) throws InterruptedException {
        String key = inetSocketAddress.toString() + serializer.getCode();
        if (channels.containsKey(key)) {
            Channel channel = channels.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channels.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                // 自定义序列化编解码器
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS))
                        .addLast(new NettyClientHandler());
            }
        });
        Channel channel = null;
        try {
            channel = connect(bootstrap, inetSocketAddress);
        } catch (ExecutionException e) {
            logger.error("连接客户端时有错误发生：", e);
            return null;
        }
        channels.put(key, channel);
        return channel;
    }

    private static Channel connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress)
            throws ExecutionException, InterruptedException {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("客户端连接成功!");
                        completableFuture.complete(future.channel());
                    } else {
                        throw new IllegalStateException();
                    }
                });
        return completableFuture.get();
    }

    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接的超时时间，超过这个时间仍建立连接失败的话就代表连接建立失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 是否开启TCP底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                // TCP默认开启了Nagle算法，作用为尽可能的发送大数据快，减少网络传输
                // 该参数可以控制是否启用Nagle算法
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
}
