package com.koi.krpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author koi
 * @date 2023/8/2 22:54
 */
// 消费者向服务提供者发送的请求对象
@Data
@Builder
public class RpcRequest implements Serializable {

    // 待调用的接口名称
    private String interfaceName;

    // 待调用方法名称
    private String methodName;

    // 调用方法的参数
    private Object[] parameters;

    // 调用方法的参数类型
    private Class<?>[] paramTypes;
}