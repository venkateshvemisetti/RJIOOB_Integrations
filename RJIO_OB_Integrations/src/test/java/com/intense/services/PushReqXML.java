package com.intense.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PushReqXML {
	//String filename="EKYC_MH_Ind.xml";
	//String filename="DKYC_MNP-ROI.xml"; //contains outstation -0005
	String filename="PaperCAF_out.xml";
	//String filename ="SKYC.xml";
	//String filename="PaperCAF_JK.xml";

	public static void main(String[] args) throws InterruptedException {

		PushReqXML p=new PushReqXML();
		p.writeReqXML();
		Thread.sleep(5000);
		p.pushToQueue();

	}
	public void writeReqXML() {
		try {
			
			
			  String CAFNumber="PAPERCAQ20"; String ORNNumber="PAPERCAQ0121"; String
			  partyid="200349120"; String MSISDN="7066656120"; //String EID="343434";
			 			 			
			
			/*
			 * String CAFNumber="SKYC922059"; String ORNNumber="SKYC00922059"; String
			 * partyid="201134959"; String MSISDN="7646562059"; //
			 */			 			int eidReq=0;
			 String EID="303437";
			
			//File fis = new File("SKYC_MH.xml");
			 File fis =new File(filename);
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = null;
			dbuilder = dbfactory.newDocumentBuilder();
			Document doc = null;
			doc = dbuilder.parse(fis);
			NodeList nodes;
			Element element;
			nodes = doc.getElementsByTagName("order");
			element = (Element) nodes.item(0);
			element.getElementsByTagName("orderRefNumber").item(0).setTextContent(ORNNumber);

			nodes=doc.getElementsByTagName("CustomerAcquisitionAgreement");
			element = (Element) nodes.item(0);
			element.getElementsByTagName("number").item(0).setTextContent(CAFNumber);

			nodes=doc.getElementsByTagName("customer");
			element = (Element) nodes.item(0);
			element.getElementsByTagName("partyId").item(0).setTextContent(partyid);


			nodes= doc.getElementsByTagName("Identifier");
			element = (Element) nodes.item(0);
			element.getElementsByTagName("name").item(0).setTextContent("MSISDN");
			element.getElementsByTagName("value").item(0).setTextContent(MSISDN);

			
			if(eidReq==1) {
			  nodes = doc.getElementsByTagName("ResourceCharacteristic"); element =
			  (Element) nodes.item(0);
			  element.getElementsByTagName("name").item(0).setTextContent("EID");
			  element.getElementsByTagName("value").item(0).setTextContent(EID);
		}
		else {
			System.out.println("eidReq skipped");
		}
		
			 


			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(fis);
			transformer.transform(source, result);

			/*--------------------------To print on console----------*/
			// StreamResult consoleResult = new StreamResult(System.out);
			// transformer.transform(source, consoleResult);

			System.out.println("XML file updated successfully.");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void pushToQueue() {
		String brokerURL = "tcp://172.16.0.227:61616";  // ActiveMQ broker URL
		String queueName = "RJIO_STRT";         // Name of the already existing queue

		// Create a connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);

		String filePath = filename; // Path to the file you want to read (adjust the path as necessary)

        // Read the file content directly
        String messageIP = null;
        try {
            // Read all bytes from the file and convert it to a string
            byte[] fileBytes = null;
			try {
				fileBytes = Files.readAllBytes(Paths.get(filePath));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            messageIP = new String(fileBytes);
				
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