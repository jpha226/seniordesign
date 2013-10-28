package com.example.shouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesClient;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.location.LocationClient;

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


public class MainActivity extends Activity {// implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	//private LocationClient myClient;
	
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
		
		//Location loc = myClient.getLastLocation();
		
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
		
		Shout myShout = new Shout(message, null);
		
		
		refresh(view); // automatically refresh after posting message
		
	}
	
	/* This function will pull shouts from the database and update displayed shouts
	 * 
	 */
	public void refresh(View view){
		
		
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
	
	/*/ Define a DialogFragment that displays the error dialog
    @SuppressLint("NewApi")
	public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        @SuppressLint("NewApi")
		public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
    
    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {
    
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
               /* switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */
                 /*   
                    break;
                }
            
        }
     }
    
  
	private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            // Continue
            return true;
        // Google Play services was not available for some reason
        } else {
            // Get the error code
            int errorCode = 0;//ConnectionResult.getErrorCode();
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getFragmentManager(), "Location Updates");
            }
            return false;
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
		 try {
			throw new Exception("Connection failed");
		} catch (Exception e) {e.printStackTrace();}
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		
		Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
		
	}

	@Override
	public void onDisconnected() {
		 Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
		 
		
	}
	
*/}
