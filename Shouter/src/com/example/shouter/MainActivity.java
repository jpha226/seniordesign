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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidtools.networking.Networking;
import com.example.shouter.util.ShouterAPI;
import com.example.shouter.util.ShouterAPIDelegate;
import com.google.android.gms.location.LocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.*;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements ShouterAPIDelegate {// implements
																			// GooglePlayServicesClient.ConnectionCallbacks,
																			// GooglePlayServicesClient.OnConnectionFailedListener{

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE"; // message
	public final static String EXTRA_ID = "com.example.shouter.ID"; // id of
																	// shout
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	private static final int GPS_RESOLUTION = 1;
	private UserLocation userLocation; // For finding current location
	private static Location location; // current location of user
	private List<Map<String, String>> shoutList = new ArrayList<Map<String, String>>(); // Maintains
																						// list
																						// of
																						// shout
																						// messages
	private List<Shout> shouts = new ArrayList<Shout>(); // Maintains the actual
															// shouts in the
															// list
	private List<Shout> innerShoutList = new ArrayList<Shout>();
	private ShouterAPI api; // API to call
	private RestTemplate REST = Networking.defaultRest();
	private static final String Shouter_URL = "http://shouterapi-env.elasticbeanstalk.com/shouter";

	/* Dialogs */
	public static final int DIALOG_LOADING = 0;
	private ListView lv; // The list

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

		initList();
		updateLocation();

		lv = (ListView) findViewById(R.id.listView);

		SimpleAdapter adapter = new SimpleAdapter(this, shoutList,
				android.R.layout.simple_list_item_1, new String[] { "shout" },
				new int[] { android.R.id.text1 });
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parentAdapter, View view,
					int position, long id) {

				// We know the View is a TextView so we can cast it

				TextView clickedView = (TextView) view;
				Toast.makeText(
						MainActivity.this,
						"Item with id [" + id + "] - Position [" + position
								+ "] - Shout [" + clickedView.getText() + "]",
						Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(MainActivity.this,
						CommentActivity.class);
				// EditText editText = (EditText)
				// findViewById(R.id.edit_message);
				String message = (String) clickedView.getText();

				updateLocation();

				intent.putExtra(EXTRA_MESSAGE, message);
				intent.putExtra(EXTRA_ID, shouts.get(position).getID());
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

	/**
	 * This function will push the entered message into the database. The
	 * message will also be added to the displayed shouts
	 * 
	 * @author Josiah
	 * @param view
	 *            The current view that function is called from
	 */
	public void postMessage(View view) {

		// Do something in response to button
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();

		updateLocation();

		Shout myShout = new Shout(message, location);

		String android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);
		myShout.setID(android_id);

		String lon = myShout.getLongitude();
		String lat = myShout.getLatitude();

		Toast.makeText(MainActivity.this,
				"Longitude: " + lon + " Latitude: " + lat, Toast.LENGTH_LONG)
				.show();

		try {

			showDialog(DIALOG_LOADING);
			api.postShout(myShout);
			// new putShoutAsyncTask().execute(myShout);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// refresh(view); // automatically refresh after posting message

	}

	/**
	 * This function will pull shouts from the database and update displayed
	 * shouts
	 * 
	 * @author Josiah
	 */
	public void refresh(View view) {

		updateLocation();

		List<Shout> newShouts = new ArrayList<Shout>();
		String lat = "";
		String lon = "";

		if (location != null) {
			lat = Shout.convert(location.getLatitude());
			lon = Shout.convert(location.getLongitude());

		}

		showDialog(DIALOG_LOADING);
		newShouts = api.getShout(lat, lon);
		Toast.makeText(MainActivity.this, "NEWSHOUTSIZE" + newShouts.size(),
				Toast.LENGTH_LONG).show();
		for (Shout s : newShouts) {
			Toast.makeText(MainActivity.this, "NEWSHOUTTEST" + s.getMessage(),
					Toast.LENGTH_LONG).show();
			shoutList.add(0, createShout("shout", s));
			shouts.add(0, s);
		}
		// shoutList.add(0,createShout("shout", new Shout("refresh",null)));

		SimpleAdapter adapter = new SimpleAdapter(this, shoutList,
				android.R.layout.simple_list_item_1, new String[] { "shout" },
				new int[] { android.R.id.text1 });
		lv.setAdapter(adapter);

	}

	/**
	 * Gets shouts and populates the list view
	 * 
	 * @author Craig
	 */
	private void initList() {

		shoutList.add(createShout("shout", new Shout("Test Shout 1", null)));
		shoutList.add(createShout("shout", new Shout(
				"Just making sure this App is working", null)));
		shoutList.add(createShout("shout", new Shout("Woot Shouter", null)));
		shoutList.add(createShout("shout", new Shout("Still working", null)));
		shoutList.add(createShout("shout", new Shout("Test Shout 5", null)));
		shoutList.add(createShout("shout", new Shout("Test Shout 6", null)));
		shoutList.add(createShout("shout", new Shout("Test Shout 7", null)));

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
					Toast.makeText(MainActivity.this, e.toString(),
							Toast.LENGTH_LONG).show();
				else {
					List<Shout> shoutList = new ArrayList();
					// ObjectMapper mapper = new ObjectMapper();
					try {
						Gson gson = new Gson();
						TypeToken<List<Shout>> token = new TypeToken<List<Shout>>() {
						};
						shoutList = gson.fromJson(result, token.getType());
						Collections.reverse(shoutList);
						api.setShoutList(shoutList);
						// mapper.readValue(result, new
						// TypeReference<List<Shout>>(){}));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					Toast.makeText(MainActivity.this, "GET" + result,
							Toast.LENGTH_LONG).show();
				}
			}
		});
		Toast.makeText(MainActivity.this,
				"blah" + api.getShoutList().get(0).getMessage(),
				Toast.LENGTH_LONG).show();
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
	public void onPostShoutReturn(ShouterAPI api, final String result,
			final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (!isFinishing())
					dismissDialog(DIALOG_LOADING);

				if (e != null)
					Toast.makeText(MainActivity.this, e.toString(),
							Toast.LENGTH_LONG).show();
				else {
					Toast.makeText(MainActivity.this, "POST" + result,
							Toast.LENGTH_LONG).show();

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

	// Testing out ASync activities. STill no clue what is going on.
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
				response = REST.exchange(url, HttpMethod.PUT, request,
						String.class);
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
}
