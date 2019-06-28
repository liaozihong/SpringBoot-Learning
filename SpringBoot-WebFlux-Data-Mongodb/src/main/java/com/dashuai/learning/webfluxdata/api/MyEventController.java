package com.dashuai.learning.webfluxdata.api;

import com.dashuai.learning.webfluxdata.dao.MyEventRepository;
import com.dashuai.learning.webfluxdata.model.MyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/events")
public class MyEventController {
    @Autowired
    private MyEventRepository myEventRepository;

    /**
     * POST方法的接收数据流的Endpoint，所以传入的参数是一个Flux，返回结果其实就看需要了，
     * 我们用一个Mono<Void>作为方法返回值，表示如果传输完的话只给一个“完成信号”就OK了；
     * <p>
     * 指定传入的数据是application/stream+json，与getEvents方法的区别在于这个方法是consume这个数据流；
     *
     * @param events
     * @return
     */
    @PostMapping(path = "", consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Mono<Void> loadEvents(@RequestBody Flux<MyEvent> events) {   // 1
//        saveAll返回的是保存成功的记录的Flux，但我们不需要，使用then方法表示“忽略数据元素，只返回一个完成信号”。
        return this.myEventRepository.saveAll(events).then();
    }

    /**
     * GET方法的无限发出数据流的Endpoint，所以返回结果是一个Flux<MyEvent>，
     * 不要忘了注解上produces = MediaType.APPLICATION_STREAM_JSON_VALUE。
     *
     * @return
     */
    @GetMapping(path = "", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<MyEvent> getEvents() {  // 2
        return this.myEventRepository.findBy();
    }
}
