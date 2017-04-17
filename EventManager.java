/*
 * Description:		This class is an event manager class that operates all the clients
 * Authors:			Chaitali Kamble (csk3565)
 * Date:			03/12/2017
 */
import java.net.*;

public class EventManager {
	/*
	 * This class is the main event manager which manages all the incoming and outgoing
	 * clients. It creates a thread per connection and calls the worker thread for processing
	 * the client task.
	 */

	public static void main(String[] args) throws Exception {
		/*
		 * Execution starts here.
		 */
		ServerSocket server = null;

		try {
			server = new ServerSocket(4000); // Open a 4000 port.
			System.out.println("Event manager is on !!!");
			while (true) {
				Socket socket = server.accept(); // Wait for connections.
				
				/*
				 * As soon as new client comes in, create a thread and call worker thread to
				 * perform specified task.
				 */
				EventManagerServer ems = new EventManagerServer(socket);
				ems.start();
			}

		} catch (Exception e) {
			server.close(); // In case of any technical issues, close the server connection.
		}
	}
}
