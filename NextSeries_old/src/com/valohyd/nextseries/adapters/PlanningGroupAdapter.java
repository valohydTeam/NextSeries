package com.valohyd.nextseries.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.Episode;
import com.valohyd.nextseries.models.Serie;

public class PlanningGroupAdapter extends BaseExpandableListAdapter {

	private Context context;
	private ArrayList<Serie> groupes;
	private LayoutInflater inflater;

	public PlanningGroupAdapter(Context context, ArrayList<Serie> groupes) {
		this.context = context;
		this.groupes = groupes;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	public Object getChild(int gPosition, int cPosition) {
		return groupes.get(gPosition).getEps().get(cPosition);
	}

	public long getChildId(int gPosition, int cPosition) {
		return cPosition;
	}

	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final Episode ep = (Episode) getChild(groupPosition, childPosition);

		ChildViewHolder holder;
		if (convertView == null) {
			holder = new ChildViewHolder();
			convertView = inflater.inflate(
					R.layout.planning_item_episode_renderer, null);

			convertView
			.findViewById(R.id.widgetSerie).setVisibility(View.GONE); //On cache le textview de la serie
			
			holder.saisonEpisode = (TextView) convertView
					.findViewById(R.id.widgetSaisonEpisode);
			holder.numeroEpisode = (TextView) convertView
					.findViewById(R.id.widgetNumeroEpisode);
			holder.titreEpisode = (TextView) convertView
					.findViewById(R.id.widgetTitreEpisode);
			holder.keyDateEpisode = (TextView) convertView
					.findViewById(R.id.widgetKeyDateEpisode);
			holder.dispoEpisode = (TextView) convertView
					.findViewById(R.id.widgetDispoEpisode);
			holder.jourEpisode = (TextView) convertView
					.findViewById(R.id.widgetJourEpisode);

			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		// Numero de l'episode
		holder.saisonEpisode.setText(ep.getNuméro().substring(0, 3));
		holder.numeroEpisode.setText(ep.getNuméro().substring(3, 6));

        // Titre de l'episode
		holder.titreEpisode.setText(ep.getTitre());

		// Date de l'episode
        String key = ep.getDaysLeft().substring(0, 1);
        if (key.equals("-")) {
        	holder.keyDateEpisode.setText("Dans");
        } else {
        	holder.keyDateEpisode.setText("Il y a");
        }
        holder.jourEpisode.setText(ep.getDaysLeft().substring(1) + " j");
		

        // Disponibilité de l'episode
        setProgress(context,ep.getDaysLeft(), holder.dispoEpisode);
		return convertView;
	}

	public int getChildrenCount(int gPosition) {
		return groupes.get(gPosition).getEps().size();
	}

	public Object getGroup(int gPosition) {
		return groupes.get(gPosition);
	}

	public int getGroupCount() {
		return groupes.size();
	}

	public long getGroupId(int gPosition) {
		return gPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder gholder;

		Serie serie = (Serie) getGroup(groupPosition);

		if (convertView == null) {
			gholder = new GroupViewHolder();

			convertView = inflater.inflate(
					R.layout.planning_item_serie_renderer, null);

			gholder.serie = (TextView) convertView
					.findViewById(R.id.titrePlanning);

			convertView.setTag(gholder);
		} else {
			gholder = (GroupViewHolder) convertView.getTag();
		}

		gholder.serie.setText(serie.getTitre());
		ExpandableListView eLV = (ExpandableListView) parent;
		eLV.expandGroup(groupPosition);
		return convertView;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}

	class GroupViewHolder {
		public TextView serie;
	}

	class ChildViewHolder {
		TextView saisonEpisode,numeroEpisode;
		TextView titreEpisode;
		
		TextView keyDateEpisode, dispoEpisode, jourEpisode;
	}

	   public static void setProgress(Context ctxt, String progress, View row) {
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
	            row.setBackgroundColor(ctxt.getResources().getColor(R.color.holo_green_dark));
	            break;
	        case -1:
	        case -2:
	        case -3:
	        	row.setBackgroundColor(ctxt.getResources().getColor(R.color.holo_orange_dark));
	            break;
	        case -4:
	        case -5:
	        case -6:
	        case -7:
	        	row.setBackgroundColor(ctxt.getResources().getColor(R.color.holo_red_dark));
	            break;
	        default:
	        	row.setBackgroundColor(ctxt.getResources().getColor(R.color.holo_red_dark));
	            Log.e("NextSeries", "aucune image correspondant à " + days);
	        }
	    }

}
