package com.dashuai.learning.redislock.constant;

/**
 * Application config const
 * <p/>
 * Created in 2018.12.03
 * <p/>
 *
 * @author Liaozihong
 */
public class ApplicationConfigConst {
    /**
     * The constant REDIS_IP.
     */
    public static final String REDIS_IP = "${java.redis.ip}";

    /**
     * The constant REDIS_PORT.
     */
    public static final String REDIS_PORT = "${java.redis.port}";

    /**
     * The constant REDIS_PASSWORD.
     */
    public static final String REDIS_PASSWORD = "${java.redis.password}";

    /**
     * The constant REDIS_TIMEOUT.
     */
    public static final String REDIS_TIMEOUT = "${java.redis.timeout}";

    /**
     * The constant REDIS_MAX_TOTAL.
     */
    public static final String REDIS_MAX_TOTAL = "${java.redis.max-total}";

    /**
     * The constant REDIS_MAX_IDLE.
     */
    public static final String REDIS_MAX_IDLE = "${java.redis.max-idle}";

    /**
     * The constant REDIS_MAX_WAIT_MILLIS.
     */
    public static final String REDIS_MAX_WAIT_MILLIS = "${java.redis.max-wait-millis}";

    /**
     * The constant REDIS_TEST_ON_BORROW.
     */
    public static final String REDIS_TEST_ON_BORROW = "${java.redis.test-on-borrow}";

    /**
     * The constant REDIS_TEST_ON_RETURN.
     */
    public static final String REDIS_TEST_ON_RETURN = "${java.redis.test-on-return}";

    /**
     * The constant REDIS_LOCK_TIMEOUT.
     */
    public static final String REDIS_LOCK_TIMEOUT = "${java.redis.lock-timeout}";
}
