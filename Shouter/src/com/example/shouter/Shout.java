package com.example.shouter;

import java.sql.Timestamp;
import android.location.*;

public class Shout {

	private String message, id, userID, parentID;
	private Timestamp time_stamp;
	private Location location_stamp;
	
	
	// Constructor
	public Shout(String m, Location loc){}
	
	// Constructor for comment
	public Shout(String m, Location loc, String p){}
	
	// Accessor functions
	public String getMessage(){return message;}
	
	public String getID(){return id;}
	
	public String getUser(){return userID;}
	
	public String getParent(){return parentID;}
	
	public Location getLocation(){return location_stamp;}
	
	public Timestamp getTime(){return time_stamp;}
	
	
	// Mutator functions
	public void setMessage(String m){message = m;}
	
	public void setID(String i){id = i;}
	
	public void setUser(String user){userID = user;}
	
	public void setParent(String p){parentID = p;}
	
	public void setLocation(Location loc){location_stamp = loc;}
	
	public void setTime(Timestamp time){time_stamp = time;}

}
