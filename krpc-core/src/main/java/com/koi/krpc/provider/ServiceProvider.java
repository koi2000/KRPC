package com.koi.krpc.provider;

/**
 * @author koi
 * @date 2023/8/5 21:49
 */
// 保存和提供服务实例对象
public interface ServiceProvider {

    <T> void addServiceProvider(T service, String serviceName);

    Object getServiceProvider(String serviceName);
}
