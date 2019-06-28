package com.dashuai.learning.thrift.config;

import com.dashuai.learning.thrift.client.RPCThriftClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RPCThriftClientConfig {
    @Value("${thrift.host}")
    private String host;
    @Value("${thrift.port}")
    private int port;

    @Bean(initMethod = "init")
    public RPCThriftClient rpcThriftClient() {
        RPCThriftClient rpcThriftClient = new RPCThriftClient();
        rpcThriftClient.setHost(host);
        rpcThriftClient.setPort(port);
        return rpcThriftClient;
    }

}
