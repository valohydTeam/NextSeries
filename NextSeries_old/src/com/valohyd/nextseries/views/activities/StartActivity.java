package com.valohyd.nextseries.views.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.valohyd.android_utils.tabs.CompatTab;
import com.valohyd.android_utils.tabs.InstantiatingTabListener;
import com.valohyd.android_utils.tabs.TabCompatActivity;
import com.valohyd.android_utils.tabs.TabHelper;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.Logger;
import com.valohyd.nextseries.views.fragments.HomeFragment;
import com.valohyd.nextseries.views.fragments.OngletMySeriesFragment;

@SuppressLint("NewApi")
public class StartActivity extends TabCompatActivity {
	private SharedPreferences prefs;
	private TabHelper tabHelper;
	public static StartActivity instance;
	private Bundle b;
	private Intent i;
	private SearchView searchView;
	public String widgetSerie;
	/** choix de l'utilisateur par liste **/
	static final int VIEW_BY_LIST = 0;
	/** choix de l'utilisateur par pages (ViewPager) **/
	static final int VIEW_BY_PAGES = 1;
	private static final String INTENT_KEY_SERIE = "serie";

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Nextseries);
		instance = this;
		setContentView(R.layout.main);
		b = new Bundle();
		i = new Intent(this, RechercherSerieActivity.class);

		// create the tabs
		tabHelper = getTabHelper();

		CompatTab homeTab = tabHelper
				.newTab("Accueil")
				.setText(R.string.Tab_home)
				.setTabListener(
						new InstantiatingTabListener(this, HomeFragment.class));
		homeTab.setIcon(R.drawable.tab_home);
		tabHelper.addTab(homeTab);

		CompatTab mySeriesTab = tabHelper
				.newTab("Mes series")
				.setText(R.string.Tab_my_series)
				.setTabListener(
						new InstantiatingTabListener(this,
								OngletMySeriesFragment.class));
		mySeriesTab.setIcon(R.drawable.tab_series);
		tabHelper.addTab(mySeriesTab);
	}

	/**
	 * getter de prefs
	 * 
	 * @return
	 */
	public SharedPreferences getPrefs() {
		return prefs;
	}

	@Override
	public void onResume() {
		super.onResume();
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String serie = extras.getString(INTENT_KEY_SERIE);
			if (serie != null) {
				widgetSerie = serie;
				tabHelper.setCurrentTab(1);// mes series
			}
		}
		// Permet de fermer la searchView
		if (searchView != null)
			searchView.onActionViewCollapsed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);
		MenuItem searchItem = menu.findItem(R.id.itemSearch);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			final SearchView searchView = (SearchView) searchItem
					.getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setQueryHint("Titre (VO)");
			searchView.setOnQueryTextListener(new OnQueryTextListener() {

				public boolean onQueryTextSubmit(String query) {
					b.putString("query", query);
					// ouvrir le nouveau fragment
					i.putExtra("bundle", b);
					InputMethodManager inputManager = (InputMethodManager) StartActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(StartActivity.this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					searchView.onActionViewCollapsed();
					startActivity(i);
					return false;
				}

				public boolean onQueryTextChange(String newText) {
					// TODO Auto-generated method stub
					return false;
				}
			});
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			final android.widget.SearchView searchView = (android.widget.SearchView) searchItem
					.getActionView();
			searchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
			searchView.setQueryHint("Titre (VO)");
			searchView
					.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {

						public boolean onQueryTextSubmit(String query) {
							b.putString("query", query);
							// ouvrir le nouveau fragment
							i.putExtra("bundle", b);
							InputMethodManager inputManager = (InputMethodManager) StartActivity.this
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							inputManager.hideSoftInputFromWindow(
									StartActivity.this.getCurrentFocus()
											.getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
							searchView.onActionViewCollapsed();
							startActivity(i);
							return false;
						}

						public boolean onQueryTextChange(String newText) {
							// TODO Auto-generated method stub
							return false;
						}
					});
		}

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		int choix = Integer
				.parseInt(prefs.getString(getString(R.string.favoris_disp),
						String.valueOf(VIEW_BY_PAGES)));
		MenuItem item = menu.findItem(R.id.itemByList);
		if (choix == VIEW_BY_LIST) {
			item.setIcon(getResources().getDrawable(R.drawable.ic_menu_pager));

		}
		if (choix == VIEW_BY_PAGES) {
			item.setIcon(getResources().getDrawable(R.drawable.ic_menu_list));

		}
		return true;
	}

	@Override
	public void onBackPressed() {
		FragmentManager fm = getSupportFragmentManager();
		if (fm.getBackStackEntryCount() > 0
				&& getTabHelper().getCurrentTab() == 1) {
			// si l'onglet selectionné est l'onglet client : retourner en
			// arrière
			super.onBackPressed();
		} else
			finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.display_settings:
				Intent i = new Intent(this, SettingsActivity.class);
				startActivity(i);
				break;
			case R.id.exit:
				finish();
				System.exit(0);
				break;
			case R.id.itemByList:
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(this);

				// récupérer et traiter le choix de vue (viewpager ou liste)
				int choix = Integer.parseInt(prefs.getString(
						getString(R.string.favoris_disp),
						String.valueOf(VIEW_BY_PAGES)));
				Log.d("NextSeries", "Preference Liste : " + choix);

				if (choix == VIEW_BY_LIST) {
					Editor edit = prefs.edit();
					edit.clear();
					edit.putString(getString(R.string.favoris_disp),
							String.valueOf(VIEW_BY_PAGES));
					edit.commit();

				}
				if (choix == VIEW_BY_PAGES) {
					Editor edit = prefs.edit();
					edit.clear();
					edit.putString(getString(R.string.favoris_disp),
							String.valueOf(VIEW_BY_LIST));
					edit.commit();

				}
				invalidateOptionsMenu();
				break;
			}
		} catch (Exception e) {
			Logger.printQuenelle(e.getMessage());
		}
		return true;
	}

	@Override
	public void finishFromChild(Activity child) {
		Log.d("NextSeries", "FinishFromChild TabWidget");
		if (tabHelper.getCurrentTab() == 0)
			finish();
		else
			tabHelper.setCurrentTab(0);
	}
}