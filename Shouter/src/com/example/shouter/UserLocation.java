package com.example.shouter;


import android.app.Activity;
import android.content.IntentSender;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

/**UserLocation
 * <p>
 * This class strives to make the request for a user's location easier.
 * 
 * @author Chris Allen
 */
public class UserLocation implements
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
        
        private Activity activity;
        private int GPS_RESOLUTION_FAILED;
        private LocationClient locClient;
        private LocationListener locListener;
        private LocationRequest locRequest;

        /**Creates a {@link UserLocation} object
         * 
         * @param a {@link Activity} that plans on calling {@code requestLocationUpdates()}
         * @param gpsResolutionFailed Integer used in {@code onActivityResult()} when
         *                            returning from location services settings
         */
        public UserLocation(Activity a, int gpsResolutionFailed) {
                activity = a;
                GPS_RESOLUTION_FAILED = gpsResolutionFailed;
                locClient = new LocationClient(a, this, this);
        }
        
        /**Starts polling for more and more accurate user locations
         * 
         * @param request Location request.  Use {@link UserLocation.defaultRequest} for the lazy. 
         * @param listener Implements callback for location updates
         */
        public void requestLocationUpdates(LocationRequest request, LocationListener listener) {
                locListener = listener;
                locRequest = request;
                locClient.connect();
        }
        
        public static LocationRequest defaultRequest() {
                LocationRequest request = LocationRequest.create();
                request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                request.setInterval(3 * 1000);
                request.setFastestInterval(1000);
                return request;
        }


        
        public void disconnect() {
                locClient.disconnect();
                locListener = null;
                locRequest = null;
        }

        @Override
        public void onConnected(Bundle connectionHint) {
//        Toast.makeText(activity, "Connected", Toast.LENGTH_SHORT).show();
        if(locRequest != null)
                locClient.requestLocationUpdates(locRequest, locListener);
        }

        @Override
        public void onDisconnected() {
//        Toast.makeText(activity, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
        
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                                activity,
                                GPS_RESOLUTION_FAILED);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
//            Toast.makeText(activity, "Connection failed", Toast.LENGTH_SHORT).show();
        }
        }
}
