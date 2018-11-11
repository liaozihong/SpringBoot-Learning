package com.dashuai.learning.activemq.topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Topic subscriber
 * <p/>
 * Created in 2018.07.01
 * <p/>
 * 消息订阅者
 *
 * @author Liaodashuai
 */
public class TopicSubscriber {
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
        connection.start();
        // Session： 一个发送或接收消息的线程
        final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        // Destination ：消息的目的地;消息发送给谁.
        Topic destination = session.createTopic("topicTest");
        // 消费者，消息接收者
        MessageConsumer consumer = session.createConsumer(destination);
        //有事务限制
        consumer.setMessageListener((Message message) -> {
            try {
                TextMessage textMessage = (TextMessage) message;
                System.out.println(textMessage.getText());
            } catch (JMSException e1) {
                e1.printStackTrace();
            }
            try {
                session.commit();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}
