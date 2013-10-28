package com.example.shouter.util;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.ResponseEntity;

import com.example.shouter.Shout;

public class ShouterAPI {

	private static final String Shouter_URL = "INSERT URL";
	
	private ExecutorService executor;
	private ShouterAPIDelegate delegate;

	private String shoutString;

	private String path;
	
	public ShouterAPI() {
		executor = Executors.newFixedThreadPool(5);
	}
	
	public void setDelegate(ShouterAPIDelegate d){
		delegate = d;
	}
	
	public void postShout(Shout message) throws JsonGenerationException, JsonMappingException, IOException{
		path = "/api/shout/create";
		ObjectMapper mapper = new ObjectMapper();
		shoutString = mapper.writeValueAsString(message);
		executor.submit(new Runnable(){
			@Override
			public void run() {
				ResponseEntity<String> response = null;
				try{
					
					String url = Shouter_URL + path;
					//HTTP POST
					delegate.onShouterReturn(ShouterAPI.this, response.getBody(), null);
					
				}catch(Exception e){
					e.printStackTrace();
					delegate.onShouterReturn(ShouterAPI.this, null, e);
				}
				
			}
		});
	}
	
	public void getShout(final String Location){
		
	}
	
	public void getReply(final String ParentId){
		
	}
	
}
