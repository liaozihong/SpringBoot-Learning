package com.dashuai.learning.autostarter.configure;

import com.dashuai.learning.autostarter.properties.ExampleServiceProperties;
import com.dashuai.learning.autostarter.service.ExampleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Example auto configure
 * <p/>
 * Created in 2019.06.06
 * <p/>
 *
 * @author Liaozihong
 * @ConditionalOnClass 当classpath下发现该类的情况下进行自动配置。
 */
@Configuration
@ConditionalOnClass(ExampleService.class)
@EnableConfigurationProperties(ExampleServiceProperties.class)
public class ExampleAutoConfigure {
    private ExampleServiceProperties exampleServiceProperties;

    /**
     * Instantiates a new Example auto configure.
     *
     * @param exampleServiceProperties the example service properties
     */
    public ExampleAutoConfigure(ExampleServiceProperties exampleServiceProperties) {
        this.exampleServiceProperties = exampleServiceProperties;
    }

    /**
     * Example service example service.
     * @ConditionalOnMissingBean 该实例不存在是才会执行
     * @ConditionalOnProperty 配置enable为true才会实例化
     * @return the example service
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "example.service", value = "enable", havingValue = "true")
    public ExampleService exampleService() {
        return new ExampleService(exampleServiceProperties.getPrefix(), exampleServiceProperties.getSuffix());
    }
}
