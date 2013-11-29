package com.example.shouter.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// GCM Intent Service will handle intent
		ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
		
		// Start the service and keeps device awake while launching
		startWakefulService(context, intent.setComponent(comp));
		setResultCode(Activity.RESULT_OK);
		
	}

}
