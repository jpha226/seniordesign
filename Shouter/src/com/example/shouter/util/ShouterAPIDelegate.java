
package com.example.shouter.util;

public interface ShouterAPIDelegate {
	public void onGetShoutReturn(ShouterAPI api, String result, Exception e);
	public void onPostShoutReturn(ShouterAPI api, String result, Exception e);
	public void onGetCommentReturn(ShouterAPI api, String result, Exception e);
	public void onPostCommentReturn(ShouterAPI api, String result, Exception e);
}

