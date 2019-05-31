package com.zhaohg.redis;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 第一个jedisPool测试
 * @author leifu
 * @Date 2015年8月24日
 * @Time 下午1:35:26
 * 注意：jedis.close的实现：
 * (1) dataSource!=null代表使用的是连接池,所以jedis.close()代表归还连接给连接池
 * (2) dataSource=null代表直连，jedis.close代表关闭连接
 * (3) jedis.close放到finally里面做
 */
public class JedisPoolTest {

    /**
     * redis单机host
     */
    private final static String    REDIS_HOST          = "127.0.0.1";
    /**
     * redis单机port
     */
    private final static int       REDIS_PORT          = 6379;
    /**
     * 超时时间(毫秒)
     */
    private final static int       JEDIS_POOL_TIME_OUT = 1000;
    private static       JedisPool jedisPool;
    private              Logger    logger              = LoggerFactory.getLogger(JedisPoolTest.class);

    @BeforeClass
    public static void testBeforeClass() {
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxTotal(GenericObjectPoolConfig.DEFAULT_MAX_TOTAL * 5);
        poolConfig.setMaxIdle(GenericObjectPoolConfig.DEFAULT_MAX_IDLE * 3);
        poolConfig.setMinIdle(GenericObjectPoolConfig.DEFAULT_MIN_IDLE * 2);
        poolConfig.setJmxEnabled(true);
        poolConfig.setMaxWaitMillis(3000);

        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, JEDIS_POOL_TIME_OUT);
    }

    @AfterClass
    public static void testAfterClass() {
        if (jedisPool != null) {
            jedisPool.destroy();
        }
    }

    @Test
    public void testJedisPool() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "sohuKeyPool";
            jedis.set(key, "sohuValue");
            String value = jedis.get(key);
            logger.info("get key {} from redis, value is {}", key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                // 如果使用JedisPool,close操作不是关闭连接，代表归还资源池
                jedis.close();
            }
        }
    }
}
