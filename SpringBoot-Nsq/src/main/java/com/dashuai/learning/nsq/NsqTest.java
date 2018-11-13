package com.dashuai.learning.nsq;


import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.exceptions.NSQException;
import com.github.brainlag.nsq.lookup.DefaultNSQLookup;
import com.github.brainlag.nsq.lookup.NSQLookup;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;

/**
 * Nsq test
 * Created in 2018.11.11
 *
 * @author Liaozihong
 */
public class NsqTest {
    /**
     * Nsq producer.
     */
//    生产者：
    public static void nsqProducer() {
        NSQProducer producer = new NSQProducer();
        producer.addAddress("127.0.0.1", 4150).start();
        try {
            producer.produce("nimei", "廖大帅".getBytes());
        } catch (NSQException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * Nsq consumer.
     */
//    消费者：
    public static void nsqConsumer() {
        NSQLookup lookup = new DefaultNSQLookup();
        lookup.addLookupAddress("127.0.0.1", 4161);
        NSQConsumer consumer = new NSQConsumer(lookup, "nimei", "made", (message) -> {
            System.out.println("received: " + new String(message.getMessage()));
            //now mark the message as finished.
            message.finished();

            //or you could requeue it, which indicates a failure and puts it back on the queue.
            //message.requeue();
        });

        consumer.start();
        //线程睡眠，让程序执行完
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        consumer.setExecutor(new Executor() {
            @Override
            public void execute(Runnable command) {
                command.run();
            }
        });
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        nsqProducer();
        nsqConsumer();
    }
}
