package com.dashuai.learning.zookeeper.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Wrapper zk
 * <p/>
 * Created in 2018.11.12
 * <p/>
 *
 * @author Liaozihong
 */
@Component
@Data
@ConfigurationProperties(prefix = "curator")
public class WrapperZk {
    /**
     * 重试次数
     */
    private int retryCount;
    /**
     * 重试间隔时间
     */
    private int elapsedTimeMs;
    /**
     * 连接地址
     */
    private String connectString;
    /**
     * Session过期时间
     */
    private int sessionTimeoutMs;
    /**
     * 连接超时时间
     */
    private int connectionTimeoutMs;

}
