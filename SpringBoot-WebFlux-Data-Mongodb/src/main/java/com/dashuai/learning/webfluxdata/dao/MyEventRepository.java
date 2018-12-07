package com.dashuai.learning.webfluxdata.dao;

import com.dashuai.learning.webfluxdata.model.MyEvent;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

/**
 * The interface My event repository.
 *
 * @author Liaozihong
 */
public interface MyEventRepository extends ReactiveCrudRepository<MyEvent, Long> {
    /**
     * Find by flux.
     *
     * @return the flux
     * @Tailable 注解的作用类似于linux的tail命令，被注解的方法将发送无限流，@Tailable仅支持有大小限制的
     * （“capped”）collection，而自动创建的collection是不限制大小的，因此我们需要先手动创建。
     * Spring Boot提供的CommandLineRunner可以帮助我们实现这一点
     * <p>
     * 需要注解在返回值为Flux这样的多个元素的Publisher的方法上；
     * findAll()是想要的方法，但是在ReactiveMongoRepository中我们够不着，所以使用findBy()代替。
     */
    @Tailable
    Flux<MyEvent> findBy();
}
