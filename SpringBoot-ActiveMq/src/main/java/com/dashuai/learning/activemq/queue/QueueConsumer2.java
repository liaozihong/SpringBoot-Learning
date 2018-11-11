package com.dashuai.learning.activemq.queue;


import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * Queue consumer 2
 * <p/>
 * Created in 2018.07.01
 * <p/>
 * 点到点的消费者只能有一个，消费了便没了。
 *
 * @author Liaodashuai
 */
public class QueueConsumer2 {
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
        final Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("liaodashuai");
        MessageConsumer messageConsumer = session.createConsumer(queue);
        messageConsumer.setMessageListener((Message message) -> {
            try {
                TextMessage textMessage = (TextMessage) message;
                System.out.println(textMessage.getText());
                session.commit();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        });
    }
}
