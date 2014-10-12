package com.valohyd.nextseries.models;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.views.activities.StartActivity;

public class OurNotification {

	private Context context;
	private String notificationTitle,notificationDesc,serie;

	public OurNotification(Context c,String title,String desc,String serie) {
		this.context = c;
		this.notificationDesc = desc;
		this.notificationTitle = title;
		this.serie=serie;
	}

	public void sendNotification() {
		// Récupération du notification Manager
		final NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Création de la notification avec spécification de l'icone de la
		// notification et le texte qui apparait à la création de la notfication
		final Notification notification = new Notification(
				R.drawable.icon, notificationTitle,
				System.currentTimeMillis());

		// Definition de la redirection au moment du clique sur la notification.
		// Dans notre cas la notification redirige vers notre application
		final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, StartActivity.class), 0);

		// Notification & Vibration
		notification.setLatestEventInfo(context, notificationTitle,
				notificationDesc, pendingIntent);
		notification.vibrate = new long[] { 0, 200, 100, 200, 100, 200 };

		notificationManager.notify(serie,0, notification);
	}

	public void deleteNotification() {
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// la suppression de la notification se fait grâce a son ID
		notificationManager.cancel(serie,0);
	}

}
