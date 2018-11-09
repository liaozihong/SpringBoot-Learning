package com.dashuai.learning.mongodb;

import com.spring4all.mongodb.EnableMongoPlus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 启用mongodb和增强插件
 */
@EnableMongoRepositories
@EnableMongoPlus
@SpringBootApplication
public class MongodbApplication {


    public static void main(String[] args) {
        SpringApplication.run(MongodbApplication.class, args);
    }
}
