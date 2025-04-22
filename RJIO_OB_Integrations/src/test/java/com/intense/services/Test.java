package com.intense.services;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.*;

public class Test {
    public static void main(String[] args) {
        // ActiveMQ connection parameters
        String brokerURL = "tcp://172.16.0.227:61616";  // ActiveMQ broker URL
        String queueName = "MNP_AIDUI_VALIDATION_Q1";         // Name of the already existing queue

        // Create a connection factory
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
        
        // Sample XML content
        String messageIP = "{\r\n"
        		+ "    \"resultStatus\": {\r\n"
        		+ "        \"status\": \"SUCCESS\"\r\n"
        		+ "    },\r\n"
        		+ "    \"transc_id\": \"SKYC00922071\",\r\n"
        		+ "    \"upc\": \"TA560123\",\r\n"
        		+ "    \"msisdn\": \"9810798107\"\r\n"
        		+ "}";

        try {
            // Create a connection
            Connection connection = connectionFactory.createConnection();
            connection.start();  // Start the connection

            // Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Lookup the existing queue (no need to create it, assuming it already exists)
            Destination queue = session.createQueue(queueName);

            // Create a producer to send a message to the queue
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage(messageIP);
            
            // Send the XML message to the existing queue
            producer.send(message);
            System.out.println("Sent to queue :: " + messageIP);

            // Clean up
            producer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
