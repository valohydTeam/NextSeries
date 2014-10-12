package com.valohyd.nextseries;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.Episode;
import com.valohyd.nextseries.models.GetPlanning;
import com.valohyd.nextseries.models.Serie;
import com.valohyd.nextseries.services.ListWidgetService;

public class ListViewsFactory implements RemoteViewsService.RemoteViewsFactory {
	ArrayList<Pair<String, Episode>> liste = new ArrayList<Pair<String, Episode>>();
	private Context ctxt = null;
	private GetPlanning gp;

	boolean isSection = false;
	String previousSerie = "";

	public ListViewsFactory(Context ctxt, Intent intent) {
		Log.d("NextSeries-Widget", "Constructeur ListViewFactory");
		this.ctxt = ctxt;
	}

	public void onCreate() {
		Log.d("NextSeries-Widget", "OnCreate ListViewFactory");
	}

	public void onDestroy() {
		Log.d("NextSeries-Widget", "OnDestroy ListViewFactory");
	}

	public int getCount() {
		return ((liste == null) ? 0 : liste.size());
	}

	public RemoteViews getViewAt(int position) {

		if (position == 0 || !previousSerie.equals(liste.get(position).first)) {
			isSection = true;
		} else {
			isSection = false;
		}
		Log.d("NextSeries", "Construction vue position " + position
				+ " section ? " + isSection);

		// On prend la vue de la cellule
		RemoteViews row = new RemoteViews(ctxt.getPackageName(),
				R.layout.planning_item_episode_renderer);

		// On entre les infos recupérées
		row.setTextViewText(R.id.widgetSerie, liste.get(position).first);

		// Numero de l'episode
		row.setTextViewText(R.id.widgetSaisonEpisode,
				liste.get(position).second.getNuméro().substring(0, 3));
		row.setTextViewText(R.id.widgetNumeroEpisode,
				liste.get(position).second.getNuméro().substring(3, 6));

		// Titre de l'episode
		row.setTextViewText(R.id.widgetTitreEpisode,
				liste.get(position).second.getTitre());

		// Date de l'episode
		String key = liste.get(position).second.getDaysLeft().substring(0, 1);
		if (key.equals("-")) {
			row.setTextViewText(R.id.widgetKeyDateEpisode, "Dans");
		} else {
			row.setTextViewText(R.id.widgetKeyDateEpisode, "Il y a");
		}
		row.setTextViewText(R.id.widgetJourEpisode, liste.get(position).second
				.getDaysLeft().substring(1) + " j");

		// Disponibilité de l'episode
		setProgress(liste.get(position).second.getDaysLeft(), row);

		// Next, set a fill-intent, which will be used to fill in the pending intent template
        // that is set on the collection view in StackWidgetProvider.
        Bundle extras = new Bundle();
        extras.putString(ListWidgetProvider.EXTRA_SERIE, liste.get(position).first);
        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);
        // Make it possible to distinguish the individual on-click
        // action of a given item
        row.setOnClickFillInIntent(R.id.widgetLayoutEpisode, fillInIntent);
		
		
		if (!isSection) {
			row.setViewVisibility(R.id.widgetSerie, View.GONE);
		} else {
			row.setViewVisibility(R.id.widgetSerie, View.VISIBLE);
		}
		previousSerie = liste.get(position).first;
		return row;
	}

	public void setProgress(String progress, RemoteViews row) {
		if (progress == null)
			return;
		int days = 0;
		if (progress.charAt(0) == '+') {
			days = Integer.parseInt(progress.substring(1, progress.length()));
		} else
			days = -Integer.parseInt(progress.substring(1, progress.length()));
		Log.d("NextSeries", "Widget : setProgress de " + days);
		switch (days) {
		case 7:
		case 6:
		case 5:
		case 4:
		case 3:
		case 2:
		case 1:
		case 0:
			row.setInt(R.id.widgetDispoEpisode, "setBackgroundColor", ctxt
					.getResources().getColor(R.color.holo_green_dark));
			break;
		case -1:
		case -2:
		case -3:
			row.setInt(R.id.widgetDispoEpisode, "setBackgroundColor", ctxt
					.getResources().getColor(R.color.holo_orange_dark));
			break;
		case -4:
		case -5:
		case -6:
		case -7:
			row.setInt(R.id.widgetDispoEpisode, "setBackgroundColor", ctxt
					.getResources().getColor(R.color.holo_red_dark));
			break;
		default:
			row.setInt(R.id.widgetDispoEpisode, "setBackgroundColor", ctxt
					.getResources().getColor(R.color.holo_red_dark));
			Log.e("NextSeries", "aucune image correspondant à " + days);
		}
	}

	public RemoteViews getLoadingView() {
		return null;
	}

	public int getViewTypeCount() {
		return liste.size();
	}

	public long getItemId(int position) {
		return position;
	}

	public boolean hasStableIds() {
		return true;
	}

	public void onDataSetChanged() {
		// TODO mettre dans les prefs (settings & co)
		boolean choixByDate = true;

		// tester le choix d'affichage du planning
		if (choixByDate) {
			ArrayList<Pair<String, Episode>> items = new ArrayList<Pair<String, Episode>>();
			// choix par date : récupérer les episodes et ne pas les trier par
			// serie
			gp = new GetPlanning(ctxt, ctxt.getString(R.string.URL_FREE)
					+ ctxt.getString(R.string.getPlanningByDate), true);
			items = gp.getListeByDate();

			// créer la liste des episodes qui s'afficheront sur le planning
			liste.clear();
			if (items != null) {
				for (Pair<String, Episode> ep : items) {
					liste.add(ep);
				}
			}
		} else {
			ArrayList<Serie> items = new ArrayList<Serie>();
			// récupérer le planning et les trier par serie
			gp = new GetPlanning(ctxt, ctxt.getString(R.string.URL_FREE)
					+ ctxt.getString(R.string.getPlanningThisWeek));
			items = gp.getListe();

			// créer la liste des episodes qui s'afficheront sur le planning
			liste.clear();
			if (items != null) {
				for (Serie s : items) {
					for (Episode ep : s.getEps()) {
						liste.add(new Pair<String, Episode>(s.getTitre(), ep));
					}
				}
			}
		}
		((ListWidgetService) ctxt).stopSelf();

	}

}