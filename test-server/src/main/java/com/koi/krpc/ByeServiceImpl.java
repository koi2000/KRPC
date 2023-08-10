package com.koi.krpc;

import com.koi.krpc.annotation.Service;

@Service
public class ByeServiceImpl implements ByeService {
    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
