package com.valohyd.nextseries;

import com.valohyd.nextseries.services.NotificationService;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	 
    public static final String TAG = "TestApp";
 
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TestApp", "Got the Boot Event>>>");
        Log.d("TestApp", "Starting MySimpleService>>>");
        context.startService(new Intent().setComponent(new ComponentName(
                context.getPackageName(), NotificationService.class.getName())));
    }
}
