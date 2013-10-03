package com.example.shouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R;
import android.R.id;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
	List<Map<String, String>> shoutList = new ArrayList<Map<String,String>>();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initList();
		ListView lv = (ListView) findViewById(R.id.listView);
		SimpleAdapter simpleAdpt = new SimpleAdapter(this, shoutList, android.R.layout.simple_list_item_1, new String[]{"shout"}, new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
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
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
		
		refresh(view); // automatically refresh after posting message
		
	}
	
	/* This function will pull shouts from the database and update displayed shouts
	 * 
	 */
	public void refresh(View view){
		
		// Implement refresh function
		
	}
	
	private void initList(){
		shoutList.add(createShout("shout", "Test Shout 1"));
		shoutList.add(createShout("shout", "Just Making Sure the App is working"));
		shoutList.add(createShout("shout", "Woot Shouter"));
		shoutList.add(createShout("shout", "Anoter Test Shout"));
		shoutList.add(createShout("shout", "Test Shout 5"));
		shoutList.add(createShout("shout", "Test Shout 6"));
		shoutList.add(createShout("shout", "Test Shout 7"));
		
	}

//Use hashmaps to populate list. Can be expanded on once shout structure has been defined.
	private HashMap<String, String> createShout(String name, String message){
		HashMap<String, String> shout = new HashMap<String, String>();
		shout.put(name, message);
		return shout;
	}
}
