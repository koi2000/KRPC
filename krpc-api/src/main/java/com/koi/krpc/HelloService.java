package com.koi.krpc;

/**
 * @author koi
 * @date 2023/8/2 22:51
 */
// 供远端调用的service
public interface HelloService {
    String hello(HelloObject object);
}
