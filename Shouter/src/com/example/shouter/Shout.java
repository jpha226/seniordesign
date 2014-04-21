package com.example.shouter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.location.Location;

public class Shout {

	private String message, id, latitude, longitude, phoneId, parentID, timestamp, userName;
	private Timestamp time_stamp;
	private int numLikes, numComments;

	// Constructor
	public Shout(String m, Location loc) {

		message = m;

		if (loc != null) {
			latitude = convert(loc.getLatitude());
			longitude = convert(loc.getLongitude());
			// latitude += loc.getLatitude();
			// longitude += loc.getLongitude();
		}
	}

	// Constructor for comment
	public Shout(String m, Location loc, String p) {

		message = m;
		id = p;
		if (loc != null) {
			latitude = convert(loc.getLatitude());
			longitude = convert(loc.getLongitude());
		}
	}

	// Accessor functions
	public String getMessage() {
		return message;
	}

	public String getID() {
		return id;
	}

	public String getUser() {
		return phoneId;
	}

	public String getParent() {
		return parentID;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getTime() {
		if (null!= timestamp){
			return format(Long.parseLong(timestamp),0);
		}
		return null;
	}
	
	public String getTimestamp() {
		
		return timestamp;
	}
	
	public static String format(long mnSeconds, long mnNanoseconds) {
	    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
	    return sdf.format(new Date(mnSeconds*1000 + mnNanoseconds/1000000));
	}

	// Mutator functions
	public void setMessage(String m) {
		message = m;
	}

	public void setID(String i) {
		id = i;
	}

	public void setUser(String user) {
		phoneId = user;
	}

	public void setParent(String p) {
		parentID = p;
	}

	public void setTime(Timestamp time) {
		time_stamp = time;
	}

	public void setLatitude(double l) {
		latitude = "" + l;
	}

	public void setLongitude(double l) {
		longitude = "" + l;
	}

	public String toString() {
		return message;
	}

	public static String convert(double degrees) {
		if (degrees <= 0) {
			degrees = degrees + 180;
		}
		String conDeg = "" + degrees;
		return conDeg;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getNumComments() {
		return numComments;
	}

	public void setNumComments(int numComments) {
		this.numComments = numComments;
	}

	public int getNumLikes() {
		return numLikes;
	}

	public void setNumLikes(int numLikes) {
		this.numLikes = numLikes;
	}
}
