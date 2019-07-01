package com.zhaohg.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @program: guava
 * @description: 缓存
 * @author: zhaohg
 * @create: 2018-08-30 21:37
 **/
public class Test13 {

    public static void main(String[] args) {
        cacheLoader();
        callback();
    }

    public static void cacheLoader() {
        try {
            CacheLoader<String, String> cacheLoader = new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    String strProValue = "hello " + key + "!";
                    return strProValue;
                }
            };
            LoadingCache<String, String> cahceBuilder = CacheBuilder.newBuilder().build(cacheLoader);
            
            System.out.println(cahceBuilder.get("begincode"));  //hello begincode!
            System.out.println(cahceBuilder.get("begincode")); //hello begincode!
            System.out.println(cahceBuilder.get("wen")); //hello wen!
            System.out.println(cahceBuilder.get("wen")); //hello wen!
            System.out.println(cahceBuilder.get("da"));//hello da!
            cahceBuilder.put("begin", "code");
            System.out.println(cahceBuilder.get("begin")); //code
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void callback() {
        try {
            Cache<String, String> cache = CacheBuilder.newBuilder().maximumSize(1000).build();
            String resultVal = cache.get("code", () -> {
                String strProValue = "begin " + "code" + "!";
                return strProValue;
            });
            System.out.println("value : " + resultVal); //value : begin code!
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
