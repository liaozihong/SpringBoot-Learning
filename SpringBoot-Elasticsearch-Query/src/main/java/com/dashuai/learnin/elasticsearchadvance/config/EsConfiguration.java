package com.dashuai.learnin.elasticsearchadvance.config;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

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
@Slf4j
public class EsConfiguration {

    private Client esClient;

    /**
     * Transport client transport client.
     * 如果配置X-PACK ,则需要在此处配置用户信息
     *
     * @return the transport client
     * @throws UnknownHostException the unknown host exception
     */
    @Bean
    public Client transportClient() {
        TransportClient client = null;
        try {
            client = new PreBuiltXPackTransportClient(Settings.builder()
                    //嗅探集群状态
//                    .put("client.transport.sniff", true)
                    .put("cluster.name", "docker-cluster")
                    .put("xpack.security.user", "elastic:changeme")
                    .build())
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("120.79.58.138"), 9300));
        } catch (UnknownHostException e) {
            log.error("elasticsearch 连接失败 !");
        }
        return client;
    }

    /**
     * 避免TransportClient每次使用创建和释放
     */
    public Client esTemplate() {
        if (StringUtils.isEmpty(esClient) || StringUtils.isEmpty(esClient.admin())) {
            esClient = transportClient();
            return esClient;
        }
        return esClient;
    }
}
