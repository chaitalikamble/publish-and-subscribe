/*
 * Description:		Subscriber interface
 * Authors:			Chaitali Kamble (csk3565)
 * Date:			03/12/2017
 */
 public interface Subscriber {
	/*
	 * subscribe to a topic
	 */
	public void subscribe(String topic);
	
	/*
	 * subscribe to a topic with matching keywords
	 */
	
	public void unsubscribe(String topic);
	
	/*
	 * unsubscribe to all subscribed topics
	 */
	public void unsubscribe();
	
	/*
	 * show the list of topics current subscribed to 
	 */
	public void listSubscribedTopics();
	
}
