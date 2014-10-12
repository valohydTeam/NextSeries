package com.valohyd.nextseries;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.services.ListWidgetService;
import com.valohyd.nextseries.views.activities.StartActivity;
import com.valohyd.nextseries.views.fragments.MySeriesFragmentViewPager;
import com.valohyd.nextseries.views.fragments.OngletMySeriesFragment;

public class ListWidgetProvider extends AppWidgetProvider {
	private Intent svcIntent;
	public static String ACTION_WIDGET_RECEIVER = "ListWidgetProvider";
	public static final String EXTRA_SERIE = "com.android.valohyd.nextseries.EXTRA_SERIE";
	public static final String ITEM_ACTION = "com.android.valohyd.nextseries.TOAST_ACTION";

	AppWidgetManager appWidgetManager;
	static int[] appWidgetIds;

	@Override
	public void onUpdate(Context ctxt, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.d("NextSeries-Widget", "OnUpdate called !");
		this.appWidgetIds = appWidgetIds;
		this.appWidgetManager = appWidgetManager;
		for (int i = 0; i < appWidgetIds.length; i++) {
			// Construction de l'intent pour lancer le service
			svcIntent = new Intent(ctxt, ListWidgetService.class);
			svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			svcIntent.setData(Uri.parse(svcIntent
					.toUri(Intent.URI_INTENT_SCHEME)));
			// On lance le service
			ctxt.startService(svcIntent);

			// On recupere la vue du widget
			RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
					R.layout.widget_layout);
			Date updateDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("EEEE HH:mm");
			widget.setTextViewText(R.id.widgetUpdateDate, "Mise à jour : "
					+ sdf.format(updateDate));

			// On set un adapter pour la ListView : l'adapter sera renvoyé par
			// le service
			widget.setRemoteAdapter(appWidgetIds[i], R.id.words, svcIntent);

			// INTENT ITEM
			Intent toastIntent = new Intent(ctxt, ListWidgetProvider.class);
			// Set the action for the intent.
			// When the user touches a particular view, it will have the effect
			// of
			// broadcasting ITEM_ACTION.
			toastIntent.setAction(ListWidgetProvider.ITEM_ACTION);
			toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
					appWidgetIds[i]);
			PendingIntent toastPendingIntent = PendingIntent.getBroadcast(ctxt,
					0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			widget.setPendingIntentTemplate(R.id.words, toastPendingIntent);

			// Creation d'un Intent pour aller sur l'appli
			Intent intent = new Intent(ctxt, StartActivity.class);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds); // Identifies
																					// the
																					// particular
																					// widget...
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// Make the pending intent unique...
			intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
			PendingIntent pendIntent = PendingIntent.getActivity(ctxt, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			widget.setOnClickPendingIntent(R.id.editText2, pendIntent);

			// Creation d'un Intent pour rafraishir
			Intent clickIntent = new Intent(ctxt, ListWidgetProvider.class);

			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(ctxt, 0,
					clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			widget.setOnClickPendingIntent(R.id.widgetRefreshButton,
					pendingIntent);

			// On MAJ le widget
			appWidgetManager.updateAppWidget(appWidgetIds[i], widget);
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds[i],
					R.id.words);
		}
		super.onUpdate(ctxt, appWidgetManager, appWidgetIds);
	}

	/**
	 * Methode permettant de reguler les intent pour savoir si on a cliqué sur
	 * un item de la liste. Si l'action est de type ITEM_ACTION alors on peut
	 * aller sur le planning de la serie
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ITEM_ACTION)) {
			String serieClicked = intent.getStringExtra(EXTRA_SERIE);

			// Creation d'un Intent pour aller sur l'appli
			Intent appIntent = new Intent(context, 	StartActivity.class);
			appIntent.putExtra("serie", serieClicked);
			appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(appIntent);
		}
		super.onReceive(context, intent);
	}

	/**
	 * Classe interne representant l'alarme pour la mise a jour du widget
	 * 
	 * @author valohydteam
	 * 
	 */
	public static class PlanningAlarmWidgetProvider extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("NextSeries", "Appel de l'alarme");
			Intent alarmIntent = new Intent(context, ListWidgetProvider.class);

			alarmIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			alarmIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					appWidgetIds);
			context.sendBroadcast(alarmIntent);
			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			// On instancie l'Intent qui va être appelé au moment du reveil.
			Intent intent2 = new Intent(context,
					PlanningAlarmWidgetProvider.class);

			// On créer le pending Intent qui identifie l'Intent de reveil avec
			// un
			// ID et un/des flag(s)
			PendingIntent pendingintent = PendingIntent.getBroadcast(context,
					12345678, intent2, 0);

			// On annule l'alarm pour replanifier si besoin
			am.cancel(pendingintent);

		}

	}
}