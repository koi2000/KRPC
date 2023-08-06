package com.koi.krpc.registry;

import java.net.InetSocketAddress;

/**
 * @author koi
 * @date 2023/8/6 12:13
 */
// 服务发现接口
public interface ServiceDiscovery {

    // 根据服务名称查找服务实体
    InetSocketAddress lookupService(String serviceName);
}
