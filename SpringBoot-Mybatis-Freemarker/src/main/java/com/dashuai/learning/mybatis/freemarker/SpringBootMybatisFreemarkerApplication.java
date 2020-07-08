package com.dashuai.learning.mybatis.freemarker;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Spring boot mybatis freemarker application
 * Created in 2020.07.08
 *
 * @author Liaozihong
 */
@SpringBootApplication(scanBasePackages = "com.dashuai.learning.mybatis.freemarker")
@MapperScan(basePackages = "com.dashuai.learning.mybatis.freemarker.dao")
public class SpringBootMybatisFreemarkerApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(SpringBootMybatisFreemarkerApplication.class, args);
    }

    /**
     * 更改springboot 默认的json解析
     *
     * @return
     */
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return objectMapper;
    }


}
