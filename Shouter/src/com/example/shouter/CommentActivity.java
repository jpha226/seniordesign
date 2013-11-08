package com.example.shouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.example.shouter.util.ShouterAPI;
import com.example.shouter.util.ShouterAPIDelegate;
import com.google.android.gms.location.LocationListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity implements ShouterAPIDelegate{

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
	private static final int GPS_RESOLUTION = 1;
	private UserLocation userLocation;
	private static Location location;
	List<Map<String,String>> commentList = new ArrayList<Map<String,String>>();
	private ShouterAPI api;
	/* Dialogs */
	public static final int DIALOG_LOADING = 0;
	private ListView lv;
	private 	String android_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		
		initList();
		updateLocation();
		
		lv = (ListView) findViewById(R.id.ListView);
		
		SimpleAdapter adapter = new SimpleAdapter(this, commentList, android.R.layout.simple_list_item_1, new String[]{"shout"}, new int[]{android.R.id.text1});
		lv.setAdapter(adapter);
		
		
		// Show the Up button in the action bar.
		setupActionBar(); // Josiah was here
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.comment, menu);
		
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

		android_id = intent.getStringExtra(MainActivity.EXTRA_ID);
		
		// Create text view
		TextView textView = new TextView(this);
		textView.setTextSize(40);
		textView.setText(message);

		setContentView(textView);
		
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	/*
	 *  This function will push the entered message into the database. 
	 *  The message will also be added to the displayed shouts
	 */
	public void postComment(View view){
		// Do something in response to button
		
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		
		updateLocation();
		
		Shout myShout = new Shout(message, location);
		String id = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID); 
		myShout.setID(id);
		myShout.setParent(android_id);
		String lon = myShout.getLongitude();
		String lat = myShout.getLatitude();
		Toast.makeText(this, "Longitude: " + lon + " Latitude: " + lat, Toast.LENGTH_LONG).show();
		try {
			
			//if(location == null){
			//	myShout.setLatitude(50.0);
			//	myShout.setLongitude(50.0);
				//myShout.setID("999999");
				myShout.setParent("999998");
			//}
			
			showDialog(DIALOG_LOADING);
			api.postComment(myShout);
			
		} catch (JsonGenerationException e) {e.printStackTrace();} catch (JsonMappingException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		
		refresh(view); // automatically refresh after posting message
		
	}
	
	
	/* This function will pull shouts from the database and update displayed shouts
	 * 
	 */
	public void refresh(View view){
		
		updateLocation();
		
		List<Shout> newShouts = new ArrayList<Shout>();
		
		showDialog(DIALOG_LOADING);
		newShouts = api.getComment(android_id);

		for(Shout s : newShouts){
			
			commentList.add(createShout("shout", s));
			
		}
		
	}
	
	private void initList(){
		
		List<Shout> comments = api.getComment(android_id);
		
		for(Shout s: comments){
			
			commentList.add(createShout("shout", s));
			
		}
		
	}

//Use hashmaps to populate list. Can be expanded on once shout structure has been defined.
	private HashMap<String, String> createShout(String name, Shout shout){
		
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(name, shout.toString());
		return item;
		
	}

	private void updateLocation(){
		
		userLocation = new UserLocation(this, GPS_RESOLUTION);
		
		
		userLocation.requestLocationUpdates(userLocation.defaultRequest(), new LocationListener(){
			
			@Override
			public void onLocationChanged(Location loc) {
	
				userLocation.disconnect();
				
				if (loc != null){
					
					location = loc;
					
				}
				
			}

			});
		
		
	}

	
	/**
	 * @author Craig
	 * @param api - API wrapper used to make certain call to api
	 * @param result - Return value from http call to api if success
	 * @param e -  Return value from http call to api if failure
	 * @return List of shouts from call to api for GetShout
	 * Return logic for a call to the API for getShout
	 */
	@Override
	public List<Shout> onGetShoutReturn(ShouterAPI api, final String result, final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(CommentActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				else{
					List<Shout> shoutList = new ArrayList();
					ObjectMapper mapper = new ObjectMapper();
					try{
						shoutList = mapper.readValue(result, new TypeReference<List<Shout>>(){});
					}catch(Exception e1){
						e1.printStackTrace();}
					Collections.reverse(shoutList);
					Toast.makeText(CommentActivity.this, "GET" + result, Toast.LENGTH_LONG).show();
			}
		}});
		return shoutList;
	}
    
	/**
	 * @author Craig
	 * @param api - API wrapper used to make certain call to api
	 * @param result - Return value from http call to api if success
	 * @param e -  Return value from http call to api if failure
	 * Return logic for a call to the API for postShout
	 */
	public void onPostShoutReturn(ShouterAPI api, final String result, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(CommentActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				else{
					Toast.makeText(CommentActivity.this, "POST" + result, Toast.LENGTH_LONG).show();
					
				//Post Shout Success Logic
				//Return logic not that important, if not error should return shout
				}
			}
		});
	}
	
	/**
	 * @author Craig
	 * @param api - API wrapper used to make certain call to api
	 * @param result - Return value from http call to api if success
	 * @param e -  Return value from http call to api if failure
	 * @return List of shouts from call to api for getComment
	 * Return logic for a call to the API for getComment
	 */
	public List<Shout> onGetCommentReturn(ShouterAPI api, final String result, final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(CommentActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else{
					Toast.makeText(CommentActivity.this, result, Toast.LENGTH_LONG).show();
				//Get Comment Success Logic
				//Same as get shout 
				}
			}
		});
		return shoutList;
	}
	
	/**
	 * @author Craig
	 * @param api - API wrapper used to make certain call to api
	 * @param result - Return value from http call to api if success
	 * @param e -  Return value from http call to api if failure
	 * Return logic for a call to the API for postComment
	 */
	public void onPostCommentReturn(ShouterAPI api, final String result, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(CommentActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else{
					Toast.makeText(CommentActivity.this, result, Toast.LENGTH_LONG).show();
				//Post Comment Success Logic
				//same as post shout
				}
			}
		});
	}
}
