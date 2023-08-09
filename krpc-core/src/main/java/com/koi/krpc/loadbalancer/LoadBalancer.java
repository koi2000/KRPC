package com.koi.krpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @author koi
 * @date 2023/8/9 21:40
 */
public interface LoadBalancer {
    Instance select(List<Instance> instances);
}
