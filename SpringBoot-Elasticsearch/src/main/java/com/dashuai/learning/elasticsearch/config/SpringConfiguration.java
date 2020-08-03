package com.dashuai.learning.elasticsearch.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Spring configuration
 * <p/>
 * Created in 2018.11.29
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
public class SpringConfiguration {
    /**
     * Transport client transport client.
     * 如果配置X-PACK ,则需要在此处配置用户信息
     *
     * @return the transport client
     * @throws UnknownHostException the unknown host exception
     */
    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        TransportClient client = new PreBuiltXPackTransportClient(Settings.builder()
                .put("cluster.name", "docker-cluster")
                .put("xpack.security.user", "elastic:changeme")
                .build())
                .addTransportAddress(new TransportAddress(InetAddress.getByName("120.79.58.138"), 9300));
        return client;
    }
}
