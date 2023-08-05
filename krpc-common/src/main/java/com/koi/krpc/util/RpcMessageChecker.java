package com.koi.krpc.util;

import com.koi.krpc.entity.RpcRequest;
import com.koi.krpc.entity.RpcResponse;
import com.koi.krpc.enumeration.ResponseCode;
import com.koi.krpc.enumeration.RpcError;
import com.koi.krpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author koi
 * @date 2023/8/5 16:06
 */
// 检查响应与请求
public class RpcMessageChecker {

    private static final String INTERFACE_NAME = "interfaceName";
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);

    private RpcMessageChecker(){}

    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse){
        if (rpcResponse==null){
            logger.error("调用服务失败, serviceName:{}",rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }

}
