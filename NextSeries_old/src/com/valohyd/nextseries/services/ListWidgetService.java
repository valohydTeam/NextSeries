package com.valohyd.nextseries.services;

import java.util.Calendar;
import java.util.Date;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.ListViewsFactory;
import com.valohyd.nextseries.ListWidgetProvider.PlanningAlarmWidgetProvider;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		Log.d("NextSeries-Widget", "Lancement de la fabrique");
		// On renvoit l'adapter que la Factory va nous faire pour la ListView du
		// widget
		return (new ListViewsFactory(this, intent));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("NextSeries", "DESTRUCTION DU SERVICE !");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("NextSeries-Widget", "Lancement du service");
		super.onStartCommand(intent, flags, startId);
		AlarmManager am = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		// On instancie l'Intent qui va être appelé au moment du reveil.
		Intent intent2 = new Intent(this, PlanningAlarmWidgetProvider.class);

		// On créer le pending Intent qui identifie l'Intent de reveil avec
		// un
		// ID et un/des flag(s)
		PendingIntent pendingintent = PendingIntent.getBroadcast(this,
				12345678, intent2, 0);

		// On annule l'alarm pour replanifier si besoin
		am.cancel(pendingintent);

		// On set l'alarme selon les prefs du fichier widget_config.xml
		Calendar cal_alarm = Calendar.getInstance();
		cal_alarm.setTime(new Date());
		cal_alarm.set(Calendar.HOUR_OF_DAY,
				getResources().getInteger(R.integer.hour_update));
		cal_alarm.set(Calendar.MINUTE,
				getResources().getInteger(R.integer.minute_update));
		cal_alarm.set(Calendar.SECOND,
				getResources().getInteger(R.integer.seconds_update));
		cal_alarm.set(Calendar.MILLISECOND,
				getResources().getInteger(R.integer.millis_update));
		if (Calendar.getInstance().after(cal_alarm)) {
			cal_alarm.add(Calendar.HOUR,
					getResources().getInteger(R.integer.refresh_hours_period));
		}
		am.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),
				pendingintent);
		return START_NOT_STICKY;
	}

}