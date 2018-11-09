package com.dashuai.learning.provider.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Dubbo configuration
 * <p/>
 * Created in 2018.11.09
 * <p/>
 *
 * @author Liaozihong
 */
@Configuration
@ImportResource("classpath:dubbo/*.xml")
public class DubboConfiguration {

}
