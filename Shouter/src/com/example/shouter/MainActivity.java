package com.example.shouter;

import java.util.HashMap;

import android.R;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initList();
		ListView lv = (ListView) findViewById(R.id.listView);
		simpleAdpt = new SimpleAdapter(this, shoutList, android.R.layout.simple_list_item_1, new String[]{"shout"}, new int[] {android.R.id.text1});
		lv.setAdapter(simpleAdpt);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void sendMessage(View view){
		// Do something in response to button
		
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
		
	}

//Use hashmaps to populate list. Can be expanded on once shout structure has been defined.
	private HashMap<String, String> createShout(String name, String shout){
		HashMap<String, String> shout = new HashMap<String, String>();
		shout.put(name, shout)
		return shout
	}
}
