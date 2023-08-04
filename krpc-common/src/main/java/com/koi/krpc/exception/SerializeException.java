package com.koi.krpc.exception;

/**
 * @author koi
 * @date 2023/8/4 22:10
 */
// 序列化异常
public class SerializeException extends RuntimeException{
    public SerializeException(String message) {
        super(message);
    }
}
