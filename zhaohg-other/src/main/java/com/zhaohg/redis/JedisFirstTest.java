package com.zhaohg.redis;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.Date;

/**
 * 第一个jedis测试
 * @author zhaohg
 */
public class JedisFirstTest {
    /**
     * redis单机host
     */
    private final static String JEDIS_HOST = "127.0.0.1";
    /**
     * redis单机port
     */
    private final static int    JEDIS_PORT = 6379;
    /**
     * 超时时间(毫秒)
     */
    private final static int JEDIS_TIME_OUT = 300;
    private              Logger logger     = LoggerFactory.getLogger(JedisFirstTest.class);

    @Test
    public void testJedis() {
        Jedis jedis = null;
        try {
            jedis = new Jedis(JEDIS_HOST, JEDIS_PORT, JEDIS_TIME_OUT);
            String key = "zhaoKey";
            jedis.set(key, "zhaoValue");
            String value = jedis.get(key);
            logger.info("get key {} from redis, value is {}", key, value);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Test
    public void testJedisSerializable() {
        ProtostuffSerializer protostuffSerializer = new ProtostuffSerializer();
        Jedis jedis = null;
        try {
            jedis = new Jedis(JEDIS_HOST, JEDIS_PORT, JEDIS_TIME_OUT);
            String key = "sohuKeySerializable";
            // 序列化
            Club club = new Club(1, "AC", "米兰", new Date(), 1);
            byte[] clubBtyes = protostuffSerializer.serialize(club);
            jedis.set(key.getBytes(), clubBtyes);
            // 反序列化
            byte[] resultBtyes = jedis.get(key.getBytes());
            Club resultClub = protostuffSerializer.deserialize(resultBtyes);
            logger.info("get key {} from redis, value is {}", key, resultClub);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}