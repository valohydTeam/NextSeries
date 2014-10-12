package com.valohyd.nextseries.utils;

import android.util.Log;

/**
 * Classe utile pour faciliter le log dans l'applciation
 * 
 * @author valohydTeam
 * 
 */
public class Logger {
	private static final String TAG = "NextSeries";

	/**
	 * Affiche un message info
	 * @param message
	 */
	public static void print(String message) {
		Log.i(TAG, message);
	}

	/**
	 * Affiche un message Warning
	 * @param message
	 */
	public static void printWarning(String message) {
		Log.w(TAG, message);
	}

	/**
	 * Affiche une quenelle !
	 * @param quenelle
	 */
	public static void printQuenelle(String quenelle) {
		Log.e(TAG, quenelle);
	}
}
