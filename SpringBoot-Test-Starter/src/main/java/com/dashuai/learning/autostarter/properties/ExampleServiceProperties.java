package com.dashuai.learning.autostarter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Example service properties
 * <p/>
 * Created in 2019.06.06
 * <p/>
 *
 * @author Liaozihong
 */
@ConfigurationProperties("example.service")
public class ExampleServiceProperties {
    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
