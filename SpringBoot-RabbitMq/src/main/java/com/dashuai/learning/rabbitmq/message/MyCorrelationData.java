package com.dashuai.learning.rabbitmq.message;

import org.springframework.amqp.rabbit.support.CorrelationData;

/**
 * My correlation data
 * <p/>
 * Created in 2018.11.08
 * <p/>
 *
 * @author Liaodashuai
 */
public class MyCorrelationData extends CorrelationData {
    /**
     * 消息体
     */
    private volatile Object message;

    /**
     * 交换机名称
     */
    private String exchange;

    /**
     * 路由key
     */
    private String routingKey;

    /**
     * 重试次数
     */
    private int retryCount = 0;

    /**
     * Instantiates a new My correlation data.
     */
    public MyCorrelationData() {
        super();
    }

    /**
     * Instantiates a new My correlation data.
     *
     * @param id the id
     */
    public MyCorrelationData(String id) {
        super(id);
    }

    /**
     * Instantiates a new My correlation data.
     *
     * @param id   the id
     * @param data the data
     */
    public MyCorrelationData(String id, Object data) {
        this(id);
        this.message = data;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public Object getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(Object message) {
        this.message = message;
    }

    /**
     * Gets retry count.
     *
     * @return the retry count
     */
    public int getRetryCount() {
        return retryCount;
    }

    /**
     * Sets retry count.
     *
     * @param retryCount the retry count
     */
    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    /**
     * Gets exchange.
     *
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * Sets exchange.
     *
     * @param exchange the exchange
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * Gets routing key.
     *
     * @return the routing key
     */
    public String getRoutingKey() {
        return routingKey;
    }

    /**
     * Sets routing key.
     *
     * @param routingKey the routing key
     */
    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }
}
