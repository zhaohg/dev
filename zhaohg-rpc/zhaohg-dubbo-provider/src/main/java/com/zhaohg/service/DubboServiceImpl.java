package com.zhaohg.service;

import org.springframework.stereotype.Service;

@Service("dubboService")
public class DubboServiceImpl implements DubboService {

    @Override
    public int calculate(int a, int b) {
        return a + b;
    }

}
