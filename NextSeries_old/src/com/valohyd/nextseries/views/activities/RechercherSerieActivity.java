package com.valohyd.nextseries.views.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.views.fragments.RechercherFragment;

public class RechercherSerieActivity extends SherlockFragmentActivity {
	private Bundle b;
	private SearchView searchView;

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

	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
		// Permet de fermer la searchView
		if (searchView != null)
			searchView.onActionViewCollapsed();
	}

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// ouvrir le nouveau fragment
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

		RechercherFragment vueRechercher = new RechercherFragment();
		vueRechercher.setArguments(b);

		ft.add(android.R.id.tabcontent, vueRechercher);
		ft.commit();
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.rechercher_menu, menu);
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
					// stocker la recherche dans un bundle
					b.putString("query", query);

					// cacher le clavier
					InputMethodManager inputManager = (InputMethodManager) RechercherSerieActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputManager.hideSoftInputFromWindow(
							RechercherSerieActivity.this.getCurrentFocus()
									.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
					searchView.onActionViewCollapsed();

					// relancer la recherche (rouvrir le fragment
					FragmentTransaction ft = getSupportFragmentManager()
							.beginTransaction();
					// ft.addToBackStack(null);

					RechercherFragment vueRechercher = new RechercherFragment();
					vueRechercher.setArguments(b);

					ft.replace(android.R.id.tabcontent, vueRechercher);
					ft.commit();
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
							// stocker la recherche dans un bundle
							b.putString("query", query);

							// cacher le clavier
							InputMethodManager inputManager = (InputMethodManager) RechercherSerieActivity.this
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							inputManager
									.hideSoftInputFromWindow(
											RechercherSerieActivity.this
													.getCurrentFocus()
													.getWindowToken(),
											InputMethodManager.HIDE_NOT_ALWAYS);
							searchView.onActionViewCollapsed();

							// relancer la recherche (rouvrir le fragment
							FragmentTransaction ft = getSupportFragmentManager()
									.beginTransaction();
							// ft.addToBackStack(null);

							RechercherFragment vueRechercher = new RechercherFragment();
							vueRechercher.setArguments(b);

							ft.replace(android.R.id.tabcontent, vueRechercher);
							ft.commit();
							return false;
						}

						public boolean onQueryTextChange(String newText) {
							// TODO Auto-generated method stub
							return false;
						}
					});
		}

		return true;
	}
}
