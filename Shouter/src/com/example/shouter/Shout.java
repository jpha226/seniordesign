package com.example.shouter;

import java.sql.Timestamp;

import android.location.Location;

public class Shout {

	private String message, phoneId, latitude, longitude, userID, parentID;
	private Timestamp time_stamp;

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
		phoneId = p;
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
		return phoneId;
	}

	public String getUser() {
		return userID;
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

	public Timestamp getTime() {
		return time_stamp;
	}

	// Mutator functions
	public void setMessage(String m) {
		message = m;
	}

	public void setID(String i) {
		phoneId = i;
	}

	public void setUser(String user) {
		userID = user;
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
}
