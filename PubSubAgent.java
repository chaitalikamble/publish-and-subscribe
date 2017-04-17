/*
 * Description:		Pub Sub Agent - a client class
 * Authors:			Chaitali Kamble (csk3565)
 * Date:			03/12/2017
 */
import java.io.*;
import java.net.*;
import java.util.*;

public class PubSubAgent implements Publisher, Subscriber {
	/*
	 * This is a client class which takes input from user and sends it to event manager and gets response
	 * appropriate event manager.
	 */
	
	/*
	 * ************ Declare Variables **********************
	 */
	public static Socket skt;
	public static Scanner sc;
	public static Socket socket;

	static PubSubAgent pb = new PubSubAgent();
	static String responseData;
	public static InetAddress ip;

	@Override
	public void subscribe(String topic) {
		/*
		 * This method concatenates IP address and topic name.
		 */
		responseData = "4" + ip + ";" + topic.toString();
		return;

	}

	@Override
	public void unsubscribe(String topic) {
		/*
		 * This method concatenates IP address and topic name.
		 */
		responseData = "6" + ip + ";" + topic;
		return;

	}

	@Override
	public void unsubscribe() {
		/*
		 * This method sends IP address
		 */
		responseData = "7" + ip;
		return;
	}

	@Override
	public void listSubscribedTopics() {
		/*
		 * This method sends IP address.
		 */
		responseData = "5" + ip;
		return;

	}

	@Override
	public void publish(Event event) {
		/*
		 * This method concatenates ip address and event.
		 */
		responseData = "2" + ip + ";" + event.toString();
		return;

	}

	@Override
	public void advertise(Topic newTopic) {
		/*
		 * This method concatenates ip address and topic.
		 */
		responseData = "3" + ip + ";" + newTopic.toString();
		return;

	}

	public static void main(String args[]) throws UnknownHostException, IOException {
		/*
		 * Eecution starts here.
		 */
		ip = (InetAddress.getLocalHost());  // Get IP address
		System.out.println("Client Started...");
		socket = new Socket( "129.21.94.144" , 4000); // Port number of a server and its IP address
		DataInputStream is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

		try {
			while (true) {
				// Get response from server
				byte[] byteData = receive(is);
				String serverMessage = new String(byteData).trim();
				//Print server message.
				System.out.println(serverMessage);
				sc = new Scanner(System.in);
				responseData = sc.nextLine();
				// Process client response
				doProcess(responseData);
				// Send client response
				send(os, responseData.getBytes());
			}
		} catch (Exception e) {
			//If any exception occurs, disconnect the client.
			System.out.println("Disconnected!!!");
		}
	}

	private static void doProcess(String message) throws IOException {
		/*
		 * This method gets client inputs and processes it.
		 */
		char option = message.charAt(0);
		switch (option) {
		case '1':
			/*
			 * Request to list all topics
			 */
			responseData = "1";
			break;
		case '2':
			/*
			 * Request to publish event
			 */
			Topic topic = createTopic();
			Event event = createEvent(topic);
			pb.publish(event);
			break;
		case '3':
			/*
			 * Advertise a new topic
			 */
			topic = createTopic();
			pb.advertise(topic);
			break;
		case '4':
			/*
			 * Subscribe to a specific topic
			 */
			System.out.println("For which of the listed topics you want to subsrcibe?");
			Scanner s = new Scanner(System.in);
			String sub_topic = s.nextLine();
			pb.subscribe(sub_topic);
			break;
		case '5':
			/*
			 * List all subscribed topics
			 */
			pb.listSubscribedTopics();
			break;
		case '6':
			/*
			 * Unsubscribe to a specified topic
			 */
			System.out.println("For which of the listed topic you want to unsubscribe?");
			Scanner s1 = new Scanner(System.in);
			String sub_topic1 = s1.nextLine();
			pb.unsubscribe(sub_topic1);
			break;
		case '7':
			/*
			 * Unsubscribe from the system.
			 */
			pb.unsubscribe();
			break;
		case 'q':
			/*
			 * Close the connection.
			 */
			socket.close();
			break;
		case 'c':
			/*
			 * Continue processing.
			 */
			break;
		default:
			/*
			 * Show error message as invalid entry
			 */
			System.out.println("Invalid entry !!!");
			break;

		}
	}

	private static Event createEvent(Topic topic) {
		/*
		 * This method creates an event string of a specified topic.
		 * parameter:	Topic 
		 * return:		None
		 */
		System.out.println("Enter the title of the event\n");
		String title = sc.nextLine();
		System.out.println("Enter the content you want to publish\n");
		String content = sc.nextLine();
		Event e = new Event(topic, title, content);
		return e;
	}

	private static Topic createTopic() {
		/*
		 * This method creates a topic string.
		 * parameter:	None
		 * return:		None
		 */
		System.out.println("Enter keywords for the topic and then hit 'q' to finish entering keywords\n");
		List<String> keywords = new ArrayList<>();
		sc = new Scanner(System.in);
		while (true) {
			String s = sc.nextLine();
			if (s.equals("q")) {
				break;
			}
			keywords.add(s);
		}

		System.out.println("Enter the name of the topic\n");
		String name = sc.nextLine();
		Topic t = new Topic(keywords, name);
		return t;
	}

	private static byte[] receive(DataInputStream is) throws Exception {
		/*
		 * This method receives data from server and reads it and gives an array of bytes.
		 * parameter:	Data input Stream 
		 * return:		Array of bytes
		 */
		try {
			byte[] inputData = new byte[1024];
			is.read(inputData);
			return inputData;
		} catch (Exception exception) {
			throw exception;
		}
	}

	private static void send(DataOutputStream os, byte[] byteData) throws Exception {
		/*
		 * This method writes byte data to output stream.
		 * parameters: 	Data output stream and byte data array
		 * return:		None
		 */
		try {
			os.write(byteData);
			os.flush();
		} catch (Exception exception) {
			throw exception;
		}

	}

}
