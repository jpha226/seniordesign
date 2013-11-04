
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

import com.androidtools.Networking;
import com.example.shouter.Shout;


public class ShouterAPI {

	private static final String Shouter_URL = "http://shouterapi-env.elasticbeanstalk.com";
	
	private ExecutorService executor;
	private ShouterAPIDelegate delegate;
	private RestTemplate REST = Networking.defaultRest();
	private String path;
	List<Shout> shoutList = new ArrayList<Shout>();
	
	public ShouterAPI() {
		executor = Executors.newFixedThreadPool(5);
	}
	
	public void setDelegate(ShouterAPIDelegate d){
		delegate = d;
	}
	
	public void register(String phoneID, String userName){
		
	}
	
	public void postShout(final Shout message) throws JsonGenerationException, JsonMappingException, IOException{
		path = "/api/shout/create";
		
		executor.submit(new Runnable(){
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try{
					HttpHeaders headers = new HttpHeaders();
					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?phoneId=" + message.getID() + "&message=" + message.getMessage() + "&latitude=" + message.getLatitude() + "&longitude=" + message.getLongitude() + "&parentId=" + message.getParent(); 
					response = REST.exchange(url, HttpMethod.POST, request, String.class);
					delegate.onPostShoutReturn(ShouterAPI.this, response.getBody(), null);
					
				}catch(Exception e){
					e.printStackTrace();
					delegate.onPostShoutReturn(ShouterAPI.this, null, e);
				}
				
			}
		});
	}
	
	public List<Shout> getShout(final String latitude, final String longitude){
			path = "/api/shout/search";
			executor.submit(new Runnable() { 
				@Override
				public void run() {
					ResponseEntity<String> response = null;
					try {
						HttpHeaders headers = new HttpHeaders();
						HttpEntity<String> request = new HttpEntity<String>(headers);
						String url = Shouter_URL + path + "?latitute=" + latitude + "&longitude=" + longitude;
						response = REST.exchange(url, HttpMethod.GET, request, String.class);
						shoutList = delegate.onGetShoutReturn(ShouterAPI.this, response.getBody(), null);
					} catch(Exception e) {
						e.printStackTrace();
						delegate.onGetShoutReturn(ShouterAPI.this, null, e);
					}
				}
			});
			return shoutList;
	}
		
	public void postCOmment(final Shout message) throws JsonGenerationException, JsonMappingException, IOException{
		path = "/api/shout/comment/create";
		executor.submit(new Runnable(){
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try{
					HttpHeaders headers = new HttpHeaders();
					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?phoneId=" + message.getID() + "&message=" + message.getMessage() + "&latitude=" + message.getLatitude() + "&longitude=" + message.getLongitude() + "&parentId=" + message.getParent(); 
					response = REST.exchange(url, HttpMethod.POST, request, String.class);
					delegate.onPostShoutReturn(ShouterAPI.this, response.getBody(), null);
					
				}catch(Exception e){
					e.printStackTrace();
					delegate.onPostShoutReturn(ShouterAPI.this, null, e);
				}
				
			}
		});
	}
	
	
	public void postComment(final Shout message){
		path = "/api/shout/comment/create";
		
		executor.submit(new Runnable(){
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try{
					HttpHeaders headers = new HttpHeaders();
					HttpEntity<String> request = new HttpEntity<String>(headers);
					String url = Shouter_URL + path + "?phoneId=" + message.getID() + "&message=" + message.getMessage() + "&latitude=" + message.getLatitude() + "&longitude=" + message.getLongitude() + "&parentId=" + message.getParent(); 
					response = REST.exchange(url, HttpMethod.POST, request, String.class);
					delegate.onPostShoutReturn(ShouterAPI.this, response.getBody(), null);
					
				}catch(Exception e){
					e.printStackTrace();
					delegate.onPostShoutReturn(ShouterAPI.this, null, e);
				}
				
			}
		});
	}
}

