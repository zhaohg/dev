package com.zhaohg.redis.demo1;

import com.zhaohg.redis.demo1.model.User;

public interface IRedisService {
    void doSomeHashes(final User user);

    void doSomeValues();

    void doSomeExpirations();

    void doSomeBits();
}
