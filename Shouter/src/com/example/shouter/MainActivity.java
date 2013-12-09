package com.example.shouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


















//import com.androidtools.Networking;
import com.example.shouter.util.ShouterAPI;
import com.example.shouter.util.ShouterAPIDelegate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.*;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements ShouterAPIDelegate {// implements
																			// GooglePlayServicesClient.ConnectionCallbacks,
																			// GooglePlayServicesClient.OnConnectionFailedListener{

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE"; // message
	public final static String EXTRA_ID = "com.example.shouter.ID"; // id of
	public final static String EXTRA_INT = "com.example.shout.INT";
																	
	private static final int GPS_RESOLUTION = 1;
	private UserLocation userLocation; // For finding current location
	private static Location location; // current location of user
	private List<Map<String, String>> shoutMap = new ArrayList<Map<String, String>>(); // Maintains																					// shout
																						// messages
	private List<Shout> shouts = new ArrayList<Shout>(); // Maintains the actual
															// shouts in the
															// list
	private List<Shout> innerShoutList = new ArrayList<Shout>();
	private ShouterAPI api; // API to call
	private RestTemplate REST =com.androidtools.Networking.defaultRest();
	private static final String Shouter_URL = "http://shouterapi-env.elasticbeanstalk.com/shouter";

	/* Dialogs */
	public static final int DIALOG_LOADING = 0;
	
	
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private ListView lv; // The list

	/**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    String SENDER_ID = "668201981322";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMShout";
	private static final int POST_REQUEST = 0;

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;

    String regid;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = null;
		switch (id) {
		case DIALOG_LOADING:
			dialog = new ProgressDialog(this);
			((ProgressDialog) dialog).setMessage(getString(R.string.loading));
			((ProgressDialog) dialog)
					.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			break;
		}
		return dialog;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		View view = findViewById(R.id.refresh);
		
		context = getApplicationContext();
		
		gcm = GoogleCloudMessaging.getInstance(this);
        
		regid = getRegistrationId(context);

        if (regid.isEmpty()) {
            registerInBackground();
      
    	} else {
    		Log.i(TAG, "No valid Google Play Services APK found.");
    	}
		
		
		//updateLocation();
		refresh(view);
		lv = (ListView) findViewById(R.id.listView);

		SimpleAdapter adapter = new SimpleAdapter(this, shoutMap,
				android.R.layout.simple_list_item_1, new String[] { "shout" },
				new int[] { android.R.id.text1 });
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view,
					int position, long id) {

				TextView clickedView = (TextView) view;

				Intent intent = new Intent(MainActivity.this,
						CommentActivity.class);
				String message = (String) clickedView.getText();

				intent.putExtra(EXTRA_MESSAGE, message);
				intent.putExtra(EXTRA_ID, shouts.get(position).getID());

				startActivity(intent);

			}

		});

	}

	protected void onResume() {
	    super.onResume();
	    checkPlayServices();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	/**
	 * This function will push the entered message into the database. The
	 * message will also be added to the displayed shouts
	 * 
	 * @author Josiah
	 * @param view
	 *            The current view that function is called from
	 */
	public void postShout(View view) {

		// Do something in response to button
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		editText.setText("");

        Intent intent = new Intent(MainActivity.this, PostActivity.class);
        intent.putExtra(EXTRA_INT, PostActivity.MAIN_ACTIVITY);
        startActivityForResult(intent, POST_REQUEST);
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 	    
     	
	        if (resultCode == RESULT_OK) {
	        	
	        	String message = "not found";
	        	message = data.getStringExtra(PostActivity.EXTRA_MESSAGE);

	        	Location loc = Utility.updateLocation(this);

	    		Shout myShout = new Shout(message, loc);
	            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

	    		String android_id = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID);
	    		myShout.setID(android_id);

	    		String lon = myShout.getLongitude();
	    		String lat = myShout.getLatitude();

	    		Toast.makeText(MainActivity.this,"Longitude: " + lon + " Latitude: " + lat, Toast.LENGTH_LONG).show();
	    		
	    		api = new ShouterAPI();
	    		api.setDelegate(this);
	    		try {

	    			showDialog(DIALOG_LOADING);
	    			api.postShout(myShout);
	    		
	    		} catch (JsonGenerationException e) {
	    			e.printStackTrace();
	    		} catch (JsonMappingException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}     
	        }
	   
}

	/**
	 * This function will pull shouts from the database and update displayed
	 * shouts
	 * 
	 * @author Josiah
	 */ 
	public static void refresh(View view) {

		Location loc = Utility.updateLocation(this);

		List<Shout> newShouts = new ArrayList<Shout>();
		String lat = "";
		String lon = "";

		if (location != null) {
			lat = Shout.convert(loc.getLatitude());
			lon = Shout.convert(loc.getLongitude());

		}
		api = new ShouterAPI();
		api.setDelegate(this);
		
		showDialog(DIALOG_LOADING);
		api.getShout(lat, lon);
		
	}

	/**
	 * Gets shouts and populates the list view
	 * 
	 * @author Craig
	 */
	private void initList() {

		shoutMap.add(createShout("shout", new Shout("Test Shout 1", null)));
		shoutMap.add(createShout("shout", new Shout(
				"Just making sure this App is working", null)));
		shoutMap.add(createShout("shout", new Shout("Woot Shouter", null)));
		shoutMap.add(createShout("shout", new Shout("Still working", null)));
		shoutMap.add(createShout("shout", new Shout("Test Shout 5", null)));
		shoutMap.add(createShout("shout", new Shout("Test Shout 6", null)));
		shoutMap.add(createShout("shout", new Shout("Test Shout 7", null)));

	}

	/**
	 * @author Josiah and Craig
	 * @param name
	 *            A key for the Shout to be pushed into the map
	 * @param shout
	 *            The shout to be added to the list
	 * @return
	 */
	private HashMap<String, String> createShout(String name, Shout shout) {

		shouts.add(shout);
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(name, shout.toString());
		return item;

	}

	/**
	 * @author Josiah
	 * @param none
	 */
	private void updateLocation() {

		userLocation = new UserLocation(this, GPS_RESOLUTION);

		userLocation.requestLocationUpdates(userLocation.defaultRequest(),
				new LocationListener() {

					@Override
					public void onLocationChanged(Location loc) {

						userLocation.disconnect();

						if (loc != null) {

							location = loc;

						}

					}

				});

	}

	/**
	 * @author Craig
	 * @param api
	 *            - API wrapper used to make certain call to api
	 * @param result
	 *            - Return value from http call to api if success
	 * @param e
	 *            - Return value from http call to api if failure
	 * @return List of shouts from call to api for GetShout Return logic for a
	 *         call to the API for getShout
	 */
	@Override
	public List<Shout> onGetShoutReturn(final ShouterAPI api,
			final String result, final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isFinishing())
					dismissDialog(DIALOG_LOADING);

				if (e != null)
					Toast.makeText(MainActivity.this, "Error Getting Shouts, Please Try Again",Toast.LENGTH_LONG).show();
				else {
					List<Shout> shoutList = new ArrayList();
					Gson gson = new Gson();
					try {
						TypeToken<List<Shout>> token = new TypeToken<List<Shout>>() {};
						shoutList = gson.fromJson(result.substring(10,result.length()-1), token.getType());
						//Collections.reverse(shoutList);
						api.setShoutList(shoutList);
					} catch (Exception e1) {
						//e1.printStackTrace();
						Toast.makeText(MainActivity.this, "Error Please try again",Toast.LENGTH_LONG).show();
					}
					//Toast.makeText(MainActivity.this, "GET" + result.substring(10, result.length()-1),Toast.LENGTH_LONG).show();
					// Testing stuff 
					shoutMap = new ArrayList<Map<String,String>>();
					for (Shout s : api.getShoutList()) {
						shoutMap.add(0, createShout("shout", s));
						shouts.add(0, s);
					}

					SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, shoutMap,android.R.layout.simple_list_item_1, new String[] { "shout" },new int[] { android.R.id.text1 });
					lv.setAdapter(adapter);
				}
			}
		});
		return shoutList;
	}

	/**
	 * @author Craig
	 * @param api
	 *            - API wrapper used to make certain call to api
	 * @param result
	 *            - Return value from http call to api if success
	 * @param e
	 *            - Return value from http call to api if failure Return logic
	 *            for a call to the API for postShout
	 */
	public void onPostShoutReturn(final ShouterAPI api, final String result,
			final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isFinishing())
					dismissDialog(DIALOG_LOADING);

				if (e != null)
					Toast.makeText(MainActivity.this, "Error Posting Shout, Please Try Again",Toast.LENGTH_LONG).show();
				else {
					List<Shout> shoutList = new ArrayList();
					Gson gson = new Gson();
					try {
						TypeToken<List<Shout>> token = new TypeToken<List<Shout>>() {};
						shoutList = gson.fromJson(result.substring(10,result.length()-1), token.getType());
						//Collections.reverse(shoutList);
						api.setShoutList(shoutList);
					} catch (Exception e1) {
						//e1.printStackTrace();
						Toast.makeText(MainActivity.this, "Error, please try again",Toast.LENGTH_LONG).show();
					}
					//Toast.makeText(MainActivity.this, "GET" + result.substring(10, result.length()-1),Toast.LENGTH_LONG).show();
					// Testing stuff 
					shoutMap = new ArrayList<Map<String,String>>();
					for (Shout s : api.getShoutList()) {
						shoutMap.add(0, createShout("shout", s));
						shouts.add(0, s);
						//Toast.makeText(this, "Just received: " + s.getID(), Toast.LENGTH_LONG);
					}

					SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, shoutMap,android.R.layout.simple_list_item_1, new String[] { "shout" },new int[] { android.R.id.text1 });
					lv.setAdapter(adapter);
				}
			}
		});
	}

	/**
	 * @author Craig
	 * @param api
	 *            - API wrapper used to make certain call to api
	 * @param result
	 *            - Return value from http call to api if success
	 * @param e
	 *            - Return value from http call to api if failure
	 * @return List of shouts from call to api for getComment Return logic for a
	 *         call to the API for getComment
	 */
	public List<Shout> onGetCommentReturn(ShouterAPI api, final String result,
			final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isFinishing())
					dismissDialog(DIALOG_LOADING);

				if (e != null)
					Toast.makeText(MainActivity.this, "Error getting sentence",
							Toast.LENGTH_SHORT).show();
				else {
					Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG)
							.show();
					// Get Comment Success Logic
					// Same as get shout
				}
			}
		});
		return shoutList;
	}

	/**
	 * @author Craig
	 * @param api
	 *            - API wrapper used to make certain call to api
	 * @param result
	 *            - Return value from http call to api if success
	 * @param e
	 *            - Return value from http call to api if failure Return logic
	 *            for a call to the API for postComment
	 */
	public void onPostCommentReturn(ShouterAPI api, final String result,
			final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isFinishing())
					dismissDialog(DIALOG_LOADING);

				if (e != null)
					Toast.makeText(MainActivity.this, "Error getting sentence",
							Toast.LENGTH_SHORT).show();
				else {
					Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG)
							.show();
					// Post Comment Success Logic
					// same as post shout
				}
			}
		});
	}

	/*/ Testing out ASync activities. May be used later on, as of now it is not used.
	private class putShoutAsyncTask extends AsyncTask<Shout, Void, String> {
		@Override
		protected void onPreExecute() {
			showDialog(DIALOG_LOADING);
		}

		@Override
		protected String doInBackground(Shout... message) {
			String path = "/api/shout/create";
			ResponseEntity<String> response;
			try {
				HttpHeaders headers = new HttpHeaders();
				HttpEntity<String> request = new HttpEntity<String>(headers);

				String url = Shouter_URL + path + "?phoneId="
						+ message[0].getID() + "&message="
						+ message[0].getMessage() + "&latitude="
						+ message[0].getLatitude() + "&longitude="
						+ message[0].getLongitude() + "&parentId="
						+ message[0].getParent();
				response = REST.exchange(url, HttpMethod.PUT, request,String.class);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			return response.getBody();
		}

		@Override
		protected void onPostExecute(String result) {
			if (!isFinishing())
				dismissDialog(DIALOG_LOADING);

			if (result == null)
				Toast.makeText(MainActivity.this, "Error getting Shout",
						Toast.LENGTH_LONG).show();
			else
				Toast.makeText(MainActivity.this, "PutShout" + result,
						Toast.LENGTH_SHORT).show();
		}
	};
*/	
	/**
	 * Check the device to make sure it has the Google Play Services APK. If
	 * it doesn't, display a dialog that allows users to download the APK from
	 * the Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
	    int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	    if (resultCode != ConnectionResult.SUCCESS) {
	        if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
	            GooglePlayServicesUtil.getErrorDialog(resultCode, this,
	                    PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.i(TAG, "This device is not supported.");
	            finish();
	        }
	        return false;
	    }
	    return true;
	}

	private String getRegistrationId(Context context) {
	    
		final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i(TAG, "Registration not found.");
	        return "";
	    } 
	    
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	
	    if (registeredVersion != currentVersion) {
	        Log.i(TAG, "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    
		return getSharedPreferences(MainActivity.class.getSimpleName(),
	            Context.MODE_PRIVATE);
	}

	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {throw new RuntimeException("Could not get package name: " +  e);}
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
	    
		new AsyncTask<Object, Object, Object>() {
	        @Override
	        protected String doInBackground(Object... params) {
	            String msg = "";
	            try {
	                if (gcm == null) {
	                    gcm = GoogleCloudMessaging.getInstance(context);
	                }
	                regid = gcm.register(SENDER_ID);
	                msg = "Device registered, registration ID=" + regid;

	                sendRegistrationIdToBackend();

	                // Persist the regID - no need to register again.
	                storeRegistrationId(context, regid);
	            } catch (IOException ex) {
	                msg = "Error :" + ex.getMessage();
	                // If there is an error, don't just keep trying to register.
	                // Require the user to click a button again, or perform
	                // exponential back-off.
	            }
	            return msg;
	        }
	        
	        protected void onPostExecute(String msg) {
	            mDisplay.append(msg + "\n");
	        }

			
	    }.execute(null, null, null);
	    
	}
	
	private void storeRegistrationId(Context context, String regId) {
	    
		final SharedPreferences prefs = getGCMPreferences(context);
	    int appVersion = getAppVersion(context);
	    
	    Log.i(TAG, "Saving regId on app version " + appVersion);
	    SharedPreferences.Editor editor = prefs.edit();
	    
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, appVersion);
	    editor.commit();
	
	}
	
	private void sendRegistrationIdToBackend() {

		String android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		//Looper.prepare();
        //Toast.makeText(this, "Sending regID to backend", Toast.LENGTH_LONG).show();
		api.register(android_id, "first", "last", regid);

	}

}
