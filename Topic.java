/*
 * Description:		A class that creates a topic and display its contents
 * Authors:			Chaitali Kamble (csk3565)
 * Date:			03/12/2017
 */
import java.io.Serializable;
import java.util.List;
public class Topic implements Serializable{
	/*
	 * This class creates a topic and display its contents using toString method
	 */
	public List<String> keywords;
	public String name;
	
	public Topic(List<String> keywords, String name){
		/*
		 * This constructor defined for a topic which consists of its name and keywords.
		 */
		this.keywords = keywords;
		this.name = name;
		
	}
	@Override
	public String toString(){
		/*
		 * Overridden toString method to display contents of the topic.
		 */
		String topic_message = this.keywords + ";" + this.name;
		return topic_message;
		
	}
}
