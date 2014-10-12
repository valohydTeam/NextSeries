package com.valohyd.nextseries.views.activities;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

import com.valohyd.nextseries.R;
import com.google.analytics.tracking.android.EasyTracker;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.views.fragments.TotalSerieFragment;

public class InfosSerieActivity extends FragmentActivity {
	Bundle b;

	/**
	 * Code pour regler le probleme de "Smooth" des dégradés selon les devices
	 */
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	/**
	 * Lancement du Tracker Google Analytics
	 */
	@Override
	public void onStart() {
		super.onStart();
		AnalyticsManager.start(this);
	}

	/**
	 * Fermeture du Tracker Google Analytics
	 */
	@Override
	public void onStop() {
		super.onStop();
		AnalyticsManager.stop(this);
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setTheme(R.style.Theme_ns);
		b = getIntent().getBundleExtra("bundle");
		this.setContentView(R.layout.main);
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// ouvrir le nouveau fragment
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		TotalSerieFragment vueSaisons = new TotalSerieFragment();
		vueSaisons.setArguments(b);

		ft.add(android.R.id.tabcontent, vueSaisons, "TotalSerie");
		ft.commit();
	}
}
