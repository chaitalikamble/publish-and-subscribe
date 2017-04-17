/*
 * Description:		Event manager server class that manages the Pub Sub System
 * Authors:			Chaitali Kamble (csk3565)
 * Date:			03/12/2017
 */
import java.net.*;
import java.util.*; 
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

public class EventManagerServer extends Thread {
	/*
	 * The event manager server class makes the connection with client and get inputs and produces 
	 * appropriate outputs.It has a facility to store the data. Server creates a thread for each and every 
	 * client and sends it to event manager server class. 
	 */
	
	
	/*
	 * ****************Data Structures to store incoming information from clients.*************************
	 */
	
	/*
	 * TopicKeywords is a hashmap which stores name of the topic as a key and its keywords as values 
	 */
	static Map<String, String[]> TopicKeywords = new ConcurrentHashMap<String, String[]>();

	/*
	 * Event titles is a hashmap which stores topics as a key and its corresponding events in a list as values
	 */
	static Map<String, ArrayList<String>> EventTitles = new ConcurrentHashMap<String, ArrayList<String>>(); 	
	
	/*
	 * Event Content is a hashmap which stores event as a key and its corresponding content as value.
	 */
	static Map<String, String> EventContent = new ConcurrentHashMap<String, String>(); // event
	
	/*
	 * Subscriber List is a hashmap which stores topics as a key and its list of subscribers as values.
	 */
	static Map<String, Set<String>> SubscriberList = new ConcurrentHashMap<String, Set<String>>(); 
	
	/*
	 * Topic List is a hashmap which stores subscriber as a key and its corresponding subscribed topics as a list 
	 * of values.
	 */
	static Map<String, Set<String>> topicList = new ConcurrentHashMap<String, Set<String>>(); 
	
	/*
	 * ********** Variables Declarations **********************
	 */
	ServerSocket server;
	int event_id;
	int topic_id;
	String[] keywords;
	String topic_name;
	String event_title;
	String event_content;
	String ip;
	ArrayList<String> events;
	Set<String> subscribers;
	Set<String> topics;
	String clientMessage = "";
	static String notification = "";
	
	/*
	 * This message is always goes to client whenever he connects.
	 */
	String initialMessage = "1.	List all topics\n2.	Publish Event\n3.	Advertise Topic\n"
			+ "4.	Subscribe for topic\n5.	List all subscribed topics\n"
			+ "6.	Unsubscribe from a topic\n7.	Unsubscribe\n" + "c.	Continue\n" + "q.	Exit\n";
	String response = "";
	String msg = "";
	
	private Socket socket = null;
	
	public EventManagerServer(Socket socket) {
		/*
		 * A constructor that initializes a socket.
		 */
		this.socket = socket;
	}

	public synchronized void run() {
		/*
		 * Thread comes into this method to run and performs process.
		 */
		DataInputStream is = null;
		DataOutputStream os = null;
		try {
			is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
			os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			/*
			 * Send data directly to client
			 */
			sendData(os, initialMessage.getBytes());
			while (true) {
				byte[] byteData = receiveData(is);
				clientMessage = new String(byteData).trim();
				 // If client enters "q" then disconnect client
				if (clientMessage.equals("q")) { 
					break;
				}
				// If client enters "q" then continue the process for client

				if (clientMessage.equals("c")) {
					response = initialMessage;
				} else {
					synchronized (this) {
						 //Get client message and process it.
						response = doProcess(clientMessage);
					}
				}
				response = notification + '\n' + response;
				
				//Send response message to client.
				sendData(os, response.getBytes());
				//notification = "";
			}
		} catch (Exception e) {
			/*
			 * If any exception is encountered the client gets disconnected.
			 */
			System.out.println("Client Disconnected !!!");
		}

	}

	private static synchronized void sendData(DataOutputStream os, byte[] byteData) {
		/*
		 * This method sends data to client.
		 */
		try {
			os.write(byteData);
			os.flush();
		} catch (Exception exception) {
		}
	}

	private String doProcess(String message) {
		/*
		 * This method processes the message
		 * parameter:	input message
		 * return:		response message
		 */
		char option = message.charAt(0);

		switch (option) {
		case '1':
			/*
			 * Return list of all the topics
			 */
			return listAlltopics();
		case '2':
			/*
			 * Publish an event
			 */
			message = message.substring(1);
			String[] event = message.split(";");

			ip = event[0];
			String k = event[1];
			k = k.substring(1, k.length() - 1);
			keywords = k.split(",");
			topic_name = event[2];
			event_title = event[3];
			event_content = event[4];
	
			if (TopicKeywords.containsKey(topic_name)) {
				updateEventTitles(topic_name, event_title);
				updateEventContent(event_title, event_content);
				updateSubscriberList(topic_name, ip);
				updateTopicList(ip, topic_name);
				Set<String> sub = notifySubscribers(topic_name);
				for(String s: sub){
					if (s.equals(ip)){
						notification = "New event arrived for the topic you have subscribed\n" + "Event Title:" + event_title+
								'\n' + "Description:" + event_content + '\n' + "Topic:" + topic_name + '\n';
					}
				}
				msg = "Your event is published \n" + "c.	Continue\n" + "q.	Exit\n";
			} else {
				notification = "";
				msg = "Topic not found in system\n" + "c.	Continue\n" + "q.	Exit\n";
			}
	

			return msg;

		case '3':
			/*
			 * Advertise a topic
			 */
			message = message.substring(1);
			String[] advertise_topic = message.split(";");
			ip = advertise_topic[0];
			String new_word1 = advertise_topic[1];
			new_word1 = new_word1.substring(1, new_word1.length() - 1);
			keywords = new_word1.split(",");
			topic_name = advertise_topic[2];

			if (TopicKeywords.containsKey(topic_name)) {
				msg = "Topic already present in system, Please advertise new topic\n" + "c.	Continue\n" + "q.	Exit\n";
				notification = "";
			} else {
				updateTopicKeywords(topic_name, keywords);
				notification = "New Topic in the system :" + topic_name ;
				msg = "Thank you for advertising a topic \n" + "c.	Continue\n" + "q.	Exit\n";
				
			}
			return msg;

		case '4':
			/*
			 * Subscribe to a topic
			 */
			message = message.substring(1);
			String[] ip_topic = message.split(";");
			ip = ip_topic[0];
			topic_name = ip_topic[1];
			
			if (TopicKeywords.containsKey(topic_name)) {
				updateSubscriberList(topic_name, ip);
				updateTopicList(ip, topic_name);
				msg = "Thank you for subscribing to a topic \n" + "c.	Continue\n" + "q.	Exit\n";
			} else {
				msg = "Topic not found in system, Please subscribe to already existing topic\n" + "c.	Continue\n"
						+ "q.	Exit\n";
			}
			return msg;

		case '5':
			/*
			 * List all subscribed topics
			 */
			ip = message.substring(1);
			String allTopicList = "";

			if (topicList.containsKey(ip)) {

				topics = new HashSet<String>();
				topics = topicList.get(ip);

				if (topics.isEmpty()) {
					removeSubscriber(ip);
					allTopicList = "Sorry your are not subscribed to any topic !!!\n" + "c.	Continue\n" + "q.	Exit\n";
				} else {
					for (String t : topics) {
						allTopicList = allTopicList + t + '\n';
					}
					allTopicList = "Subscribed topics !\n" + allTopicList + '\n' + "c.	Continue\n" + "q.	Exit\n";
				}
			}
			else {				
				allTopicList = "Sorry your are not subscribed to any topic !!!\n" + "c.	Continue\n" + "q.	Exit\n";
			}
			return allTopicList;

		case '6':
			/*
			 * Remove subscriber for a specified topic
			 */
			message = message.substring(1);
			String[] ip_topic1 = message.split(";");
			ip = ip_topic1[0];
			topic_name = ip_topic1[1];

			removeSubscribertopic(ip, topic_name);

			return "You are no longer subscribed to above topic\n" + "c.	Continue\n" + "q.	Exit\n";

		case '7':
			/*
			 * Remove subscriber from the system
			 */
			ip = message.substring(1);
			msg = removeSubscriber(ip);
			return msg;

		case 'q':
			/*
			 * Client gets disconnected.
			 */
			return "Client disconnected!!!";
		default:
			/*
			 * If any other option is entered, return initial message to client.
			 */
			response =  initialMessage;
			return response;
		}
	}

	private Set<String> notifySubscribers(String topic_name2) {
		/*
		 * This method notifies all the subscribers who are subscribed to the topic.
		 * parameters:	a topic name.
		 * Return:		a set of subscribers
		 */
		if(SubscriberList.containsKey(topic_name2)){
			subscribers = new HashSet<String>();
			subscribers = SubscriberList.get(topic_name2);
			
			return subscribers;
		}
		return null;
		
	}

	private String removeSubscriber(String ip) {
		/*
		 * This method removes a subscriber from the system.
		 * parameters:	IP address of a subscriber.
		 * return:		success message
		 */
		if (topicList.containsKey(ip)) {
			topicList.remove(ip);
		}

		Iterator<Map.Entry<String, Set<String>>> itr = SubscriberList.entrySet().iterator();
		while (itr.hasNext()) {
			String t = itr.next().getKey();

			subscribers = new HashSet<String>();
			subscribers = SubscriberList.get(t);

			subscribers.remove(ip);
			SubscriberList.put(t, subscribers);

		}
		return "You are no longer subscriber\n" + "c.	Continue\n" + "q.	Exit\n";

	}

	private void updateTopicList(String ip2, String topic_name2) {
		/*
		 * This method updates topic list data structure.
		 * parameter:	topic name.
		 * return:		IP address of a subscriber.
		 */
		if (topicList.containsKey(ip2)) {
			topics = topicList.get(ip2);
			topics.add(topic_name2);
			topicList.put(ip2, topics);
		} else {
			topics = new HashSet<String>();
			topics.add(topic_name2);
			topicList.put(ip2, topics);
		}

	}

	private void updateSubscriberList(String topic_name2, String ip2) {
		/*
		 * This method updates subscriberList data structure.
		 * parameter:	topic name.
		 * return:		IP address of a subscriber.
		 */
		if (SubscriberList.containsKey(topic_name2)) {
			subscribers = SubscriberList.get(topic_name2);
			subscribers.add(ip2);
			SubscriberList.put(topic_name2, subscribers);
		} else {
			subscribers = new HashSet<String>();
			SubscriberList.put(topic_name2, subscribers);
		}

	}

	private void updateTopicKeywords(String topic_name2, String[] keywords2) {
		/*
		 * This method adds new topic and its keywords into topic keywords data structure.
		 * parameter:	topic name, list of keywords
		 * return:		None 
		 */
		TopicKeywords.put(topic_name2, keywords2);

	}

	private void updateEventContent(String event_title2, String event_content2) {
		/*
		 * This method adds new event into event content data structure.
		 * parameter:	event title and event content.
		 * return:		None
		 */
		EventContent.put(event_title2, event_content2);

	}

	private void updateEventTitles(String topic_name2, String event_title2) {
		/*
		 * This method updates event titles data structure.
		 * parameter:	topic name and event title
		 * return:		None
		 */
		if (EventTitles.containsKey(topic_name2)) {
			events = EventTitles.get(topic_name2);
			events.add(event_title2);
			EventTitles.put(topic_name2, events);
		} else {
			events = new ArrayList<String>();
			events.add(event_title2);
			EventTitles.put(topic_name2, events);
		}
	}

	private String listAlltopics() {
		/*
		 * This method lists all topics in the system.
		 * parameter:	None
		 * return:		List of all topics
		 */
		if (TopicKeywords.isEmpty()) {
			return "No Topics Found in the system!!!\n" + "c.	Continue\n" + "q.	Exit\n";
		} else {
			String allTopicList = "";

			for (Map.Entry<String, String[]> entry : TopicKeywords.entrySet()) {
				allTopicList = allTopicList + entry.getKey() + '\n';
			}
			return allTopicList + '\n' + "c.	Continue\n" + "q.	Exit\n";
		}
	}

	private byte[] receiveData(DataInputStream is) throws Exception {
		/*
		 * This method receives data from input stream and reads it.
		 * parameter:	Data input Stream 
		 * return:		Bytes of stream
		 */
		try {
			byte[] inputData = new byte[1024];
			is.read(inputData);
			return inputData;
		} catch (Exception exception) {
			throw exception;
		}
	}

	private void removeSubscribertopic(String ip, String topic_name) {
		/*
		 * This method removes subscriber for a specified topic.
		 * parameters:	IP address and topic name
		 * return:		None 
		 */
		if (SubscriberList.containsKey(topic_name)) {
			subscribers = new HashSet<String>();
			subscribers = SubscriberList.get(topic_name);
			subscribers.remove(ip);
			if (subscribers != null) {
				SubscriberList.put(topic_name, subscribers);
			} else {
				SubscriberList.remove(topic_name);
			}

		}
		if (topicList.containsKey(ip)) {
			topics = new HashSet<String>();
			topics = topicList.get(ip);

			topics.remove(topic_name);
			if (topics != null) {
				topicList.put(ip, topics);
			} else {
				topicList.remove(ip);
			}
		}

	}

}
