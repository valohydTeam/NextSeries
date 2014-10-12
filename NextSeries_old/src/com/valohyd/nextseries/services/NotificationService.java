package com.valohyd.nextseries.services;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.GetPlanning;
import com.valohyd.nextseries.models.OurNotification;
import com.valohyd.nextseries.models.Serie;

public class NotificationService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		Log.d("TestApp", ">>>onRebind()");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("TestApp", ">>>onCreate()");
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Log.d("TestApp", ">>>onStart()");
		ArrayList<Serie> liste = new ArrayList<Serie>();
		for(Serie s:liste){
			new OurNotification(this.getBaseContext(), "Nouvel Episode !", s.getEps().get(0).getTitre(), s.getTitre()).sendNotification();
		}
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
}
