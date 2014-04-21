package com.example.shouter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shouter.util.LazyAdapter;
import com.example.shouter.util.ShouterAPI;
import com.example.shouter.util.ShouterAPIDelegate;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class CommentActivity extends Activity implements ShouterAPIDelegate{

        public final static String EXTRA_MESSAGE = "com.example.shouter.MESSAGE";
        public final static String EXTRA_INT = "com.example.shout.INT";
        static final int POST_REQUEST = 1;
        private static final int GPS_RESOLUTION = 1;
        List<Map<String,String>> commentMap = new ArrayList<Map<String,String>>();
        private ShouterAPI api;
        /* Dialogs */
        public static final int DIALOG_LOADING = 0;
        private ListView lv;
        //public PullToRefreshListView lv;
        private String shout_id;
        
        
        @Override
    protected Dialog onCreateDialog(int id) {
                ProgressDialog dialog = null;
        switch (id) {
        case DIALOG_LOADING:
                dialog = new ProgressDialog(this);
                ((ProgressDialog) dialog).setMessage(getString(R.string.loading));                
                ((ProgressDialog) dialog).setProgressStyle(ProgressDialog.STYLE_SPINNER);
                break;
        }
        return dialog;
        }
        
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_comment);
                
                Intent intent = getIntent();
                String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

                shout_id = intent.getStringExtra(MainActivity.EXTRA_ID);
                
                // Create text view
                
                TextView textView = (TextView) findViewById(R.id.comment_text);
                textView.setTextSize(40);
                textView.setText(message);
                
                api = new ShouterAPI();
                api.setDelegate(this);
                
                final View view = findViewById(R.id.postComment);
                
                //lv = (PullToRefreshListView) findViewById(R.id.pull_refresh_list_comment);
                lv = (ListView) findViewById(R.id.CommentListView);
                
                //SimpleAdapter adapter = new SimpleAdapter(this, commentMap, android.R.layout.simple_list_item_1, new String[]{"shout"}, new int[]{android.R.id.text1});
                //SimpleAdapter adapter = new SimpleAdapter(this, commentMap,
        		//		android.R.layout.simple_list_item_2, new String[] { "shout", "header" },
        		//		new int[] { android.R.id.text1, android.R.id.text2 });
                LazyAdapter adapter = new LazyAdapter(CommentActivity.this, commentMap);
                lv.setAdapter(adapter);
                
        		/*lv.setOnRefreshListener(new OnRefreshListener<ListView>() {
        		    @Override
        		    public void onRefresh(PullToRefreshBase<ListView> refreshView) {
        		    	refresh(findViewById(R.id.refresh));
        		    	//refresh(view);
        		        new GetDataTask().execute();
        		    }
        		});  */
        		
                refresh(view);
                
                // Show the Up button in the action bar.
                setupActionBar(); // Josiah was here
                     
        }

        /**
         * Set up the {@link android.app.ActionBar}, if the API is available.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void setupActionBar() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        getActionBar().setDisplayHomeAsUpEnabled(true);
                }
                getActionBar().setTitle("Shouter");
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.comment, menu);

                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                case android.R.id.home:
                        // This ID represents the Home or Up button. In the case of this
                        // activity, the Up button is shown. Use NavUtils to allow users
                        // to navigate up one level in the application structure. For
                        // more details, see the Navigation pattern on Android Design:
                        //
                        // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                        //
                        NavUtils.navigateUpFromSameTask(this);
                        return true;
                }
                return super.onOptionsItemSelected(item);
        }


        /*
         *  This function will push the entered message into the database. 
         *  The message will also be added to the displayed shouts
         */
        public void postComment(View view){
                
        	
        	final EditText input = new EditText(this);
    		AlertDialog.Builder builder=new AlertDialog.Builder(CommentActivity.this);
    		LayoutInflater inflater = CommentActivity.this.getLayoutInflater();
    		//final EditText editText = (EditText) findViewById(R.id.edit_message);
    		builder.setView(inflater.inflate(R.layout.dialog_post_message, null))
    	    .setTitle("Post Shout")
    	    .setView(input)
    	    .setPositiveButton("Shout", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int whichButton) {
    	        	
    	    		String message = input.getText().toString();
    	        	Toast.makeText(CommentActivity.this,"Message: " + message, Toast.LENGTH_LONG).show();
    	        	Location loc = Utility.updateLocation(CommentActivity.this);
                    
                    Shout myShout = new Shout(message, loc);
                    String id = Secure.getString(CommentActivity.this.getContentResolver(),Secure.ANDROID_ID); 
                    
                    myShout.setUser(id);
                    myShout.setParent(shout_id);
                    
                    String lon = myShout.getLongitude();
                    String lat = myShout.getLatitude();
                    //Toast.makeText(this, "message: " + myShout.getMessage(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(this, "PhoneId: " + myShout.getUser(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(this, "parentID: "+ myShout.getParent(), Toast.LENGTH_LONG).show();
                    	api = new ShouterAPI();
                    	api.setDelegate(CommentActivity.this);
                    try {
                            
                            showDialog(DIALOG_LOADING);
                            api.postComment(myShout);
                            
                    } catch (JsonGenerationException e) {e.printStackTrace();} catch (JsonMappingException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}  
    	            //Editable value = view.getText(); 
    	        }
    	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int whichButton) {
    	            // Do nothing.
    	        }
    	    }).show();
    		
    		
                //Intent intent = new Intent(CommentActivity.this, PostActivity.class);
                //intent.putExtra(EXTRA_INT, PostActivity.COMMENT_ACTIVITY);      
                //startActivityForResult(intent, POST_REQUEST);
        }
        
        
        /* This function will pull shouts from the database and update displayed shouts
         * 
         */
        public void refresh(View view){
                
                List<Shout> newShouts = new ArrayList<Shout>();
                //Toast.makeText(CommentActivity.this, "ParentId of get: " +shout_id,Toast.LENGTH_LONG).show();
                api = new ShouterAPI();
                api.setDelegate(CommentActivity.this);
                showDialog(DIALOG_LOADING);
                newShouts = api.getComment(shout_id);
                
              
        }

        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        	    
        	
        	        if (resultCode == RESULT_OK) {
        	                    	
        	        	String message = data.getStringExtra(PostActivity.EXTRA_MESSAGE);
        
        	        	Location loc = Utility.updateLocation(this);
                        
                        Shout myShout = new Shout(message, loc);
                        String id = Secure.getString(this.getContentResolver(),Secure.ANDROID_ID); 
                        
                        myShout.setUser(id);
                        myShout.setParent(shout_id);
                        
                        String lon = myShout.getLongitude();
                        String lat = myShout.getLatitude();
                        //Toast.makeText(this, "message: " + myShout.getMessage(), Toast.LENGTH_LONG).show();
                        //Toast.makeText(this, "PhoneId: " + myShout.getUser(), Toast.LENGTH_LONG).show();
                        //Toast.makeText(this, "parentID: "+ myShout.getParent(), Toast.LENGTH_LONG).show();
                        	api = new ShouterAPI();
                        	api.setDelegate(CommentActivity.this);
                        try {
                                
                                showDialog(DIALOG_LOADING);
                                api.postComment(myShout);
                                
                        } catch (JsonGenerationException e) {e.printStackTrace();} catch (JsonMappingException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
                
        	        }
        	   
        }

             
        /**
         * @author Craig
         * @param api - API wrapper used to make certain call to api
         * @param result - Return value from http call to api if success
         * @param e -  Return value from http call to api if failure
         * @return List of shouts from call to api for getComment
         * Return logic for a call to the API for getComment
         */
        public List<Shout> onGetCommentReturn(final ShouterAPI api, final String result, final Exception e) {
                final List<Shout> shoutList = new ArrayList<Shout>();
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                if(!isFinishing())
                                        dismissDialog(DIALOG_LOADING);
                        
                                if(e != null)
                                        Toast.makeText(CommentActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
                                else{
                                	List<Shout> shoutList = new ArrayList();
                					Gson gson = new Gson();
                					try {
                						TypeToken<List<Shout>> token = new TypeToken<List<Shout>>() {};
                						String[] sepResult= result.split("comments\":");
                						String[] secSepResult = sepResult[1].split(",\"liked");
                						//Toast.makeText(CommentActivity.this, "Parsed?" + result,Toast.LENGTH_LONG).show();
                						//Toast.makeText(CommentActivity.this, "Parsed?" + secSepResult[0],Toast.LENGTH_LONG).show();
                						shoutList = gson.fromJson(secSepResult[0], token.getType());
                						//shoutList = gson.fromJson(result.substring(12,result.length()-1), token.getType());
                						Collections.reverse(shoutList);
                						api.setShoutList(shoutList);
                					} catch (Exception e1) {
                						//e1.printStackTrace();
                						//Toast.makeText(CommentActivity.this, "There was a catch" + e1.toString(),Toast.LENGTH_LONG).show();
                						Toast.makeText(CommentActivity.this, "Error Getting Comments, Please try again" + e1.toString(),Toast.LENGTH_LONG).show();
                					}
                					//Toast.makeText(CommentActivity.this, "GET" + result.substring(12, result.length()-1),Toast.LENGTH_LONG).show();
                					// Testing stuff 
                					commentMap = new ArrayList<Map<String,String>>();
                					for (Shout s : api.getShoutList()) {
                						commentMap.add(0, Utility.createShout(s));
                						//shouts.add(0, s);
                					}

                					//SimpleAdapter adapter = new SimpleAdapter(CommentActivity.this, commentMap,android.R.layout.simple_list_item_1, new String[] { "shout" },new int[] { android.R.id.text1 });
                					//SimpleAdapter adapter = new SimpleAdapter(CommentActivity.this, commentMap,android.R.layout.simple_list_item_2, new String[] {"shout","header" },new int[] { android.R.id.text1, android.R.id.text2 });
                					LazyAdapter adapter = new LazyAdapter(CommentActivity.this, commentMap);
                					lv.setAdapter(adapter);
                				}
                			}
                		});
                		return shoutList;
                	}
        
        /**
         * @author Craig
         * @param api - API wrapper used to make certain call to api
         * @param result - Return value from http call to api if success
         * @param e -  Return value from http call to api if failure
         * Return logic for a call to the API for postComment
         */
        public void onPostCommentReturn(final ShouterAPI api, final String result, final Exception e) {
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                                if(!isFinishing())
                                        dismissDialog(DIALOG_LOADING);
                        
                                if(e != null)
                                        Toast.makeText(CommentActivity.this, "Error getting sentence", Toast.LENGTH_SHORT).show();
                                else{
                                	List<Shout> shoutList = new ArrayList();
                					Gson gson = new Gson();
                					try {
                						TypeToken<List<Shout>> token = new TypeToken<List<Shout>>() {};
                						String[] sepResult= result.split("comments\":");
                						String[] secSepResult = sepResult[1].split(",\"liked");
                						//Toast.makeText(CommentActivity.this, "Parsed?" + result,Toast.LENGTH_LONG).show();
                						//Toast.makeText(CommentActivity.this, "Parsed?" + secSepResult[0],Toast.LENGTH_LONG).show();
                						shoutList = gson.fromJson(secSepResult[0], token.getType());
                						//shoutList = gson.fromJson(result.substring(12,result.length()-1), token.getType());
                						Collections.reverse(shoutList);
                						api.setShoutList(shoutList);
                					} catch (Exception e1) {
                						//e1.printStackTrace();
                						Toast.makeText(CommentActivity.this, "Error, Please Try again",Toast.LENGTH_LONG).show();
                					}
                					//Toast.makeText(CommentActivity.this, "GET" + result.substring(12, result.length()-1),Toast.LENGTH_LONG).show();
                					// Testing stuff 
                					commentMap = new ArrayList<Map<String,String>>();
                					for (Shout s : api.getShoutList()) {
                						commentMap.add(0, Utility.createShout(s));
                						//shouts.add(0, s);
                					}

                					//SimpleAdapter adapter = new SimpleAdapter(CommentActivity.this, commentMap,android.R.layout.simple_list_item_1, new String[] { "shout" },new int[] { android.R.id.text1 });
                					//SimpleAdapter adapter = new SimpleAdapter(CommentActivity.this, commentMap,android.R.layout.simple_list_item_2, new String[] {"shout", "header" },new int[] { android.R.id.text1, android.R.id.text2 });
                					LazyAdapter adapter = new LazyAdapter(CommentActivity.this, commentMap);
                					lv.setAdapter(adapter);
                				}
                			}
                		});
                	}


        public void onRegistrationReturn(ShouterAPI api, final String result, final Exception e){}
        public void onPostShoutReturn(ShouterAPI api, final String result,final Exception e) {}
    	public List<Shout> onGetShoutReturn(final ShouterAPI api, final String result, final Exception e) { return null;}
    	
    	
    	/*private class GetDataTask extends AsyncTask<Void, Void, String[]> {
            @Override
            protected String[] doInBackground(Void... params) {
                    // Simulates a background job.
                    try {
                            Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                    String[] mStrings = null;
    				return mStrings;
            }
    	    @Override
    	    protected void onPostExecute(String[] result) {
    	        // Call onRefreshComplete when the list has been refreshed.
    	        lv.onRefreshComplete();
    	        super.onPostExecute(result);
    	    }
    	}*/
       
}
