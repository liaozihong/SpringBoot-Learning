package com.dashuai.learning.activemq.topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Topic publisher
 * <p/>
 * Created in 2018.07.01
 * <p/>
 * 消息发布者
 *
 * @author Liaodashuai
 */
public class TopicPublisher {
    private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static String url = "tcp://127.0.0.1:61616";

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        // ConnectionFactory ：连接工厂，JMS 用它创建连接
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = connectionFactory.createConnection();
        // Connection 启动
        connection.start();
        System.out.println("Connection is start...");
        // Session： 一个发送或接收消息的线程
        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        //消息的目的地
        Topic topic = session.createTopic("topicTest");
        MessageProducer producer = session.createProducer(topic);
        //设置不持久化，test，具体情况看需求
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        // 构造消息，此处写死，项目就是参数，或者方法获取
        sendMessage(session, producer);
        session.commit();

        connection.close();
        System.out.println("send text ok.");
    }

    /**
     * Send message.
     *
     * @param session  the session
     * @param producer the producer
     * @throws Exception the exception
     */
    public static void sendMessage(Session session, MessageProducer producer)
            throws Exception {
        //有限制,达到1000就不行
        for (int i = 1; i <= 100; i++) {
            TextMessage message = session.createTextMessage("ActiveMq 发送的消息" + i);
            // 发送消息到目的地方
            System.out.println("发送消息：" + "ActiveMq 发送的消息" + i);
            producer.send(message);
        }
    }
}