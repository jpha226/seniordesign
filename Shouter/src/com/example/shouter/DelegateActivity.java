package com.example.shouter;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.resttemplate.R;
import com.example.shouter.util.ShouterAPI;
import com.example.shouter.util.ShouterAPIDelegate;

@SuppressWarnings("deprecation")
public class DelegateActivity extends MainActivity implements ShouterAPIDelegate {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        ShouterAPI api = new ShouterAPI();
        api.setDelegate(this);
        
        showDialog(DIALOG_LOADING);
        api.postShout(shout);
    }

    /**
     * We're not guaranteed that this is called on the UI thread.  Any changes to the
     * UI will need to be done using runOnUiThread()
     */
	@Override
	public List<Shout> onGetShoutReturn(ShouterAPI api, final String result, final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(DelegateActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else
					
				//Get Shout Success Logic
				//Get Json of shouts
				//Needs to convert to list of shouts
				//Possible need a custom comparator
			}
		});
		return shoutList;
	}
    
	public void onPostShoutReturn(ShouterAPI api, final String result, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(DelegateActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else
					((TextView) findViewById(R.id.text)).setText("post succesful");
			}
		});
	}
	
	public List<Shout> onGetCommentReturn(ShouterAPI api, final String result, final Exception e) {
		final List<Shout> shoutList = new ArrayList<Shout>();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(DelegateActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else
				//Get Comment Success Logic
				//Same as get shout 
			}
		});
		return shoutList;
	}
	
	public void onPostCommentReturn(ShouterAPI api, final String result, final Exception e) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(!isFinishing())
					dismissDialog(DIALOG_LOADING);
		        
				if(e != null)
					Toast.makeText(DelegateActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
				else
					((TextView) findViewById(R.id.text)).setText("post succesful");
			}
		});
	}
}
