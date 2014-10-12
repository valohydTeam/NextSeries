package com.valohyd.nextseries.views.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.adapters.MySeriesListAdapter;
import com.valohyd.nextseries.adapters.PlanningAdapter;
import com.valohyd.nextseries.models.Episode;
import com.valohyd.nextseries.models.GetPlanning;
import com.valohyd.nextseries.models.Serie;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.OurAsyncTask;

public class MySeriesFragmentList extends SherlockFragment implements
		OnItemClickListener {
	/** la liste des series favorites de l'utilisateur */
	Serie[] listSeries;

	// pour l'inflate
	LayoutInflater inflater;
	ViewGroup container;
	Bundle savedInstanceState;

	/** garde l'acivity parent pour les performances */
	Activity parent;
	/** vue du fragment */
	View mainView;
	/** la listView des series */
	ListView listViewSerie;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		this.inflater = inflater;
		this.container = container;
		this.savedInstanceState = savedInstanceState;
		// garder une instance de l'activity parent pour améliorer les
		// performances
		parent = getActivity();
		return doCreate();
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(),
				AnalyticsManager.KEY_PAGE_MES_SERIES_LIST);
		AnalyticsManager.dispatch();
		Serie[] newList = Helper.getListFavoris(parent);
		if (listSeries != null && listSeries.length != newList.length) {
			// afficher la liste des series
			listSeries = newList;
			listViewSerie
					.setAdapter(new MySeriesListAdapter(listSeries, parent));
		}

	}

	private View doCreate() {
		Log.d("NextSeries", "OnCreate MySeries List");

		// inflate de la vue en page
		mainView = inflater.inflate(R.layout.my_series_liste_layout, container,
				false);

		// récupérer les éléments graphique
		listViewSerie = (ListView) mainView.findViewById(R.id.listeMesSeries);

		// récupérer la liste des series et l'afficher si la liste n'est pas
		// nulle
		if (listViewSerie != null) {
			// obtenir les series favorites
			listSeries = Helper.getListFavoris(parent);

			// ajouter le padding du haut à la listeView
			TextView padding = new TextView(this.parent);
			padding.setHeight(3);
			listViewSerie.addHeaderView(padding);
			listViewSerie.addFooterView(padding);

			// en cas de liste vide : afficher le message
			if (listSeries == null || listSeries.length == 0) {
				// afficher le message de non favoris
				TextView noFavoris = (TextView) mainView
						.findViewById(R.id.no_favoris);
				if (noFavoris != null)
					noFavoris.setVisibility(View.VISIBLE);

				// cacher la listView
				listViewSerie.setVisibility(View.GONE);
			} else {
				// afficher la liste des series
				listViewSerie.setAdapter(new MySeriesListAdapter(listSeries,
						parent));
			}
		}

		listViewSerie.setOnItemClickListener(this);
		return mainView;
	}

	/**
	 * clic sur un item de la liste de mesSeries => afficher le planning
	 */
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Log.d("NextSeries", "liste : on item click");
		// charger ou cacher le planning
		LinearLayout planning = (LinearLayout) view.findViewById(R.id.planning);
		if (planning != null) {
			// si visible => le cacher
			if (planning.getVisibility() == View.VISIBLE) {
				Log.d("NextSeries", "liste : cacher planning");
				planning.setVisibility(View.GONE);
			}
			// sinon
			else {
				// charger le planning si ce n'est deja fait
				if (planning.getTag() == null) {
					Log.d("NextSeries", "liste : get planning ");
					new BackgroundAsyncTaskPlanning(null, position, view)
							.ourExecute();
				}
				// sinon afficher directement le planning
				else {
					Log.d("NextSeries", "liste : afficher la liste");
					planning.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	/**
	 * Télécharge le planning et l'affiche
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class BackgroundAsyncTaskPlanning extends OurAsyncTask<Void, Integer, Void> {
		boolean done = false;
		int position;
		View view;

		public BackgroundAsyncTaskPlanning(ArrayList<AsyncTask<?, ?, ?>> tach,
				int pos, View v) {
			super();
			position = pos - 1; // -1 car vient de la listView (header en
								// plus..)
			view = v;
			Log.d("TACHES", "tache planning créée " + position);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// obtenir le planning de cette serie
			new GetPlanning(parent, parent.getString(R.string.URL_FREE)
					+ parent.getString(R.string.getPlanningWeeks),
					listSeries[position]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			LinearLayout planning = (LinearLayout) view
					.findViewById(R.id.planning);
			if (listSeries != null && listSeries.length != 0) {
				Log.d("NextSeries", "affichage du planning");
				try {
					Log.d("NextSeries", "nombre d'episode dans le planning : "
							+ listSeries[position].getEps().size());

					// ajouter les episodes
					LayoutInflater inflater = LayoutInflater
							.from(MySeriesFragmentList.this.parent);
					TextView saisonEp, numeroEp, nomEp, date, keyDate, dispoEp;
					for (Episode e : listSeries[position].getEps()) {
						// inflate la vue et la rempli
						View epView = inflater.inflate(R.layout.planning_item_episode_renderer,
								null);

						epView.findViewById(R.id.widgetSerie).setVisibility(
								View.GONE); // On cache le textview de la serie

						saisonEp = (TextView) epView
								.findViewById(R.id.widgetSaisonEpisode);
						numeroEp = (TextView) epView
								.findViewById(R.id.widgetNumeroEpisode);

						nomEp = (TextView) epView
								.findViewById(R.id.widgetTitreEpisode);
						keyDate = (TextView) epView
								.findViewById(R.id.widgetKeyDateEpisode);
						keyDate.setTextSize(11);
						date = (TextView) epView
								.findViewById(R.id.widgetJourEpisode);
						date.setTextSize(13);
						dispoEp = (TextView) epView
								.findViewById(R.id.widgetDispoEpisode);

						// Remplissage infos
						// Numero de l'episode
						saisonEp.setText(e.getNuméro().substring(0, 3));
						numeroEp.setText(e.getNuméro().substring(3, 6));

						// Titre de l'episode
						nomEp.setText(e.getTitre());

						// Date de l'episode
						SimpleDateFormat dateFormatter = new SimpleDateFormat(
								"EEEE dd MMMMM yyyy");
						Date dateEp;
						try {
							dateEp = dateFormatter.parse(e.getDate());
							SimpleDateFormat jourFormatter = new SimpleDateFormat(
									"EEEE");
							SimpleDateFormat restOfDateFormatter = new SimpleDateFormat(
									"dd/MM/yy");
							keyDate.setText(jourFormatter.format(dateEp));
							date.setText(restOfDateFormatter.format(dateEp));
						} catch (ParseException ex) {
							keyDate.setText("");
							date.setText("?");
						}

						// Disponibilité de l'episode
						PlanningAdapter.setProgress(
								MySeriesFragmentList.this.parent,
								e.getDaysLeft(), dispoEp);
						planning.addView(epView);
					}
					// animer et afficher le planning
					Animation planning_in = AnimationUtils.loadAnimation(
							parent, R.anim.fade_in);
					planning.startAnimation(planning_in);
					planning.setVisibility(View.VISIBLE);

					// ajouter un tag pour indiquer que le planning a été
					// téléchargé
					if (listSeries[position].getEps().size() > 0) {
						planning.setTag(getResources().getInteger(
								R.integer.planning_rempli));
					}
				} catch (Exception e) {
					Log.d("NextSeries", "Planning : Exception caught : " + e);
				}
			} else {
				Log.d("NextSeries", "pas d'affichage du planning");
				planning.setVisibility(View.GONE);
			}
		}
	}
}
