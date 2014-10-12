package com.valohyd.nextseries.utils;

import android.app.Activity;
import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;

/**
 * Tools for Analytics Tracking
 * @author LPARODI
 *
 */
public class AnalyticsManager {

	private static String PARAMETER_SEPARATOR = "-";

	// Pages
	public static final String KEY_PAGE_HOME = "Accueil";
	public static final String KEY_PAGE_MES_SERIES_LIST = "Mes Series / List";
	public static final String KEY_PAGE_MES_SERIES_VIEW_PAGER = "Mes Series / Pager";
	public static final String KEY_PAGE_RECHERCHE = "Recherche";
	public static final String KEY_PAGE_SERIE_RECHERCHE = "Serie/Recherche";
	public static final String KEY_PAGE_TOTAL_SERIE = "Saisons";
	public static final String KEY_PAGE_SERIE_MES_SERIES = "Serie/Mes Series";
	public static final String KEY_PAGE_EPISODE = "Episode";
	public static final String KEY_PAGE_SETTINGS = "Paramètres";

	
	private static final boolean ACTIVITY_TRACKING_ENABLED = false;

	/**
	 * Start tracking for Activity
	 */
	public static void start(Activity activity) {
		if (ACTIVITY_TRACKING_ENABLED) {
			EasyTracker.getInstance().activityStart(activity);
			Logger.print("Google Analytics - Started with ID: "
					+ EasyTracker.getTracker().getTrackingId());
		}
	}

	/**
	 * Stop tracking for Activity
	 */
	public static void stop(Activity activity) {
		if (ACTIVITY_TRACKING_ENABLED) {
			EasyTracker.getInstance().activityStop(activity);
			Logger.print("Google Analytics - Stopped");
		}
	}

	/**
	 * Send pending hits
	 */
	public static void dispatch() {

		EasyTracker.getInstance().dispatch();

	}

	/**
	 * Track application screen
	 */
	public static void trackScreen(Context context, String screenName) {

		Logger.print("Google Analytics - trackPage: " + screenName);
		EasyTracker.getInstance().setContext(context);
		EasyTracker.getTracker().trackView(screenName);

	}

	/**
	 * Report an event
	 */
	public static void reportEvent(Context context, String eventName) {
		doReportEvent(context, eventName, null);
	}

	public static void reportEvent(Context context, String eventName,
			String[] eventValues) {
		String paramsConcat = "";
		if (eventValues != null) {
			// Concatenate parameters
			for (int i = 0; i < eventValues.length; i++) {
				paramsConcat += eventValues[i]
						+ ((i + 1 == eventValues.length) ? ""
								: PARAMETER_SEPARATOR);
			}
		}
		doReportEvent(context, eventName, paramsConcat);
	}

	private static void doReportEvent(Context context, String eventName,
			String eventValue) {
		EasyTracker.getInstance().setContext(context);
		// EasyTracker.getInstance().trackEvent("ui_action", "button_press",
		// "play_button", opt_value);
		EasyTracker.getTracker().trackEvent(eventName, eventValue, null,
				(long) 0);

	}

}
