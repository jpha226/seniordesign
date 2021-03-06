package com.example.shouter.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.widget.Toast;

import com.example.shouter.MainActivity;
import com.example.shouter.Shout;

/**
 * 
 * @author Craig Wrapper for the API that accesses Shouter
 * 
 */
public class ShouterAPI {

	private static final String Shouter_URL = "http://shouterapi-env.elasticbeanstalk.com/shouter";

	private ExecutorService executor;
	private ShouterAPIDelegate delegate;
	private RestTemplate REST = com.androidtools.Networking.defaultRest();

	private String path;

	List<Shout> shoutList = new ArrayList<Shout>();

	/**
	 * @author Craig Constructor that creates threads
	 */
	public ShouterAPI() {
		executor = Executors.newFixedThreadPool(5);
	}

	/**
	 * @author Craig
	 * @param delegate
	 *            - The delegate from ShouterAPIDeleagate
	 */
	public void setDelegate(ShouterAPIDelegate d) {
		delegate = d;
	}

	/**
	 * @author Craig
	 * @param phoneID
	 *            - Unique ID for the users phone
	 * @param userName
	 *            - User defined userName to be linked with their ID Not
	 *            implemented yet, to come with phase 2
	 */
	public void register(final String phoneID, final String first, final String last, final String regID) {

		path = "/api/user/create";

		executor.submit(new Runnable() {
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try {
					HttpHeaders headers = new HttpHeaders();

					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?phoneId="
							+ phoneID + "&firstName="
							+ first + "&lastName="
							+ last + "&registrationId="
							+ regID;
					// Post Entity to URL
					response = REST.exchange(url, HttpMethod.POST, request,
							String.class);
					delegate.onRegistrationReturn(ShouterAPI.this,
							response.getBody(), null);
				} catch (Exception e) {
					e.printStackTrace();
					delegate.onRegistrationReturn(ShouterAPI.this,
							null, e);
				}
				
			}
		});
		
	}

	/**
	 * @author Craig
	 * @param message
	 *            - Shout to be sent to the API Converts shout to HTTP call and
	 *            calls Shouter API
	 */
	public void postShout(final Shout message) throws JsonGenerationException,
			JsonMappingException, IOException {

		path = "/api/shout/create";

		executor.submit(new Runnable() {
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try {
					HttpHeaders headers = new HttpHeaders();

					HttpEntity<String> request = new HttpEntity<String>(headers);
					// TODO: Need to seperate Location into long and lat
					String url = Shouter_URL + path + "?phoneId="
							+ message.getID() + "&message="
							+ message.getMessage() + "&latitude="
							+ message.getLatitude() + "&longitude="
							+ message.getLongitude() + "&parentId="
							+ message.getParent();
					// Post Entity to URL
					response = REST.exchange(url, HttpMethod.POST, request,
							String.class);
					delegate.onPostShoutReturn(ShouterAPI.this,
							response.getBody(), null);

				} catch (Exception e) {
					e.printStackTrace();
					delegate.onPostShoutReturn(ShouterAPI.this, null, e);
				}

			}
		});
	}

	/**
	 * @author Craig
	 * @param latitude
	 *            - String of devices latitude
	 * @param longitude
	 *            - String of devices longitude
	 * @return list of shouts that a within geographical boundary of current
	 *         location Function gets a list of shouts based on current location
	 *         from the API
	 */
	public List<Shout> getShout(final String latitude, final String longitude) {
		path = "/api/shout/search";
		executor.submit(new Runnable() {
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try {

					HttpHeaders headers = new HttpHeaders();
					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?latitude=" + latitude
							+ "&longitude=" + longitude;
					response = REST.exchange(url, HttpMethod.GET, request,
							String.class);
					shoutList = delegate.onGetShoutReturn(ShouterAPI.this,
							response.getBody(), null);
					
				} catch (Exception e) {
					e.printStackTrace();
					delegate.onGetShoutReturn(ShouterAPI.this, null, e);
				}
			}
		});

		return shoutList;
	}

	/**
	 * @author Craig
	 * @param message
	 *            - Shout that is to be connected with parent shout Function
	 *            pushes shout back to API to store in database
	 */
	public void postComment(final Shout message)
			throws JsonGenerationException, JsonMappingException, IOException {
		path = "/api/shout/comment/create";
		executor.submit(new Runnable() {
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try {
					HttpHeaders headers = new HttpHeaders();
					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?phoneId="
							+ message.getUser() + "&message="
							+ message.getMessage() + "&latitude="
							+ message.getLatitude() + "&longitude="
							+ message.getLongitude() + "&parentId="
							+ message.getParent();
					response = REST.exchange(url, HttpMethod.POST, request,
							String.class);
					delegate.onPostCommentReturn(ShouterAPI.this,
							response.getBody(), null);

				} catch (Exception e) {
					e.printStackTrace();
					delegate.onPostCommentReturn(ShouterAPI.this, null, e);
				}

			}
		});
	}

	/**
	 * @author Craig
	 * @param parentId
	 * @return List of shouts associated with parentId shout Function returns a
	 *         list of shouts connected to the input parentId by querying the
	 *         database through the API
	 */
	public List<Shout> getComment(final String parentId) {
		path = "/api/shout/comment/search";
		executor.submit(new Runnable() {
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try {

					HttpHeaders headers = new HttpHeaders();
					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?parentId=" + parentId;
					response = REST.exchange(url, HttpMethod.GET, request,
							String.class);
					shoutList = delegate.onGetCommentReturn(ShouterAPI.this,
							response.getBody(), null);
				} catch (Exception e) {
					e.printStackTrace();
					delegate.onGetCommentReturn(ShouterAPI.this, null, e);
				}
			}
		});

		return shoutList;
	}

	public void setShoutList(List<Shout> sl) {
		shoutList = sl;
	}

	public List<Shout> getShoutList() {
		return shoutList;
	}
}

