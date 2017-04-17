
/*
 * Description:		A publisher interface
 * Authors:			Chaitali Kamble (csk3565)
 * Date:			03/12/2017
 */
public interface Publisher {
	/*
	 * publish an event of a specific topic with title and content
	 */
	public void publish(Event event);
	
	/*
	 * advertise new topic
	 */
	public void advertise(Topic newTopic);
}
