package com.dashuai.learning.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Web socket configuration
 * <p/>
 * Created in 2018.12.04
 * <p/>
 * WebSocket 配置
 *
 * @author Liaozihong
 */
@EnableWebSocket
@Configuration
public class WebSocketConfiguration {
    /**
     * Server endpoint exporter server endpoint exporter.
     *
     * @return the server endpoint exporter
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
