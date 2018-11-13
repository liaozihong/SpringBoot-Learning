package com.dashuai.learning.nsq.service;

/**
 * The interface Mq product service.
 * Created in 2018.11.13
 *
 * @author Liaozihong
 */
public interface MqProductService {
    /**
     * Mq product boolean.
     *
     * @param body the body
     * @return the boolean
     */
    boolean mqProduct(String body);
}
