package com.koi.krpc.netty.client;

import com.koi.krpc.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author koi
 * @date 2023/8/6 22:22
 */
public class UnprocessedRequests {
    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFuture =
            new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> future) {
        unprocessedResponseFuture.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFuture.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFuture
                .remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
