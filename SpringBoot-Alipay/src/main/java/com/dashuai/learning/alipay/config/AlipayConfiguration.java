package com.dashuai.learning.alipay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfiguration {
    @Value("${alipay.ali_return_url}")
    private String ali_return_url;

    @Value("${alipay.ali_notify_url}")
    private String ali_notify_url;

//    @Value("${alipay.product_no}")
//    private String product_no;

    @Value("${alipay.time_express}")
    private String time_express;

    @Value("${alipay.gatewary.url}")
    private String url;

    @Value("${alipay.appid}")
    private String appid;

    @Value("${alipay.private_key}")
    private String private_key;

    @Value("${alipay.ali_public_key}")
    private String ali_public_key;

    @Value("${alipay.sign_type}")
    private String sign_type;


    @Bean
    public AlipayClient alipayClient() {
        return new DefaultAlipayClient(url, appid
                , private_key, "json", "UTF-8"
                , ali_public_key, sign_type);
    }
}
