package com.valohyd.nextseries.views.activities;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.Helper;

public class SettingsActivity extends PreferenceActivity {

	/**
	 * Lancement du Tracker Google Analytics
	 */
	@Override
	public void onStart() {
		super.onStart();
		AnalyticsManager.start(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

	@Override
	protected void onStop() {
		super.onStop();
		AnalyticsManager.stop(this);
		Helper.displayExceptionMessage(this, "Changement sauvegardé(s)");
	}

}
