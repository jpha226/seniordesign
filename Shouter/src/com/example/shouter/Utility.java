package com.example.shouter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Josiah Class for accessing the API
 * 
 */
public class Utility {

	/**
	 * @author Josiah
	 * @param shout
	 *            - The shout to be converted to JSON and pushed to the database
	 *            Function pushes shout to database
	 * @return
	 */
	public static void post(Shout shout) {
	}

	/**
	 * Function for accessing the API to refresh the posts the user sees
	 * 
	 * @return - A list of shouts that the user has not seen yet
	 */
	public static List<Shout> pull() {

		Shout s = new Shout("This is a shout", null);
		Shout a = new Shout("This is shout a!", null);

		List<Shout> result = new ArrayList<Shout>();

		result.add(s);
		result.add(a);
		return result;

	}

	public static List<Shout> getChildren(String parentID) {
		return null;
	}

}
