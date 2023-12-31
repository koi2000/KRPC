package com.koi.krpc.registry;

import java.net.InetSocketAddress;

/**
 * @author koi
 * @date 2023/8/3 22:28
 */
// 服务注册接口
public interface ServiceRegistry {
    // 将一个服务注册进注册表
    void register(String serviceName, InetSocketAddress inetSocketAddress);
}
