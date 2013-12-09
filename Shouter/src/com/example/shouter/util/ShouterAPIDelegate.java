package com.example.shouter.util;

import java.util.List;

import com.example.shouter.Shout;

public interface ShouterAPIDelegate {
	public List<Shout> onGetShoutReturn(ShouterAPI api, String result,
			Exception e);

	public void onPostShoutReturn(ShouterAPI api, String result, Exception e);

	public List<Shout> onGetCommentReturn(ShouterAPI api, String result,
			Exception e);

	public void onPostCommentReturn(ShouterAPI api, String result, Exception e);

	public void onRegistrationReturn(ShouterAPI api, String result, Exception e);
}