package com.koi.krpc;

public class HelloServiceImpl implements HelloService{
    @Override
    public String hello(HelloObject object) {
        return "啦啦啦";
    }
}
