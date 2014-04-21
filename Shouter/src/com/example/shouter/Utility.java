package com.example.shouter;

import java.util.HashMap;

import android.app.Activity;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;

/**
 * 
 * @author Josiah Class for accessing the API
 * 
 */
public class Utility {

	private static final int GPS_RESOLUTION = 1;
	private static UserLocation userLocation;
	private static Location location;
	
	public static Location updateLocation(Activity activity){
        
        userLocation = new UserLocation(activity, GPS_RESOLUTION);
        
        
        userLocation.requestLocationUpdates(userLocation.defaultRequest(), new LocationListener(){
                
                @Override
                public void onLocationChanged(Location loc) {

                        userLocation.disconnect();
                        
                        if (loc != null){
                                
                                location = loc;
                                
                        }
                        
                }

                });
        
        	return location;
	}
	
	public static HashMap<String, String> createShout(Shout shout) {

		//shouts.add(shout);
		HashMap<String, String> item = new HashMap<String, String>();
		item.put("message", shout.getMessage());
		//Toast.makeText(MainActivity.this,"TIme " + shout.getTimestamp(), Toast.LENGTH_LONG).show();
		item.put("username", shout.getUserName());
		item.put("timestamp", shout.getTime());
		//item.put("shout", shout.getMessage());
		//item.put("header", "Name: "+shout.getUser() + " - Time: " + shout.getTime());
		//item.put("header", "Name: "+ "Test User" + " - Time: " + shout.getTime());
		return item;

	}

	/*public static HashMap<String, String> createShout(String name, Shout shout) {

		//shouts.add(shout);
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(name, shout.toString());
		return item;

	}*/
	

}
