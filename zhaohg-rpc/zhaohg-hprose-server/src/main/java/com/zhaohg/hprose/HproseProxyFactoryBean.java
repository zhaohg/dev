package com.zhaohg.hprose;

import hprose.client.HproseClient;
import hprose.client.HproseTcpClient;
import hprose.common.HproseFilter;
import hprose.io.HproseMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.support.UrlBasedRemoteAccessor;

/**
 * Created by larry on 15/11/2016.
 */
public class HproseProxyFactoryBean extends UrlBasedRemoteAccessor implements FactoryBean {
    
    private HproseClient client    = null;
    private Exception    exception = null;
    private boolean      keepAlive = true;
    private int          timeout   = 30000;  //超时默认30秒
    private HproseMode   mode      = HproseMode.MemberMode;
    private HproseFilter filter    = null;
    
    private String serviceUrls;
    
    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        try {
            if (StringUtils.isNotBlank(serviceUrls)) {
                client = HproseClient.create(serviceUrls.split(","), mode);
            } else {
                client = HproseClient.create(getServiceUrl(), mode);
            }
            
        } catch (Exception ex) {
            exception = ex;
        }
        
        if (client instanceof HproseTcpClient) {
            HproseTcpClient tcpClient = (HproseTcpClient) client;
            tcpClient.setTimeout(timeout);
            tcpClient.setKeepAlive(keepAlive);
        } else {
            throw new RuntimeException("不支持该协议： " + getServiceUrl());
        }
        client.setFilter(filter);
    }
    
    // for HproseHttpClient
    public void setKeepAlive(boolean value) {
        keepAlive = value;
    }
    
    // for HproseClient
    public void setTimeout(int value) {
        timeout = value;
    }
    
    public void setMode(HproseMode value) {
        mode = value;
    }
    
    public void setFilter(HproseFilter filter) {
        this.filter = filter;
    }
    
    public String getServiceUrls() {
        return serviceUrls;
    }
    
    public void setServiceUrls(String serviceUrls) {
        this.serviceUrls = serviceUrls;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object getObject() throws Exception {
        if (exception != null) {
            throw exception;
        }
        return client.useService(getServiceInterface());
    }
    
    @Override
    public Class getObjectType() {
        return getServiceInterface();
    }
    
    @Override
    public boolean isSingleton() {
        return true;
    }
    
}
