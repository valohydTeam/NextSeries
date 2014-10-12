package com.valohyd.nextseries.views.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.adapters.PlanningAdapter;
import com.valohyd.nextseries.adapters.TotalSeriesViewPagerAdapter;
import com.valohyd.nextseries.models.Episode;
import com.valohyd.nextseries.models.ViewPagerSeasonIndicator;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.OurAsyncTask;
import com.valohyd.nextseries.utils.OurProgressDialog;
import com.valohyd.nextseries.utils.Unzipper;

public class TotalSerieFragment extends SherlockFragment {

	private ArrayList<String> seasonList;
	private ListView listEp;
	private ViewPagerSeasonIndicator vpi;
	private ViewPager vp;
	private String currentId;
	private HashMap<Integer, ArrayList<Episode>> episodes = new HashMap<Integer, ArrayList<Episode>>();
	private Unzipper zip = new Unzipper();
	private LayoutInflater inflater;
	private View[] vues;
	private ArrayList<Integer> pagesChargees = new ArrayList<Integer>();
	private ProgressDialog dialog;

	/** pour les performances */
	FragmentActivity parentActivity;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		View mainView = inflater
				.inflate(R.layout.tous_episodes_layout, container, false);
		currentId = getArguments().getString("id");

		// garder l'activity parente pour les perfs
		parentActivity = getActivity();

		// obtenir l'inflater
		this.inflater = inflater;

		// obtenir le viewPager
		vpi = (ViewPagerSeasonIndicator) mainView
				.findViewById(R.id.viewPagerIndicatorTotalSeries);
		vp = (ViewPager) mainView.findViewById(R.id.viewpagerTotalSeries);

		// créer les pages
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			public void onPageSelected(int arg0) {
				Log.d("NextSeries", "page sélectionnée : " + arg0
						+ " et on est a :" + vpi.getSelectedItemPosition());
				if (vpi.getSelectedItemPosition() != arg0) {
					vpi.setSelection(arg0, true);
				}
			}

			public void onPageScrolled(int index, float pourcentage, int arg2) {
			}

			public void onPageScrollStateChanged(int arg0) {
			}
		});

		listEp = new ListView(parentActivity);// (ListView)findViewById(R.id.episodesList);
		listEp.setVisibility(View.GONE);
		dialog = OurProgressDialog.showDialog(parentActivity, "",
				getString(R.string.loading), true);

		new SeasonsAsyncTask().ourExecute();
		vpi.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log.d("NextSeries", "page a selectionner :" + arg2);
				new SelectionAsyncTask().ourExecute(arg2);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		return mainView;
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(),
				AnalyticsManager.KEY_PAGE_TOTAL_SERIE);
		AnalyticsManager.dispatch();
	}

	public class SelectionAsyncTask extends
			OurAsyncTask<Integer, Integer, Void> {
		boolean done = false;
		int index = -1;

		@Override
		protected void onPostExecute(Void result) {
			// si apres la pause c'est toujours le meme
			if (index != vpi.getSelectedItemPosition())
				return;

			if (vp.getCurrentItem() != index)
				vp.setCurrentItem(index);

			// if (!pagesChargees.contains(index)) {
			pagesChargees.add(index);
			episodes.put(index, new ArrayList<Episode>());
			// listEp.setVisibility(View.GONE);
			new EpisodesAsyncTask().ourExecute(seasonList.size() - index - 1);
			// }
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Integer... params) {
			index = params[0];
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				Log.e("NextSeries", "sleep impossible :" + e);
			}
			return null;
		}
	}

	public class SeasonsAsyncTask extends OurAsyncTask<Void, Integer, Void> {
		boolean done = false;

		@Override
		protected void onPostExecute(Void result) {
			// ga.setAdapter(new SeasonsAdapter(TotalSerieActivity.this,
			// seasonList));
			String[] s = new String[seasonList.size()];
			vues = new View[seasonList.size()];
			for (int i = 0; i < seasonList.size(); i++) {
				s[seasonList.size() - i - 1] = "Saison " + seasonList.get(i);
				vues[seasonList.size() - i - 1] = inflater.inflate(
						R.layout.total_serie_list, null);
			}
			vpi.setTitles(s);
			vp.setAdapter(new TotalSeriesViewPagerAdapter(parentActivity, vues));
			// afficher la premiere saison
			vpi.performItemClick(null, 0, 0);
			dialog.dismiss();
		}

		@Override
		protected void onPreExecute() {
			// Toast.makeText(TotalSerieActivity.this,
			// "Chargement des saisons ...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (seasonList == null)
				seasonList = zip.getSeasons(parentActivity,
						getString(R.string.URL_API)
								+ getString(R.string.API_KEY) + "/series/"
								+ currentId + getString(R.string.LANG_ZIP));
			return null;
		}

	}

	public class EpisodesAsyncTask extends OurAsyncTask<Integer, Integer, Void> {
		boolean done = false;
		int index = 0;

		@Override
		protected void onPostExecute(Void result) {
			if (episodes.containsKey(index) && episodes.get(index).size() != 0) {
				listEp = (ListView) vues[index].findViewById(R.id.episodesList);
				listEp.setVisibility(View.VISIBLE);
				listEp.setAdapter(new PlanningAdapter(parentActivity, episodes
						.get(index)));
				listEp.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// ouvrir le nouveau fragment
						FragmentTransaction ft = parentActivity
								.getSupportFragmentManager().beginTransaction();

						EpisodeFragment vueEpisode = new EpisodeFragment();
						Bundle b = new Bundle();
						b.putString("idSerie", currentId);
						b.putString("episodeImage",
								episodes.get(index).get(position).getImage());
						b.putString("serieTitre",
								getArguments().getString("name"));
						b.putString("episodeTitre",
								episodes.get(index).get(position).getTitre());
						b.putString("episodeNum",
								episodes.get(index).get(position).getNuméro());
						b.putString("episodeDate",
								episodes.get(index).get(position).getDate());
						b.putString("episodeRating",
								episodes.get(index).get(position).getRating());
						b.putString("episodeResume",
								episodes.get(index).get(position).getResume());
						vueEpisode.setArguments(b);

						// ft.detach(TotalSerieFragment.this);
						ft.add(android.R.id.tabcontent, vueEpisode);
						ft.hide(TotalSerieFragment.this);
						ft.addToBackStack(null);
						ft.commit();
					}
				});
			} else {
				listEp = (ListView) vues[index].findViewById(R.id.episodesList);
				listEp.setVisibility(View.GONE);
				TextView empty = (TextView) vues[index]
						.findViewById(R.id.empty);
				if (empty != null)
					empty.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPreExecute() {
			// Toast.makeText(TotalSerieActivity.this,
			// "Chargement des épisodes ...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected Void doInBackground(Integer... params) {
			Log.d("NextSeries", "asyncTask : saison n°" + params[0]);
			index = seasonList.size() - 1 - params[0];
			if (!episodes.containsKey(index) || episodes.get(index).size() == 0)
				episodes.put(index, zip.getEpisodes(params[0]));
			return null;
		}
	}
}