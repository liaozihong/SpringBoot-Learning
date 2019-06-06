package com.dashuai.learning.autostarter.service;

/**
 * Example service
 * <p/>
 * Created in 2019.06.06
 * <p/>
 *
 * @author Liaozihong
 */
public class ExampleService {
    private String prefix;
    private String suffix;

    /**
     * Instantiates a new Example service.
     *
     * @param prefix the prefix
     * @param suffix the suffix
     */
    public ExampleService(String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    /**
     * Wrap string.
     *
     * @param word the word
     * @return the string
     */
    public String wrap(String word) {
        return prefix + word + suffix;
    }
}
