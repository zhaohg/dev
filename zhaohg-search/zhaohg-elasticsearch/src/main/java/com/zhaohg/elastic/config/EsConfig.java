package com.zhaohg.elastic.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @program: elastic
 * @description: 配置Elasticsearch客户端信息，获取客户端
 * @author: zhaohg
 * @create: 2018-08-21 21:42
 **/
@Configuration
public class EsConfig {
    
    @Value("${elasticsearch.cluster-nodes}")
    private String clusterNodes;
    
    @Value("${elasticsearch.cluster-name}")
    private String clusterName;
    
    @Bean
    public Client client() {
        Settings settings = Settings.builder().put("cluster.name", clusterName)
                .put("client.transport.sniff", true).build();
        
        TransportClient client = new PreBuiltTransportClient(settings);
        try {
            if (clusterNodes != null && !"".equals(clusterNodes)) {
                TransportAddress[] transportAddresses = {
                        new InetSocketTransportAddress(InetAddress.getByName("192.168.52.35"), 9300),
                        new InetSocketTransportAddress(InetAddress.getByName("192.168.52.36"), 9300),
                        new InetSocketTransportAddress(InetAddress.getByName("192.168.52.34"), 9300),
                        new InetSocketTransportAddress(InetAddress.getByName("192.168.52.33"), 9300)
                };
                
                client.addTransportAddresses(transportAddresses);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        
        return client;
    }
    
}
