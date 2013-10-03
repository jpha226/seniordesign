package com.example.shouter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

}
