package com.valohyd.nextseries.views.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.valohyd.nextseries.R;
import com.loopj.android.image.SmartImageView;
import com.valohyd.nextseries.adapters.PlanningGroupAdapter;
import com.valohyd.nextseries.adapters.ViewPagerAdapter;
import com.valohyd.nextseries.models.GetPlanning;
import com.valohyd.nextseries.models.Serie;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.Logger;
import com.valohyd.nextseries.utils.OurAsyncTask;
import com.valohyd.nextseries.utils.TopSeries;
import com.valohyd.nextseries.utils.Unzipper;

public class HomeFragment extends SherlockFragment {

	private View mainView;

	GetPlanning gp;
	ProgressBar progressBar;
	ViewPagerAdapter topAdapter;
	ExpandableListView planning;
	TextView topEmpty, planningEmpty;
	SharedPreferences pref;
	ArrayList<Serie> listSerie, listTopSeries;
	TopSeries topItems;
	Unzipper[] zip;
	ArrayList<AsyncTask<?, ?, ?>> taches = new ArrayList<AsyncTask<?, ?, ?>>();
	ViewFlipper MyViewFlipper;
	/** choix de l'utilisateur par liste **/
	static final int VIEW_BY_LIST = 0;
	/** choix de l'utilisateur par pages (ViewPager) **/
	static final int VIEW_BY_PAGES = 1;

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		mainView = inflater.inflate(R.layout.accueil_layout, container, false);
		MyViewFlipper = (ViewFlipper) mainView.findViewById(R.id.viewflipper);
		MyViewFlipper.setVisibility(View.GONE);
		pref = PreferenceManager
				.getDefaultSharedPreferences(this.getActivity());
		progressBar = (ProgressBar) mainView.findViewById(R.id.progressBar);
		planningEmpty = (TextView) mainView.findViewById(R.id.planningEmpty);
		topEmpty = (TextView) mainView.findViewById(R.id.rssEmpty);
		planning = (ExpandableListView) mainView.findViewById(R.id.planning);
		planning.setVisibility(View.GONE);
		topEmpty.setVisibility(View.GONE);
		planningEmpty.setVisibility(View.GONE);

		MyViewFlipper.setFlipInterval(6000);
		Animation animationFlipIn = AnimationUtils.loadAnimation(getActivity(),
				R.anim.slide_in);
		animationFlipIn.setDuration(1000);
		Animation animationFlipOut = AnimationUtils.loadAnimation(
				getActivity(), R.anim.slide_out);
		animationFlipOut.setDuration(1000);
		MyViewFlipper.setInAnimation(animationFlipIn);
		MyViewFlipper.setOutAnimation(animationFlipOut);

		new BackgroundAsyncTaskTop(getActivity()).ourExecute();

		setHasOptionsMenu(true);

		return mainView;
	}

	@Override
	public void onPause() {
		super.onPause();
		MyViewFlipper.stopFlipping();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Logger.print("HomeFragment : onDestroy called");
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(),
				AnalyticsManager.KEY_PAGE_HOME);
		AnalyticsManager.dispatch();
		MyViewFlipper.startFlipping();
		new BackgroundAsyncTaskPlanning(getActivity()).ourExecute();
	}

	/**
	 * affiche ou non l'item de choix d'affichage des series favorites
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		if (menu != null && menu.findItem(R.id.itemByList) != null) {
			menu.findItem(R.id.itemByList).setVisible(isHidden());
		}
	}

	/**
	 * permet de dire de redessiner le menu
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			getActivity().invalidateOptionsMenu();
		}
	}

	/**
	 * Permet de construire la vue des tops
	 * 
	 * @author parodi
	 * 
	 */
	public class BackgroundAsyncTaskTop extends
			OurAsyncTask<Void, Integer, Void> {
		boolean done = false;

		private Context context;

		public BackgroundAsyncTaskTop(Context context) {
			this.context = context;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (topItems != null) {
				topItems.getBanners();
				progressBar.setVisibility(View.GONE);

				SmartImageView img1 = (SmartImageView) mainView
						.findViewById(R.id.top_image1);
				topItems.setBanner(0, img1);

				SmartImageView img = (SmartImageView) mainView
						.findViewById(R.id.top_image1);
				img = (SmartImageView) mainView.findViewById(R.id.top_image2);
				topItems.setBanner(1, img);

				img = (SmartImageView) mainView.findViewById(R.id.top_image3);
				topItems.setBanner(2, img);

				img = (SmartImageView) mainView.findViewById(R.id.top_image4);
				topItems.setBanner(3, img);

				img = (SmartImageView) mainView.findViewById(R.id.top_image5);
				topItems.setBanner(4, img);
				while (img1.getDrawable().equals(
						context.getResources().getDrawable(R.drawable.unknown))) {
					progressBar.setVisibility(View.VISIBLE);
					MyViewFlipper.setVisibility(View.GONE);
				}
				MyViewFlipper.setVisibility(View.VISIBLE);
				MyViewFlipper.startFlipping();
			} else {
				progressBar.setVisibility(View.GONE);
				topEmpty.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			topItems = new TopSeries(context, getString(R.string.URL_FREE)
					+ getString(R.string.getFavoris), 5);
			return null;
		}
	}

	public class BackgroundAsyncTaskPlanning extends
			OurAsyncTask<Void, Integer, Void> {
		boolean done = false;

		private Context context;

		public BackgroundAsyncTaskPlanning(Context context) {
			this.context = context;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (listSerie != null && listSerie.size() != 0) {
				try {
					planning.setAdapter(new PlanningGroupAdapter(context,
							listSerie));
					planning.setIndicatorBounds(
							((WindowManager) getActivity().getSystemService(
									Context.WINDOW_SERVICE))
									.getDefaultDisplay().getWidth()
									- Helper.GetDipsFromPixel(getActivity(), 35),
							((WindowManager) getActivity().getSystemService(
									Context.WINDOW_SERVICE))
									.getDefaultDisplay().getWidth()
									- Helper.GetDipsFromPixel(getActivity(), 5));

					planning.setVisibility(View.VISIBLE);
					planningEmpty.setVisibility(View.GONE);
				} catch (Exception e) {
					Logger.printQuenelle("Planning : Exception caught : " + e);
				}
			} else {
				planning.setVisibility(View.GONE);
				planningEmpty.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			gp = new GetPlanning(context, getString(R.string.URL_FREE)
					+ getString(R.string.getPlanningThisWeek));
			listSerie = gp.getListe();
			return null;
		}
	}
}