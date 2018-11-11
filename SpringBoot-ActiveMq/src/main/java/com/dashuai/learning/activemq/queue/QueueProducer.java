package com.dashuai.learning.activemq.queue;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Queue producer
 * <p/>
 * Created in 2018.07.01
 * <p/>
 *
 * @author Liaodashuai
 */
public class QueueProducer {
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
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = connectionFactory.createConnection();
        connection.start();
        System.out.println("Connection  is start.. ");
        Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        // 消息的目的地，发送给谁
        Queue queue = session.createQueue("liaodashuai");
        //创建消息发送者
        MessageProducer producer = session.createProducer(queue);
        //设置不持久化，学习的，真实情况视场景而定
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        //此次为测试写死，真实情况应该是用发法传参数
        sendMessage(session, producer);
        session.commit();
        connection.close();
        System.out.println("send text ok!");
    }

    /**
     * Send message.
     *
     * @param session  the session
     * @param producer the producer
     */
    public static void sendMessage(Session session, MessageProducer producer) throws Exception {
        for (int i = 1; i < 100; i++) {
            TextMessage message = session.createTextMessage("ActiveMq 发送的消息" + i);
            System.out.println("发送消息：ActiveMQ发送：" + i);
            producer.send(message);
        }
    }
}
