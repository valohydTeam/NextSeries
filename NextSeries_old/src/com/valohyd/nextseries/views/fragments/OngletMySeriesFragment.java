package com.valohyd.nextseries.views.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.views.activities.StartActivity;

public class OngletMySeriesFragment extends SherlockFragment {
	/** choix de l'utilisateur par liste **/
	static final int VIEW_BY_LIST = 0;
	/** choix de l'utilisateur par pages (ViewPager) **/
	static final int VIEW_BY_PAGES = 1;

	private LayoutInflater inflater;
	private ViewGroup container;
	private View mainView;
	OnSharedPreferenceChangeListener listener;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		return doCreate();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (((StartActivity) getActivity()).widgetSerie != null) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getActivity());
			displayFavoris(prefs, ((StartActivity) getActivity()).widgetSerie);
		}
		Log.d("NextSeries", "OnResume MesSeries");

	}

	private View doCreate() {
		mainView = inflater.inflate(R.layout.onglet, container, false);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		listener = new OnSharedPreferenceChangeListener() {

			public void onSharedPreferenceChanged(
					SharedPreferences sharedPreferences, String key) {
				if (key.equals(getString(R.string.favoris_disp))) {
					displayFavoris(sharedPreferences, null);
				}
			}
		};
		prefs.registerOnSharedPreferenceChangeListener(listener);

		displayFavoris(prefs, null);

		return mainView;
	}

	private void displayFavoris(SharedPreferences sharedPreferences,
			String serie) {
		// préparer l'affichage de l'onglet
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		fm.beginTransaction();
		Fragment frag = null;
		//Si on vient du widget
		if (serie != null) {
			frag = new MySeriesFragmentViewPager(serie);
		} else {
			// récupérer et traiter le choix de vue (viewpager ou liste)
			int choix = Integer.parseInt(sharedPreferences.getString(
					getString(R.string.favoris_disp),
					String.valueOf(VIEW_BY_PAGES)));
			if (choix == VIEW_BY_LIST) {
				// ******************//
				// vue par liste //
				// ******************//
				Log.d("NS", "choix par liste");
				frag = new MySeriesFragmentList();

			} else {
				// ******************//
				// vue par ViewPager//
				// ******************//
				frag = new MySeriesFragmentViewPager(null);
			}
		}

		// ajouter l'onglet
		ft.replace(R.id.fragment_intern, frag);
		ft.commit();
	}
}