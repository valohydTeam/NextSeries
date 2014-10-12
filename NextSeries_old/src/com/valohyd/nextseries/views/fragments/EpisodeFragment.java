package com.valohyd.nextseries.views.fragments;

import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.OurAsyncTask;
import com.valohyd.nextseries.utils.ScrollingTextView;

public class EpisodeFragment extends SherlockFragment {

	private String currentSerieTitre, currentEpisodeTitre, currentEpisodeImage,
			currentEpisodeRating, currentEpisodeResume, currentEpisodeActeurs,
			currentEpisodeNum, currentEpisodeDate;
	private TextView date, acteurs, serie, numero;
	private ScrollingTextView titre;
	private TextView resume;
	private ImageView image;
	private RatingBar rating;

	private ArrayList<AsyncTask<?, ?, ?>> taches = new ArrayList<AsyncTask<?, ?, ?>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View mainView = inflater.inflate(R.layout.episode_layout, container, false);

		// récupérer les infos de la série
		Bundle b = getArguments();
		currentSerieTitre = b.getString("serieTitre");
		currentEpisodeTitre = b.getString("episodeTitre");
		currentEpisodeImage = b.getString("episodeImage");
		currentEpisodeRating = b.getString("episodeRating");
		currentEpisodeResume = b.getString("episodeResume");
		currentEpisodeNum = b.getString("episodeNum");
		currentEpisodeDate = b.getString("episodeDate");

		// récupérer les éléments de l'UI
		image = (ImageView) mainView.findViewById(R.id.imageEpisode);
		serie = (TextView) mainView.findViewById(R.id.titreSerieEpisode);
		titre = (ScrollingTextView) mainView.findViewById(R.id.nomEpisodeSerie);
		date = (TextView) mainView.findViewById(R.id.dateEpisode);
		rating = (RatingBar) mainView.findViewById(R.id.ratingEpisode);
		resume = (TextView) mainView.findViewById(R.id.resumeEpisode);

		// rempli l'UI
		fillEpisode();
		
		// afficher l'image de l'épisode (chargement en background)
		new ShowImageTask(taches).ourExecute();

		return mainView;
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(),
				AnalyticsManager.KEY_PAGE_EPISODE);
	}

	@Override
	public void onDetach() {
		Log.d("NextSeries", "arret des taches de fonds : " + taches.size());
		for (AsyncTask<?, ?, ?> as : taches)
			as.cancel(true);
		taches.clear();
		super.onDetach();
	}

	public void fillEpisode() {
		// titre de la serie
		serie.setText(currentSerieTitre);
		
		// titre de l'épisode
		titre.setText(currentEpisodeNum + " : " + currentEpisodeTitre);
		
		// date de l'épisode
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
		Date dateObj = null;
		try {
			dateObj = curFormater.parse(currentEpisodeDate);
			SimpleDateFormat postFormater = new SimpleDateFormat("dd MMMM yyyy");

			String newDateStr = postFormater.format(dateObj);
			date.setText(newDateStr);
		} catch (ParseException e) {
			// afficher la date par défaut ("NC")
			e.printStackTrace();
			date.setText("NC");
		}

		// afficher le résumé
		resume.setText(currentEpisodeResume);
		
		// afficher la note de l'épisode
		try {
			Log.d("NextSeries", "Rating : " + currentEpisodeRating);
			rating.setRating((Float.parseFloat(currentEpisodeRating) / 5) * 2);
		} catch (NumberFormatException nbfe) {
			rating.setRating(100);
			Log.d("NextSeries",
					"FillEpisode : aucune info sur la note de l'episode");
			rating.setVisibility(View.GONE);
		}
	}

	private Bitmap LoadImageFromWebOperations(String url) {
		if (url != null) {
			try {
				InputStream is = (InputStream) new URL(url).getContent();
				Drawable d = Drawable.createFromStream(is, "src name");
				Bitmap bmp = ((BitmapDrawable) d).getBitmap();
				return bmp;
			} catch (Exception e) {
				Log.e("ERROR",
						"Impossible de charger l'image à partir de l'url : "
								+ url);
				return null;
			}
		} else {
			return null;
		}
	}

	class ShowImageTask extends OurAsyncTask<Integer, Integer, Object> {

		public ShowImageTask(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected void onPostExecute(Object bmp) {
			super.onPostExecute(bmp);
			if (bmp != null) {
				Bitmap bm = Helper.getRoundedCornerBitmap((Bitmap) bmp, 5);
				image.setImageBitmap((Bitmap) bm);
			}
		}

		@Override
		protected Object doInBackground(Integer... position) {
			Bitmap bmp = null;
			if (currentEpisodeImage != null) {
				bmp = LoadImageFromWebOperations("http://www.thetvdb.com/banners/"
						+ currentEpisodeImage);
			}
			return bmp;
		}
	}
}
