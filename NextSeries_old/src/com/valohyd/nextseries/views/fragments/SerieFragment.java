package com.valohyd.nextseries.views.fragments;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.jdom.Element;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.adapters.ViewPagerSerieAdapter;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.DownloadSerieZip;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.OurAsyncTask;
import com.valohyd.nextseries.utils.OurProgressDialog;
import com.valohyd.nextseries.utils.Unzipper;

public class SerieFragment extends SherlockFragment {

	// constantes
	private Unzipper zip = new Unzipper();
	public Helper helper = new Helper();
	private HashMap<String, String> infos;
	private ImageView banner, addFavButton, image_last, image_next;
	private String currentId;
	private TextView titre, rating, status, last_ep, next_ep, nb_ep;
	private EditText resume;
	private ImageView seasonButton;
	private LayoutInflater inflater;
	private ViewPagerSerieAdapter vpAdapter;
	private ViewPager pager;
	private View layout1;
	private View layout2;
	private ArrayList<AsyncTask<?, ?, ?>> taches = new ArrayList<AsyncTask<?, ?, ?>>();
	private Builder dialogSupp;
	/** Boite de chargement **/
	private ProgressDialog dialog;
	private boolean dejaCharge = false;

	/** pour les performances */
	FragmentActivity parentActivity;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View mainView = inflater.inflate(R.layout.info_serie_layout, container, false);

		this.inflater = inflater;

		pager = (ViewPager) mainView.findViewById(R.id.viewpager);
		if (!dejaCharge) {
			pager.setVisibility(View.GONE);
			currentId = getArguments().getString("id");
			refresh();
		} else {
			pager.setVisibility(View.VISIBLE);
			banner.setVisibility(View.VISIBLE);
			pager.setAdapter(vpAdapter);
		}
		dejaCharge = true;
		parentActivity = getActivity();
		return mainView;
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(),
				AnalyticsManager.KEY_PAGE_SERIE_RECHERCHE);
		AnalyticsManager.dispatch();
	}

	/*
	 * @Override protected void onNewIntent(Intent i) { super.onNewIntent(i);
	 * setContentView(R.layout.serie); pager = (ViewPager)
	 * findViewById(R.id.viewpager); pager.setVisibility(View.GONE); currentId =
	 * i.getStringExtra("id"); refresh(); }
	 */

	@Override
	public void onDetach() {
		Log.d("NextSeries", "arret des taches de fonds : " + taches.size());
		for (AsyncTask<?, ?, ?> as : taches)
			as.cancel(true);
		taches.clear();
		super.onDetach();
	}

	private void refresh() {
		if (parentActivity == null)
			parentActivity = getActivity();
		if (helper.getNetworkStatus(parentActivity)) {
			layout1 = inflater.inflate(R.layout.info_serie_page1_renderer, null);
			layout2 = inflater.inflate(R.layout.info_serie_page2_renderer, null);

			banner = (ImageView) layout1.findViewById(R.id.banner);
			image_last = (ImageView) layout2.findViewById(R.id.imageLast);
			image_next = (ImageView) layout2.findViewById(R.id.imageNext);
			titre = (TextView) layout1.findViewById(R.id.titreSerie);
			status = (TextView) layout1.findViewById(R.id.statusSerie);
			rating = (TextView) layout1.findViewById(R.id.ratingSerie);
			nb_ep = (TextView) layout1.findViewById(R.id.nbEpSerie);
			resume = (EditText) layout1.findViewById(R.id.resumeSerie);
			dialogSupp = new AlertDialog.Builder(parentActivity);
			seasonButton = (ImageView) layout1.findViewById(R.id.seasonButton);
			addFavButton = (ImageView) layout1
					.findViewById(R.id.addFavButtonSerie);
			if (Helper.isFavoris(parentActivity, "" + currentId)) {
				setStatusFavoris(true);
			} else {
				setStatusFavoris(false);
			}
			seasonButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Intent i = new Intent(parentActivity,
							TotalSerieFragment.class);
					i.putExtra("id", currentId);

					// ouvrir le nouveau fragment
					FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();

					// donner l'id de la serie
					TotalSerieFragment totalf = new TotalSerieFragment();
					Bundle extra = new Bundle();
					extra.putString("id", currentId);
					totalf.setArguments(extra);

					// remplacer le fragment actuel par la page web
					ft.add(android.R.id.tabcontent, totalf);
					ft.hide(SerieFragment.this);
					ft.addToBackStack(null);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					ft.commit();
				}
			});

			last_ep = (TextView) layout2.findViewById(R.id.last_ep);
			next_ep = (TextView) layout2.findViewById(R.id.next_ep);
			dialog = OurProgressDialog.showDialog(parentActivity, "",
					getResources().getString(R.string.loading), true);

			new DownloadSerieInfos(taches).ourExecute(parentActivity,
					getString(R.string.URL_API) + getString(R.string.API_KEY)
							+ "/series/" + currentId
							+ getString(R.string.LANG_ZIP));
		}
	}

	private void setStatusFavoris(boolean isFav) {
		if (isFav) {
			addFavButton.setImageDrawable(getResources().getDrawable(
					R.drawable.removefav));
			addFavButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					dialogSupp
							.setMessage(
									"Voulez vous supprimer " + titre.getText()
											+ " de vos favoris ?")
							.setCancelable(false)
							.setPositiveButton("Oui",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											Helper.retirerFavoris(
													parentActivity,
													zip.checkInfos(infos
															.get("name")),
													currentId);
											setStatusFavoris(false);
										}
									})
							.setNegativeButton("Non",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					dialogSupp.create();
					dialogSupp.show();
				}
			});
		} else {
			addFavButton.setImageDrawable(getResources().getDrawable(
					R.drawable.favorite2));
			addFavButton.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					Helper.ajouterFavoris(parentActivity,
							zip.checkInfos(infos.get("name")), currentId,
							infos.get("channel"), infos.get("status"),
							infos.get("rating"), infos.get("nb_episodes"),
							infos.get("banner"));
					setStatusFavoris(true);
				}
			});
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
				Log.d("NextSeries", "Erreur load banniere : " + e.getMessage());
				return null;
			}
		} else {
			return null;
		}
	}

	private void setLayout(int orientation) {
		final int res = R.layout.info_serie_page2_renderer;
		layout2 = inflater.inflate(res, null);
		if (infos != null) {
			last_ep = (TextView) layout2.findViewById(R.id.last_ep);
			next_ep = (TextView) layout2.findViewById(R.id.next_ep);
			last_ep.setText(zip.checkInfos(infos.get("last_ep")));
			next_ep.setText(zip.checkInfos(infos.get("next_ep")));
			Drawable d1 = image_last.getDrawable();
			Drawable d2 = image_next.getDrawable();
			image_last = (ImageView) layout2.findViewById(R.id.imageLast);
			image_last.setImageDrawable(d1);
			image_next = (ImageView) layout2.findViewById(R.id.imageNext);
			image_next.setImageDrawable(d2);
		}
		vpAdapter = new ViewPagerSerieAdapter(parentActivity, layout1, layout2);
		pager.setAdapter(vpAdapter);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setLayout(newConfig.orientation);
	}

	class DownloadSerieInfos extends OurAsyncTask<Object, Integer, Element> {

		public DownloadSerieInfos(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected Element doInBackground(Object... params) {
			Element root = new DownloadSerieZip().downloadSerieInfos(
					(Context) params[0], (String) params[1], 0);
			return root;
		}

		@Override
		protected void onPostExecute(Element root) {
			new ShowSerieInfos(taches).ourExecute(root);
			super.onPostExecute(root);
		}

	}

	class ShowSerieInfos extends OurAsyncTask<Element, Integer, Object> {

		public ShowSerieInfos(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected Object doInBackground(Element... params) {
			infos = zip.getSerieInfos(params[0]);
			File input = new File(getString(R.string.CACHE_DIR) + currentId
					+ ".png");
			if (Helper.isInCache(input)) {
				Log.d("NextSeries", "Bannière en cache");
				banner.setImageBitmap(Helper.getImageFromCache(parentActivity,
						input.getPath()));

			} else {
				Log.d("NextSeries", "Mise en cache de la bannière");
				new File(getString(R.string.CACHE_DIR)).mkdirs();
				new ShowImageTask(taches).ourExecute();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object obj) {
			dialog.dismiss();
			banner.setVisibility(View.VISIBLE);
			titre.setText(zip.checkInfos(infos.get("name"))
					+ zip.checkInfos(infos.get("channel")));
			Log.d("SERIEACTIVITY", "titre : " + titre.getText());
			status.setText("Status : " + zip.checkInfos(infos.get("status")));
			rating.setText("Note : " + zip.checkInfos(infos.get("rating")));
			nb_ep.setText("Episodes : "
					+ zip.checkInfos(infos.get("nb_episodes")));
			resume.setText(zip.checkInfos(infos.get("resume")));
			last_ep.setText(zip.checkInfos(infos.get("last_ep")));
			next_ep.setText(zip.checkInfos(infos.get("next_ep")));
			vpAdapter = new ViewPagerSerieAdapter(parentActivity, layout1,
					layout2);

			pager.setVisibility(View.VISIBLE);
			pager.setAdapter(vpAdapter);

			super.onPostExecute(obj);
		}
	}

	class ShowImageTask extends OurAsyncTask<Integer, Integer, Object> {

		public ShowImageTask(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected void onPostExecute(Object bmp) {
			if (bmp != null) {
				try {
					File output = new File(parentActivity.getFilesDir().getPath() + currentId + ".png");
					banner.setImageBitmap((Bitmap) bmp);
					banner.setScaleType(ImageView.ScaleType.FIT_CENTER);
					// Helper.listDirectory(getString(R.string.CACHE_DIR));
					// Log.d("DIR", getFilesDir().getPath());
					Helper.putImageInCache(output, (Bitmap) bmp);
				} catch (Exception e) {
					Log.d("NextSeries", "Erreur de mise en cache");
				}
			}
			vpAdapter = new ViewPagerSerieAdapter(parentActivity, layout1,
					layout2);

			pager.setVisibility(View.VISIBLE);
			pager.setAdapter(vpAdapter);
			new ShowImageEpisodeLastTask(taches).ourExecute();
			new ShowImageEpisodeNextTask(taches).ourExecute();
			super.onPostExecute(bmp);
		}

		@Override
		protected Object doInBackground(Integer... position) {
			Bitmap bmp = null;
			if (infos.get("banner") != null) {
				Log.d("NextSeries", "Telechargement de la bannière");
				bmp = LoadImageFromWebOperations("http://www.thetvdb.com/banners/"
						+ infos.get("banner"));
			}
			return bmp;
		}
	}

	class ShowImageEpisodeLastTask extends
			OurAsyncTask<Integer, Integer, Object> {

		public ShowImageEpisodeLastTask(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected void onPostExecute(Object bmp) {
			if (bmp != null) {
				Bitmap bm = Helper.getRoundedCornerBitmap((Bitmap) bmp, 10);
				image_last.setImageBitmap(bm);
				image_last.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			super.onPostExecute(bmp);
		}

		@Override
		protected Object doInBackground(Integer... position) {
			Bitmap bmp = null;
			if (infos.get("pic_episode_last") != null) {
				bmp = LoadImageFromWebOperations("http://www.thetvdb.com/banners/"
						+ infos.get("pic_episode_last"));
			}
			return bmp;
		}
	}

	class ShowImageEpisodeNextTask extends
			OurAsyncTask<Integer, Integer, Object> {

		public ShowImageEpisodeNextTask(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected void onPostExecute(Object bmp) {
			if (bmp != null) {
				Bitmap bm = Helper.getRoundedCornerBitmap((Bitmap) bmp, 10);
				image_next.setImageBitmap(bm);
				image_next.setScaleType(ImageView.ScaleType.FIT_CENTER);
			}
			super.onPostExecute(bmp);
		}

		@Override
		protected Object doInBackground(Integer... position) {
			Bitmap bmp = null;
			if (infos.get("pic_episode_next") != null) {
				bmp = LoadImageFromWebOperations("http://www.thetvdb.com/banners/"
						+ infos.get("pic_episode_next"));
			}
			return bmp;
		}
	}
}