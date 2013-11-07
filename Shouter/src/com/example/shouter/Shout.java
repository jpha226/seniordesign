package com.example.shouter;

import java.sql.Timestamp;

import android.location.Location;

public class Shout {

	private String message, id, latitude, longitude, userID, parentID;
	private Timestamp time_stamp;
	
	
	// Constructor
	public Shout(String m, Location loc){
		
		message = m;
		
		latitude = "";
		longitude = "";
		
		if(loc != null){
			latitude +=  loc.getLatitude();
			longitude += loc.getLongitude();
		}
	}
	
	// Constructor for comment
	public Shout(String m, Location loc, String p){
		
		message = m;
		id = p;
		if(loc != null){
			latitude = "" + loc.getLatitude();
			longitude = "" + loc.getLongitude();		
		}
	}
	
	// Accessor functions
	public String getMessage(){return message;}
	
	public String getID(){return id;}
	
	public String getUser(){return userID;}
	
	public String getParent(){return parentID;}
	
	public String getLatitude(){return latitude;}
	
	public String getLongitude(){return longitude;}
	
	public Timestamp getTime(){return time_stamp;}
	
	
	// Mutator functions
	public void setMessage(String m){message = m;}
	
	public void setID(String i){id = i;}
	
	public void setUser(String user){userID = user;}
	
	public void setParent(String p){parentID = p;}
	
	public void setTime(Timestamp time){time_stamp = time;}
	
	public void setLatitude(double l){latitude = "" + l;}
	
	public void setLongitude(double l){longitude = "" + l;}
	
	public String toString(){return message;}
	
}
