
package com.example.shouter.util;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.androidtools.Networking;
import com.example.shouter.Shout;



public class ShouterAPI {

	private static final String Shouter_URL = "http://shouterapi-env.elasticbeanstalk.com";
	
	private ExecutorService executor;
	private ShouterAPIDelegate delegate;
	private RestTemplate REST = Networking.defaultRest();

	private String shoutString;

	private String path;
	
	public ShouterAPI() {
		executor = Executors.newFixedThreadPool(5);
	}
	
	public void setDelegate(ShouterAPIDelegate d){
		delegate = d;
	}
	
	public void register(String phoneID, String userName){
		
	}
	
	public void postShout(Shout message, String location, String phoneId) throws JsonGenerationException, JsonMappingException, IOException{
		path = "/api/shout/create";
		ObjectMapper mapper = new ObjectMapper();
		shoutString = mapper.writeValueAsString(message);
		executor.submit(new Runnable(){
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try{
					//TODO: Need to tweak to suit our API better
					String url = Shouter_URL + path + "?location=" + location;
					//Creates Entity with shout as body
					HttpEntity<String> request = new HttpEntity<String>(shoutString);
					//Post Entity to URL
					response = REST.exchange(url, HttpMethod.POST, request, String.class);
					delegate.onPostShoutReturn(ShouterAPI.this, response.getBody(), null);
					
				}catch(Exception e){
					e.printStackTrace();
					delegate.onPostShoutReturn(ShouterAPI.this, null, e);
				}
				
			}
		});
	}
	
	public void getShout(final String Location){
			path = "/api/shout/search";
			
			executor.submit(new Runnable() {
				@Override
				public void run() {
					ResponseEntity<String> response = null;
					try {
						HttpEntity<String> request = new HttpEntity<String>();
						
						String url = Shouter_URL + path + "?latitute=" + latitude;
						response = REST.exchange(url, HttpMethod.GET, request, String.class);
						delegate.onGetShoutReturn(ShouterAPI.this, response.getBody(), null);
					} catch(Exception e) {
						e.printStackTrace();
						delegate.onGetShoutReturn(ShouterAPI.this, null, e);
					}
				}
			});
	}
		
	
	
	public void postComment(String parentId, Shout message){

	}
	
	public void getComment(final String parentId){
		path = "/api/shout/comment/search";
		
		executor.submit(new Runnable() {
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try {
					HttpEntity<String]> request = new HttpEntity<String>();
					
					String url = Shouter_URL + path + "?parentId=" + parentId;
					response = REST.exchange(url, HttpMethod.GET, request, String.class);
					delegate.onGetCommentReturn(ShouterAPI.this, response.getBody(), null);
				} catch(Exception e) {
					e.printStackTrace();
					delegate.onGetCommentReturn(ShouterAPI.this, null, e);
				}
			}
		});			
	}
	
}

