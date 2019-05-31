package com;

import com.google.common.base.Objects;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zhaohg
 * @date 2018/12/18.
 */
@Slf4j
public class test {
    
    public static void main(String[] args) throws ExecutionException {
        Map<String, Integer> map = Maps.newHashMapWithExpectedSize(10);
        List<String> list = Lists.newArrayListWithCapacity(10);
        Set<String> set = Sets.newCopyOnWriteArraySet();
        int result = ComparisonChain.start().compare(-11, -1).result();
        System.out.println(result);
        
        ListeningExecutorService service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
        String s = String.format("a %s - b %s", 1, 2);
        
        System.out.println(s.toString());
        
        HostAndPort hostAndPort = HostAndPort.fromString("127.0.0.1:8080");
        System.out.println("host == " + hostAndPort.getHost());
        System.out.println("port == " + hostAndPort.getPortOrDefault(80));
        
        int code = Objects.hashCode(hostAndPort);
        java.util.Objects.hashCode(hostAndPort);
        
        Cache<String, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(10, TimeUnit.SECONDS).maximumSize(500).build();
        cache.get("key", new Callable<Object>() { //Callable 加载
            @Override
            public Object call() throws Exception {
                return "value";
            }
        });
        
        
        LoadingCache<String, Object> loadingCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.SECONDS).maximumSize(5).build(new CacheLoader<String, Object>() {
            @Override
            public Object load(String key) throws Exception {
                return "value";
            }
        });
        
        
        CacheBuilder.newBuilder().weigher(new Weigher<String, Object>() {
            @Override
            public int weigh(String key, Object value) {
                //the value.size()
                return 0;
            }
        }).expireAfterWrite(10, TimeUnit.SECONDS).maximumWeight(500).build();
        
        
        LinkedList list1 = new LinkedList();
    }
    
    private static void a(int i) {
        System.out.println(i++);
    }
    
}
