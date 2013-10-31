package com.example.shouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.LocationClient;


import com.google.android.gms.location.LocationListener;

import android.annotation.SuppressLint;
//import android.R;
//import android.R.id;
//import android.R.menu;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


public class MainActivity extends Activity{// implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int GPS_RESOLUTION = 1;
	private UserLocation userLocation;
	private static Location location;
	List<Shout> shoutList = new ArrayList<Shout>();
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		initList();
		ListView lv = (ListView) findViewById(R.id.ListView);
		ListAdapter adapter = new ListAdapter(this, shoutList, android.R.layout.simple_list_item_1, new String[]{"shout"}, new int[]{android.R.id.text1});
		
		lv.setAdapter(adapter);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		//myClient = new LocationClient(this, this, this);
		//myClient.connect();
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
		
		if(location != null)
			message = location.getLongitude()+", "+location.getLatitude();
		else
			message = "null";
		
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
		
		Shout myShout = new Shout(message, null);
		
		
		refresh(view); // automatically refresh after posting message
		
	}
	
	/* This function will pull shouts from the database and update displayed shouts
	 * 
	 */
	public void refresh(View view){
		
		updateLocation();
		
	}
	
	private void initList(){
		
		shoutList.add(new Shout("Test Shout 1",null));
		shoutList.add(new Shout("Just making sure this App is working",null));
		shoutList.add(new Shout("Woot Shouter",null));
		shoutList.add(new Shout("Still working",null));
		shoutList.add(new Shout("Test Shout 5",null));
		shoutList.add(new Shout("Test Shout 6",null));
		shoutList.add(new Shout("Test Shout 7",null));
		
	}

//Use hashmaps to populate list. Can be expanded on once shout structure has been defined.
	private HashMap<String, String> createShout(String name, String message){
		
		HashMap<String, String> shout = new HashMap<String, String>();
		shout.put(name, message);
		return shout;
		
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
				
			}});
		
		
	}

	
	
}
