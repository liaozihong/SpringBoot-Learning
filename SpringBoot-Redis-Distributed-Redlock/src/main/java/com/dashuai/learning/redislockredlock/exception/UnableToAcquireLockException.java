package com.dashuai.learning.redislockredlock.exception;

/**
 * Unable to aquire lock exception
 * <p/>
 * Created in 2018.12.05
 * <p/>
 *
 * @author Liaozihong
 */
public class UnableToAcquireLockException extends RuntimeException {
    /**
     * Instantiates a new Unable to aquire lock exception.
     */
    public UnableToAcquireLockException() {
    }

    /**
     * Instantiates a new Unable to aquire lock exception.
     *
     * @param message the message
     */
    public UnableToAcquireLockException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Unable to aquire lock exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnableToAcquireLockException(String message, Throwable cause) {
        super(message, cause);
    }
}
