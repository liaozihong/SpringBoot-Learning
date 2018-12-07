package com.dashuai.learning.webfluxdata.config;

import com.dashuai.learning.webfluxdata.model.MyEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;

@Configuration
public class WebFluxConfiguration {
    /**
     * @param mongo
     * @return
     */
    @Bean
    public CommandLineRunner initData(MongoOperations mongo) {
//        CommandLineRunner也是一个函数式接口，其实例可以用lambda表达；
        return (String... args) -> {
//            如果有，先删除collection，生产环境慎用这种操作；
//            mongo.dropCollection(MyEvent.class);
//          创建一个记录个数为10的capped的collection，容量满了之后，新增的记录会覆盖最旧的。
            mongo.createCollection(MyEvent.class, CollectionOptions.empty().maxDocuments(200)
                    .size(100000).capped());

        };
    }
}
