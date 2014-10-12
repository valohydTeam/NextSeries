package com.valohyd.nextseries.adapters;

import java.io.File;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.Serie;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.OurAsyncTask;

public class MySeriesListAdapter implements ListAdapter {
	/** liste des series à afficher */
	Serie[] lesSeries;
	/** context de l'application */
	Context context;

	/**
	 * Constructeur prenant la liste des series
	 * 
	 * @param lesSeries
	 */
	public MySeriesListAdapter(Serie[] lesSeries, Context context) {
		this.lesSeries = lesSeries;
		this.context = context;
	}

	public int getCount() {
		return lesSeries.length;
	}

	public Object getItem(int position) {
		if (position >= 0 && position < lesSeries.length)
			return lesSeries[position];
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View ligne = convertView;

		// holder utile pour garder les infos de la case dans chaque case
		SerieHolder holder = null;

		// si premier création
		if (ligne == null) {
			// inflate la vue
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ligne = inflater.inflate(R.layout.my_series_list_item_renderer, parent,
					false);

			// créer le holder
			holder = new SerieHolder();
			holder.txtTitle = (TextView) ligne.findViewById(R.id.TitreSerie);
			holder.banner = (ImageView) ligne.findViewById(R.id.item_banner);
			new ShowImageTask(holder.banner, lesSeries[position].getId())
					.ourExecute();

			ligne.setTag(holder);
		} else {
			holder = (SerieHolder) ligne.getTag();
		}

		holder.txtTitle.setText(lesSeries[position].getTitre());

		return ligne;
	}

	static class SerieHolder {
		ImageView banner;
		TextView txtTitle;
	}

	public int getViewTypeCount() {
		return lesSeries.length;
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isEmpty() {
		return lesSeries.length == 0;
	}

	public void registerDataSetObserver(DataSetObserver observer) {
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEnabled(int arg0) {
		return true;
	}

	/**
	 * Charge l'image et l'affiche
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class ShowImageTask extends OurAsyncTask<Integer, Integer, Object> {

		ImageView img;
		String id;

		public ShowImageTask(ImageView img, String id) {
			this.img = img;
			this.id = id;
		}

		@Override
		protected Object doInBackground(Integer... position) {
			// charger l'image
			File input = new File(context.getFilesDir().getPath() + id + ".png");
			// si elle n'est pas en cache : la télécharger
			if (!Helper.isInCache(input)) {
				Log.d("NextSeries", "Mise en cache de la bannière");
				Bitmap bmp = Helper.LoadImageFromWebOperations(context
						.getString(R.string.URL_FREE)
						+ context.getString(R.string.url_dossier_top)
						+ id
						+ ".jpg");
				File output = new File(context.getFilesDir().getPath() + id
						+ ".png");
				Helper.readDirectory(context.getFilesDir().getPath(), false);
				Helper.putImageInCache(output, (Bitmap) bmp);
				// retourner l'image
				return bmp;
			} else {
				// retourner l'image en cache
				Log.d("NextSeries", "Bannière en cache");
				return (Helper.getImageFromCache(context, input.getPath()));
			}
		}

		@Override
		protected void onPostExecute(Object bmp) {
			if (bmp != null) {
				img.setImageBitmap((Bitmap) bmp);
			}
			super.onPostExecute(bmp);
		}
	}
}
