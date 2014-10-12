package com.valohyd.nextseries.views.fragments;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.DialogInfo;
import com.valohyd.nextseries.models.GetResearch;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.OurAsyncTask;
import com.valohyd.nextseries.utils.OurProgressDialog;

public class RechercherFragment extends SherlockFragment {
	/** la liste des resultats de la recherche (NomSerie, ID) **/
	private ArrayList<ArrayList<String>> listRessource;
	/** la listeView des series **/
	private ListView listSeries;
	/** la recherche effectuée **/
	private String recherche;
	/** besoin ou non d'afficher tous les resultats **/
	private boolean plus;
	/** Utilitaire **/
	public Helper helper = new Helper();
	/** Nom de la serie actuellement selectionnée **/
	public String currentName;
	/** ID de la serie actuellement selectionnée **/
	public String currentId;
	/** Boite de chargement **/
	public ProgressDialog dialog;
	/** Vue **/
	private View mainView;

	private TextView searchHelp;

	/** pour les performances **/
	FragmentActivity parent;

	/** Outil **/
	// private Helper help = new Helper();
	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(), AnalyticsManager.KEY_PAGE_RECHERCHE);
		AnalyticsManager.dispatch();
	}

	/** Called when the activity is first created. */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreateView(inflater, container, savedInstanceState);
		View mainView = inflater.inflate(R.layout.rechercher_layout, container, false);
		this.mainView = mainView;
		recherche = getArguments().getString("query");
		searchHelp = (TextView) mainView.findViewById(R.id.searchHelp);

		// obtenir la liste des resultats
		listSeries = (ListView) mainView.findViewById(R.id.recherche_liste);
		listSeries.setVisibility(View.GONE);

		// créer le dico ressource de la liste
		listRessource = new ArrayList<ArrayList<String>>();

		// garder le context pour les performances
		parent = getActivity();

		// onclick sur liste recherche
		listSeries.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				currentId = listRessource.get(position).get(0);
				currentName = listRessource.get(position).get(1);

				// si c'est "afficher + de res"
				if (listRessource
						.get(position)
						.get(0)
						.equals(RechercherFragment.this
								.getString(R.string.id_afficher_plus))) {
					plus = true;
					dialog = OurProgressDialog.showDialog(
							RechercherFragment.this.parent, "", getResources()
									.getString(R.string.loading), true);

					new BackgroundAsyncResearch().ourExecute();
					return;
				}

				// afficher le fragment de la serie
				//
				FragmentTransaction ft = RechercherFragment.this.parent
						.getSupportFragmentManager().beginTransaction();
				SerieFragment vueSerie = new SerieFragment();
				Bundle b = new Bundle();
				b.putString("id", currentId);
				vueSerie.setArguments(b);

				ft.add(android.R.id.tabcontent, vueSerie);
				ft.hide(RechercherFragment.this);
				ft.addToBackStack(null);
				ft.commit();
			}
		});
		performSearch();
		return mainView;
	}

	public void performSearch() {
		if (helper.getNetworkStatus(parent)) {
			dialog = OurProgressDialog.showDialog(parent, "", getResources()
					.getString(R.string.loading), true);
			new BackgroundAsyncResearch().ourExecute();
		}
	}

	public class BackgroundAsyncResearch extends
			OurAsyncTask<Void, Integer, Void> {
		boolean done = false;

		@Override
		protected void onPostExecute(Void result) {
			searchHelp.setVisibility(View.GONE);
			listSeries.setVisibility(View.VISIBLE);
			dialog.dismiss();
			if (listRessource != null && listRessource.size() == 0) {
				new DialogInfo(parent, "Désolé, aucune série de ce nom !", 1);
			}
			if (listRessource == null) {
				new DialogInfo(parent,
						"Désolé il y a eu une rreur, veuillez recommencer !", 1);
			}
			// affiche la liste
			listSeries.setAdapter(new SeriesAdapter(parent, listRessource));
		}

		@Override
		protected Void doInBackground(Void... params) {
			// lancer la recherche
			if (listRessource == null || listRessource.size() == 0) {
				GetResearch gr = new GetResearch(mainView.getContext(),
						recherche, plus);
				plus = false;
				listRessource = gr.getListe();
				gr = null;
			}

			return null;
		}
	}

	class SeriesAdapter extends BaseAdapter {

		ArrayList<ArrayList<String>> list;
		LayoutInflater inflater;

		public SeriesAdapter(Context context, ArrayList<ArrayList<String>> list) {
			inflater = LayoutInflater.from(context);
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
			return null;
		}

		public long getItemId(int pos) {
			return pos;
		}

		private class ViewHolder {
			TextView title;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.rechercher_item_renderer, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.nameSerie);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.title.setText(list.get(position).get(1));
			// take the Button and set listener. It will be invoked when you
			// click the button.
			ImageView btn = (ImageView) convertView
					.findViewById(R.id.addFavButton);
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Helper.ajouterFavoris(RechercherFragment.this.parent,
							listRessource.get(position).get(1), listRessource
									.get(position).get(0));
				}
			});
			if (list.get(position)
					.get(1)
					.equals(getResources().getString(
							R.string.message_recherche_plus))) {
				btn.setVisibility(View.GONE);
				((ImageView) convertView.findViewById(R.id.icon))
						.setVisibility(View.GONE);
			}
			if (Helper.isFavoris(mainView.getContext(),
					list.get(position).get(0))) {
				btn.setImageDrawable(getResources().getDrawable(
						R.drawable.favorites));
				btn.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO
					}
				});
			}
			return convertView;
		}
	}
}