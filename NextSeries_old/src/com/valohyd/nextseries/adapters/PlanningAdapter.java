package com.valohyd.nextseries.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.Episode;

public class PlanningAdapter extends BaseAdapter {

	ArrayList<Episode> list;
	LayoutInflater inflater;
	Context context;

	public PlanningAdapter(Context context, ArrayList<Episode> list) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list;
	}

	public int getCount() {
		if (list != null)
			return list.size();
		else
			return 0;
	}

	public Object getItem(int itemPos) {
		if (list != null)
			return list.get(itemPos);
		else
			return null;
	}

	public long getItemId(int pos) {
		return pos;
	}

	private class ViewHolder {
		TextView saisonEpisode,numeroEpisode;
		TextView titreEpisode;
		
		TextView keyDateEpisode, dispoEpisode, jourEpisode;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
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
			holder.keyDateEpisode.setTextSize(11);
			holder.dispoEpisode = (TextView) convertView
					.findViewById(R.id.widgetDispoEpisode);
			holder.jourEpisode = (TextView) convertView
					.findViewById(R.id.widgetJourEpisode);
			holder.jourEpisode.setTextSize(13);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// Numero de l'episode
		holder.saisonEpisode.setText(list.get(position).getNuméro().substring(0, 3));
		holder.numeroEpisode.setText(list.get(position).getNuméro().substring(3, 6));

        // Titre de l'episode
		String titre = list.get(position).getTitre();
		holder.titreEpisode.setText(titre.length()==0?"TBA":titre);

        // Date de l'episode
		SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE dd MMMMM yyyy");
		Date dateEp;
		try {
			dateEp = dateFormatter.parse(list.get(position).getDate());
			SimpleDateFormat jourFormatter = new SimpleDateFormat("EEEE");
			SimpleDateFormat restOfDateFormatter = new SimpleDateFormat("dd/MM/yy");
	        holder.keyDateEpisode.setText(jourFormatter.format(dateEp));
	        holder.jourEpisode.setText(restOfDateFormatter.format(dateEp));
		} catch (ParseException e) {
			holder.keyDateEpisode.setVisibility(View.GONE);
	        holder.jourEpisode.setVisibility(View.GONE);
		}
		

        // Disponibilité de l'episode
        setProgress(context,list.get(position).getDaysLeft(), holder.dispoEpisode);
		return convertView;
	}

    public static void setProgress(Context ctxt, String progress, View row) {
        if (progress == null || progress.length()==0){
        	row.setVisibility(View.GONE);
            return;
        }
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
