package com.example.shouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.location.Location;

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

	public static HashMap<String, String> createShout(String name, Shout shout) {

		//shouts.add(shout);
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(name, shout.toString());
		return item;

	}
	

}
