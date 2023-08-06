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
import java.util.concurrent.CountDownLatch;
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

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    public static Channel get(InetSocketAddress inetSocketAddress,
                              CommonSerializer serializer) {
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
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap, inetSocketAddress, countDownLatch);
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取channel时有错误发生：", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,
                                CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,
                                int retry, CountDownLatch countDownLatch) {
        bootstrap.connect(inetSocketAddress)
                .addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        logger.info("客户端连接成功!");
                        channel = future.channel();
                        countDownLatch.countDown();
                        return;
                    }
                    if (retry == 0) {
                        logger.error("客户端连接失败：重试次数以用完，放弃连接");
                        countDownLatch.countDown();
                        throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
                    }
                    // 第几次重连
                    int order = (MAX_RETRY_COUNT - retry) + 1;
                    // 本次重连的间隔
                    int delay = 1 << order;
                    logger.error("{}: 连接失败，第{}次重连....", new Date(), order);
                    bootstrap.config().group().schedule(() ->
                                    connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch), delay,
                            TimeUnit.SECONDS);
                });
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
