package com.koi.krpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.koi.krpc.enumeration.RpcError;
import com.koi.krpc.exception.RpcException;
import io.protostuff.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author koi
 * @date 2023/8/5 22:05
 */
public class NacosServiceRegistry implements ServiceRegistry{
    private static final Logger logger =
            LoggerFactory.getLogger(NacosServiceRegistry.class);

    private static final String SERVER_ADDR = "127.0.0.1:8848";
    private static final NamingService namingService;

    static {
        try {
            namingService = NamingFactory.createNamingService(SERVER_ADDR);
        }catch (NacosException e){
            logger.error("连接到Nacos时有错误发生: ",e);
            throw new RpcException(RpcError.FAILED_TO_CONNECT_TO_SERVICE_REGISTRY);
        }
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try{
            namingService.registerInstance(serviceName,
                    inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        }catch (NacosException e){
            logger.error("注册服务时有错误发生:",e);
            throw new RpcException(RpcError.REGISTRY_SERVICE_FAILED);
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> instances = namingService.getAllInstances(serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(),
                    instance.getPort());
        }catch (NacosException e){
            logger.error("获取服务时有错误发生: ",e);
        }
        return null;
    }
}
