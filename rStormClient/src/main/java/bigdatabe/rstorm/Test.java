package bigdatabe.rstorm;

import org.mcbrooks.twitter.Status;
import org.mcbrooks.twitter.User;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Status status = new Status();
		
		status.setText("I believe $AAPL rocks!");
		status.setId(98798l);
		
		User user = new User();
		user.setId(9879l);
		user.setName("steevo");
		status.setUser(user);
		
		System.out.println(status);
		
		String sentiment = new RClient().sendTweet(status);
		System.out.println(sentiment);
		
	}

}
