package com.dashuai.learning.rabbitmq.constants;

/**
 * 消息队列常量
 *
 * @author Liaozihong
 */
public final class RabbitConstants {

    /**
     * 死性队列EXCHANGE名称
     */
    public static final String MQ_EXCHANGE_DEAD_QUEUE = "test-dead-queue-exchange";

    /**
     * 死性队列名称
     */
    public static final String QUEUE_NAME_DEAD_QUEUE = "test-dead-queue";

    /**
     * 死性队列路由名称
     */
    public static final String MQ_ROUTING_KEY_DEAD_QUEUE = "test-routing-key-dead-queue";

    /**
     * 发放普通消息的EXCHANGE名称
     */
    public static final String MQ_EXCHANGE_SEND_MESSAGE = "test-send-message-exchange";

    /**
     * 发放普通消息的队列名称
     */
    public static final String QUEUE_NAME_SEND_MESSAGE = "test-send-message-queue";

    /**
     * 发放普通消息的路由key
     */
    public static final String MQ_ROUTING_KEY_SEND_MESSAGE = "test-routing-key-send-message";
    /**
     * 失败允许重试次数
     */
    public static final Integer MQ_RETRY_COUNT = 3;
}
