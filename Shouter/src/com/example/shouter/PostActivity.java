package com.example.shouter;

import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.EditText;

public class PostActivity extends Activity {

	
	final static int MAIN_ACTIVITY = 0, COMMENT_ACTIVITY = 1;
	public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE"; // message
	public final static String EXTRA_ID = "com.example.shouter.ID"; // id of
	
	private static final int GPS_RESOLUTION = 1;
	
	private int parent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.post, menu);
		return true;
	}
	
	
	public void postMessage(){
		
		EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        Intent intent;
        
        if(parent == this.MAIN_ACTIVITY)
        	intent = new Intent(PostActivity.this, MainActivity.class);
        else
        	intent = new Intent(this, CommentActivity.class);
        
		intent.putExtra(EXTRA_MESSAGE, message);
		
		setResult(Activity.RESULT_OK, intent);
		
		finishActivity(CommentActivity.POST_REQUEST);

        
	}

}
