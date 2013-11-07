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

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shouter.util.ShouterAPI;
import com.example.shouter.util.ShouterAPIDelegate;
import com.google.android.gms.location.LocationListener;


public class MainActivity extends Activity implements ShouterAPIDelegate{// implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int GPS_RESOLUTION = 1;
	private UserLocation userLocation;
	private static Location location;
	List<Map<String,String>> shoutList = new ArrayList<Map<String,String>>();
	private ShouterAPI api;
	/* Dialogs */
	public static final int DIALOG_LOADING = 0;
	private ListView lv;
	
	@Override
    protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = null;
        switch (id) {
        case DIALOG_LOADING:
        	dialog = new ProgressDialog(this);
        	((ProgressDialog) dialog).setMessage(getString(R.string.loading));        	
        	((ProgressDialog) dialog).setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	break;
        }
        return dialog;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		initList();
		
		lv = (ListView) findViewById(R.id.ListView);
		
		SimpleAdapter adapter = new SimpleAdapter(this, shoutList, android.R.layout.simple_list_item_1, new String[]{"shout"}, new int[]{android.R.id.text1});
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long id) {
				
				// We know the View is a TextView so we can cast it
				
				TextView clickedView = (TextView) view;
				Toast.makeText(MainActivity.this, "Item with id ["+id+"] - Position ["+position+"] - Shout ["+clickedView.getText()+"]", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(MainActivity.this, CommentActivity1.class);
				//EditText editText = (EditText) findViewById(R.id.edit_message);
				String message = (String) clickedView.getText();
				
				updateLocation();
				
				intent.putExtra(EXTRA_MESSAGE, message);
				startActivity(intent);
				
				
			}
			
		});
		
		api = new ShouterAPI();
        api.setDelegate(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	/*
	 *  This function will push the entered message into the database. 
	 *  The message will also be added to the displayed shouts
	 */
	public void postMessage(View view){
		// Do something in response to button
		
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		
		updateLocation();
		
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
		
		Shout myShout = new Shout(message, location);
		
		
		try {
			
			if(location == null){
				myShout.setLatitude(50.0);
				myShout.setLongitude(50.0);
				myShout.setID("999999");
				myShout.setParent("999998");
			}
			
			showDialog(DIALOG_LOADING);
			api.postShout(myShout);
			
		} catch (JsonGenerationException e) {e.printStackTrace();} catch (JsonMappingException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
		
		refresh(view); // automatically refresh after posting message
		
	}
	
	
	/* This function will pull shouts from the database and update displayed shouts
	 * 
	 */
	public void refresh(View view){
		
		updateLocation();
		
		List<Shout> newShouts = new ArrayList<Shout>();
		
		String lat = ""; 
		String lon = ""; 
		
		if(location != null){
			
			lat += location.getLatitude();
			lon += location.getLongitude();
			
		}
		
		showDialog(DIALOG_LOADING);
		newShouts = api.getShout(lat, lon);
		
		
		for(Shout s : newShouts){
			
			shoutList.add(createShout("shout", s));
			
		}
		
	}
	
	private void initList(){
		
		shoutList.add(createShout("shout", new Shout("Test Shout 1",null)));
		shoutList.add(createShout("shout", new Shout("Just making sure this App is working",null)));
		shoutList.add(createShout("shout", new Shout("Woot Shouter",null)));
		shoutList.add(createShout("shout", new Shout("Still working",null)));
		shoutList.add(createShout("shout", new Shout("Test Shout 5",null)));
		shoutList.add(createShout("shout", new Shout("Test Shout 6",null)));
		shoutList.add(createShout("shout", new Shout("Test Shout 7",null)));
		
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
     * We're not guaranteed that this is called on the UI thread.  Any changes to the
     * UI will need to be done using runOnUiThread()
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
					Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				else{
					List<Shout> shoutList = new ArrayList();
					ObjectMapper mapper = new ObjectMapper();
					try{
						shoutList = mapper.readValue(result, new TypeReference<List<Shout>>(){});
					}catch(Exception e1){
						e1.printStackTrace();}
					Collections.reverse(shoutList);
					Toast.makeText(MainActivity.this, "Get Successful!!!", Toast.LENGTH_LONG);
			}
		}});
		return shoutList;
	}
    
	public void onPostShoutReturn(ShouterAPI api, final String result, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
				else{
					Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
				//Post Shout Success Logic
				//Return logic not that important, if not error should return shout
				}
			}
		});
	}
	
	public List<Shout> onGetCommentReturn(ShouterAPI api, final String result, final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(MainActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else{
					Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
				//Get Comment Success Logic
				//Same as get shout 
				}
			}
		});
		return shoutList;
	}
	
	public void onPostCommentReturn(ShouterAPI api, final String result, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(MainActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else{
					Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
				//Post Comment Success Logic
				//same as post shout
				}
			}
		});
	}

	
	
}
