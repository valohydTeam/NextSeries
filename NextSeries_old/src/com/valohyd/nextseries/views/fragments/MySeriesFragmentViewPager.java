package com.valohyd.nextseries.views.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.jdom.Element;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.valohyd.android_utils.EasyShowDialogs;
import com.valohyd.nextseries.R;
import com.valohyd.nextseries.adapters.MesSeriesAdapter;
import com.valohyd.nextseries.adapters.PlanningAdapter;
import com.valohyd.nextseries.models.GetPlanning;
import com.valohyd.nextseries.models.Serie;
import com.valohyd.nextseries.utils.AnalyticsManager;
import com.valohyd.nextseries.utils.DownloadSerieZip;
import com.valohyd.nextseries.utils.Helper;
import com.valohyd.nextseries.utils.OurAsyncTask;
import com.valohyd.nextseries.utils.Unzipper;

public class MySeriesFragmentViewPager extends SherlockFragment {

	/** le ViewPager des series **/
	ViewPager pager;
	/** la liste ressource des series **/
	Serie[] listSerie;
	/** serie venant du widget **/
	String serieClicked;
	/** l'adapteur de la liste **/
	MesSeriesAdapter mesSeriesAdapter;
	/** pages de chaque series **/
	View[] vuesSeries;
	/** Vue principale **/
	View mainView;

	ArrayList<AsyncTask<?, ?, ?>> taches = new ArrayList<AsyncTask<?, ?, ?>>();
	Unzipper[] zip;
	boolean occupe = false;

	ArrayList<Integer> listChargees;
	boolean charge = false;
	ProgressDialog dialog;
	TextView hint;
	LinearLayout hintLayout;
	LinearLayout noFavoris;
	SeekBar selectBar;
	int largeurEcran;

	// pour l'inflate
	LayoutInflater inflater;
	ViewGroup container;
	Bundle savedInstanceState;

	/** garde l'acivity parent pour les performances */
	Activity parent;

	public MySeriesFragmentViewPager(String serie) {
		serieClicked = serie;
	}

	/** Called when the activity is first created. */
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
		setHasOptionsMenu(true);
		return doCreate();
	}

	@Override
	public void onResume() {
		super.onResume();
		AnalyticsManager.trackScreen(getActivity(),
				AnalyticsManager.KEY_PAGE_MES_SERIES_VIEW_PAGER);
		AnalyticsManager.dispatch();
		Log.d("NextSeries", "OnResume de MySeriesViewPager");
		refresh();
	}

	private View doCreate() {
		Log.d("NextSeries", "OnCreate MySeries");

		// inflate de la vue en page
		mainView = inflater.inflate(R.layout.my_series_pager_layout, container,
				false);
		largeurEcran = ((WindowManager) parent
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
				.getWidth();

		// obtenir la liste
		listChargees = new ArrayList<Integer>();

		// récupérer les éléments de l'interface
		pager = (ViewPager) mainView.findViewById(R.id.mes_series_pager);
		selectBar = (SeekBar) mainView.findViewById(R.id.selectBar);
		hint = (TextView) mainView.findViewById(R.id.hintScroll);
		hintLayout = (LinearLayout) mainView
				.findViewById(R.id.hintscroll_layout);
		noFavoris = (LinearLayout) mainView.findViewById(R.id.no_favoris);

		if (pager != null) {
			// cacher le view pager le temps du chargment
			pager.setVisibility(View.GONE);

			// gérer le changement de page du viewPager
			pager.setOnPageChangeListener(new OnPageChangeListener() {

				public void onPageSelected(int selectedPage) {
					// lancer les chargements des infos s'il n'est pas déjà
					// chargé
					if (!listChargees.contains(selectedPage)) {
						// afficher la dialog de chargement
						if (dialog == null || !dialog.isShowing())
							dialog = EasyShowDialogs
									.showProgressDialogInfinite(parent, null,
											getString(R.string.loading));

						// télécharger les infos de la serie
						new DownloadSerieInfos(taches, selectedPage)
								.ourExecute(
										parent,
										getString(R.string.URL_API)
												+ getString(R.string.API_KEY)
												+ "/series/"
												+ listSerie[selectedPage]
														.getId()
												+ getString(R.string.LANG_ZIP));

						// lancer le planning
						new BackgroundAsyncTaskPlanning(taches, selectedPage)
								.ourExecute();
						// lancer le dl du status & co
						new ShowStatusAndCo(taches, selectedPage).ourExecute();

						// ajouter la serie comme chargée
						listChargees.add(selectedPage);
					}
					// changer le niveau de scroll (seekbar)
					int ecartEntre2Series = selectBar.getMax()
							/ listSerie.length;
					selectBar.setProgress(ecartEntre2Series * selectedPage
							+ (ecartEntre2Series / 2));
				}

				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				public void onPageScrollStateChanged(int arg0) {
				}
			});

			// gérer la barre de selection
			if (selectBar != null) {
				selectBar
						.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

							public void onStopTrackingTouch(SeekBar seekBar) {
								// changer de page et cacher le titre indicateur
								pager.setCurrentItem(seekBar.getProgress()
										* listSerie.length / seekBar.getMax(),
										false);
								if (hintLayout != null)
									hintLayout.setVisibility(View.GONE);

							}

							public void onStartTrackingTouch(SeekBar seekBar) {
								// afficher le titre indicateur
								if (hintLayout != null)
									hintLayout.setVisibility(View.VISIBLE);
							}

							public void onProgressChanged(SeekBar seekBar,
									int progress, boolean fromUser) {
								// affciher le nom de la serie dans le titre
								// indicateur
								if (hint != null && hintLayout != null
										&& listSerie != null
										&& listSerie.length > 0
										&& seekBar != null) {

									// afficher le nom de la serie
									int index = progress * listSerie.length
											/ seekBar.getMax();

									// borner le max
									if (index >= listSerie.length)
										index = listSerie.length;

									if (index >= 0 && index < listSerie.length)
										hint.setText(listSerie[index]
												.getTitre());
								}
							}
						});
			}
			// // raffraichir
			// refresh();
		}
		return mainView;
	}

	@Override
	public void onDetach() {
		// arreter les taches en cas de quit
		if (taches != null) {
			Log.d("NextSeries", "arret des taches de fonds : " + taches.size());
			for (AsyncTask<?, ?, ?> as : taches)
				as.cancel(true);
			taches.clear();
			occupe = false;
		}
		super.onDetach();
	}

	public void refresh() {
		if (pager != null) {
			// indiquer le chargement en cours
			listChargees = new ArrayList<Integer>();
			occupe = true;
			charge = false;

			// obtenir les series favorites
			new GetFavorisAsync(taches).ourExecute();
			return;
		}
	}

	/**
	 * lance le téléchargement des infos (zip): -puis lance ShowSerieInfos
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class DownloadSerieInfos extends OurAsyncTask<Object, Integer, Element> {
		int position;

		public DownloadSerieInfos(ArrayList<AsyncTask<?, ?, ?>> tach, int pos) {
			super(tach);
			position = pos;
			Log.d("TACHES", "tache download serie info créée " + position);
		}

		@Override
		protected Element doInBackground(Object... params) {
			Log.d("TACHES", "tache en cours " + position);
			Element root = null;
			if (listSerie != null && zip != null && zip[position] != null) {
				if (listSerie[position].getBanner() == null
						|| listSerie[position].getBanner().equals("null")) {
					root = new DownloadSerieZip().downloadSerieInfos(
							(Context) params[0], (String) params[1], position);
				}
			}
			return root;
		}

		@Override
		protected void onPostExecute(Element root) {
			if (listSerie != null && zip != null && zip[position] != null) {
				Log.d("TACHES", "tache finie " + position);
				new ShowSerieInfos(taches, position).ourExecute(root);
			}
			super.onPostExecute(root);
		}
	}

	/**
	 * Telecharge le statut, la note et le nb d'épisodes, et les affiche
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class ShowStatusAndCo extends OurAsyncTask<Element, Integer, Object> {
		int position;

		public ShowStatusAndCo(ArrayList<AsyncTask<?, ?, ?>> tach, int pos) {
			super(tach);
			position = pos;
		}

		@Override
		protected Object doInBackground(Element... params) {
			Element root = new DownloadSerieZip().downloadSerieStatusAndCo(
					MySeriesFragmentViewPager.this.parent,
					getString(R.string.URL_FREE) + "getFavoris.php?id="
							+ listSerie[position].getId(), position);

			HashMap<String, String> s = zip[position]
					.getSerieInfosStatusAndCo(root);
			listSerie[position].setNbEp(s.get("nb_episodes"));
			listSerie[position].setNote(s.get("rating"));
			listSerie[position].setStatus(s.get("status"));

			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(Object res) {
			super.onPostExecute(res);
			// le status
			((TextView) vuesSeries[position].findViewById(R.id.statusSerie))
					.setText(zip[position].checkInfos(listSerie[position]
							.getStatus()));

			// la note
			((TextView) vuesSeries[position].findViewById(R.id.ratingSerie))
					.setText(zip[position].checkInfos(listSerie[position]
							.getNote()));

			// le nb d'ep
			((TextView) vuesSeries[position].findViewById(R.id.nbEpSerie))
					.setText(zip[position].checkInfos(listSerie[position]
							.getNbEp()));
		}

	}

	/**
	 * Afficher les infos de la serie - puis lance le dl de l'image en cas de
	 * nouvelle image (non en cache)
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class ShowSerieInfos extends OurAsyncTask<Element, Integer, Object> {
		int position;

		public ShowSerieInfos(ArrayList<AsyncTask<?, ?, ?>> tach, int pos) {
			super(tach);
			position = pos;
		}

		@Override
		protected Object doInBackground(Element... params) {
			if (listSerie != null && zip != null && zip[position] != null) {
				// si la série a été ajoutée sans toutes les infos : les ajouter
				// à la base sqlite
				if (listSerie[position].getBanner() == null
						|| listSerie[position].getBanner().equals("null")
						|| listSerie[position].getChannel().equals("NC")
						&& params[0] != null) {
					HashMap<String, String> s = zip[position]
							.getSerieInfos(params[0]);
					listSerie[position].setBanner(s.get("banner"));
					listSerie[position].setChannel(s.get("channel"));
					listSerie[position].setNbEp(s.get("nb_episodes"));
					listSerie[position].setNote(s.get("rating"));
					listSerie[position].setStatus(s.get("status"));

					// ajouter les infos manquantes au xml
					// TODO : plus les nbep, note et status (vient du web now),
					// changer sqlite
					Helper.retirerFavorisSilent(parent,
							listSerie[position].getTitre(),
							listSerie[position].getId());
					Helper.ajouterFavorisSilent(parent,
							listSerie[position].getTitre(),
							listSerie[position].getId(),
							listSerie[position].getChannel(),
							listSerie[position].getStatus(),
							listSerie[position].getNote(),
							listSerie[position].getNbEp(),
							listSerie[position].getBanner());
				}

				// charger l'image
				File input = new File(parent.getFilesDir().getPath()
						+ listSerie[position].getId() + ".png");

				// si elle n'est pas en cache : la télécharger
				if (!Helper.isInCache(input)) {
					Log.d("NextSeries", "Mise en cache de la bannière");
					new ShowImageTask(taches, position).ourExecute();
					// retourner une image bidon pour le moment
					return ((BitmapDrawable) MySeriesFragmentViewPager.this
							.getResources().getDrawable(R.drawable.unknown))
							.getBitmap();
				} else {
					// retourner l'image en cache
					Log.d("NextSeries", "Bannière en cache");
					ImageView ban = (ImageView) vuesSeries[position]
							.findViewById(R.id.banner);
					ban.setVisibility(View.VISIBLE);
					return (Helper.getImageFromCache(parent, input.getPath()));
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object bmp) {
			super.onPostExecute(bmp);
			if (listSerie != null && zip != null && zip[position] != null) {
				// afficher l'image
				ImageView ban = (ImageView) vuesSeries[position]
						.findViewById(R.id.banner);
				ban.setImageBitmap((Bitmap) bmp);

				// afficher les informations sur la page
				// le titre
				((TextView) vuesSeries[position].findViewById(R.id.titreSerie))
						.setText(zip[position].checkInfos(listSerie[position]
								.getTitre()
								+ zip[position].checkInfos(listSerie[position]
										.getChannel())));

				// le status
				((TextView) vuesSeries[position].findViewById(R.id.statusSerie))
						.setText(zip[position].checkInfos(listSerie[position]
								.getStatus()));

				// la note
				((TextView) vuesSeries[position].findViewById(R.id.ratingSerie))
						.setText(zip[position].checkInfos(listSerie[position]
								.getNote()));

				// le nb d'ep
				((TextView) vuesSeries[position].findViewById(R.id.nbEpSerie))
						.setText(zip[position].checkInfos(listSerie[position]
								.getNbEp()));

				// ajouter les clics sur les boutons
				// bouton enlever des favoris
				Button removFavButton = (Button) vuesSeries[position]
						.findViewById(R.id.removeFavBouton);
				removFavButton.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						// demander la confirmation de suppression des favoris
						EasyShowDialogs.showAlertDialogYesNo(parent,
								"Retirer des favoris", "Voulez vous supprimer "
										+ listSerie[position].getTitre()
										+ " de vos favoris ?",
								// si oui
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										Helper.retirerFavoris(parent,
												listSerie[position].getTitre(),
												listSerie[position].getId());
										refresh();
									}
								},
								// si non
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
									}
								});
					}
				});

				// bouton toutes les saisons
				Button seasonButton = (Button) vuesSeries[position]
						.findViewById(R.id.seasonButton);
				seasonButton.setOnClickListener(new OnClickListener() {
					public void onClick(View arg0) {
						// ouvrir le nouveau fragment
						FragmentManager fm = getFragmentManager();
						FragmentTransaction ft = fm.beginTransaction();

						// donner l'id de la serie et le nom
						TotalSerieFragment totalf = new TotalSerieFragment();
						Bundle extra = new Bundle();
						extra.putString("id", listSerie[position].getId());
						extra.putString("name", listSerie[position].getTitre());
						totalf.setArguments(extra);

						// remplacer le fragment actuel par le nouveau
						ft.add(android.R.id.tabcontent, totalf);
						ft.hide(MySeriesFragmentViewPager.this);
						ft.addToBackStack(null);
						ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
						ft.commit();
					}
				});

				if (!charge) {
					Log.d("TACHES", "setVisible " + position);
					noFavoris.setVisibility(View.GONE);
					pager.setVisibility(View.VISIBLE);
					Log.d("TACHES", "setAdapter " + position);
					mesSeriesAdapter = new MesSeriesAdapter(parent, vuesSeries);
					pager.setAdapter(mesSeriesAdapter);
					charge = true;
				}
				dialog.dismiss();

				// afficher l'image avec une animation
				Animation a = AnimationUtils.loadAnimation(parent,
						R.anim.flip_in);
				a.reset();
				((ImageView) vuesSeries[position].findViewById(R.id.banner))
						.clearAnimation();
				((ImageView) vuesSeries[position].findViewById(R.id.banner))
						.startAnimation(a);
			}
			occupe = false;
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

		public BackgroundAsyncTaskPlanning(ArrayList<AsyncTask<?, ?, ?>> tach,
				int pos) {
			super(tach);
			position = pos;
			Log.d("TACHES", "tache planning créée " + position);
		}

		@Override
		protected Void doInBackground(Void... params) {
			// obtenir le planning de cette serie
			new GetPlanning(parent, parent.getString(R.string.URL_FREE)
					+ parent.getString(R.string.getPlanningWeeks),
					listSerie[position]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			ListView planning = (ListView) vuesSeries[position]
					.findViewById(R.id.planning);
			if (listSerie != null && listSerie.length != 0) {
				Log.d("NextSeries", "affichage du planning");
				try {
					planning.setAdapter(new PlanningAdapter(parent,
							listSerie[position].getEps()));
					// TODO : metttre dans le xml la liste expandable et adapter
					Log.d("NextSeries", "nombre d'episode dans le planning : "
							+ listSerie[position].getEps().size());

					Animation planning_in = AnimationUtils.loadAnimation(
							parent, R.anim.fade_in);
					planning.startAnimation(planning_in);
					planning.setVisibility(View.VISIBLE);
					// PROCESS CLIC DU WIDGET
					if (serieClicked != null) {
						for (int i = 0; i < listSerie.length; i++) {
							if (listSerie[i].getTitre().equalsIgnoreCase(
									serieClicked)) {
								pager.setCurrentItem(i, false); // si on trouvé
																// la bonne
																// serie on
																// l'affiche
																// directement
							}
						}
						serieClicked = null; // annulation du clic
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

	/**
	 * Charge l'image et l'affiche
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class ShowImageTask extends OurAsyncTask<Integer, Integer, Object> {
		int posi;

		public ShowImageTask(ArrayList<AsyncTask<?, ?, ?>> tach, int pos) {
			super(tach);
			posi = pos;
		}

		@Override
		protected Object doInBackground(Integer... position) {
			Bitmap bmp = null;
			// charger l'image si on peut
			if (listSerie != null && zip != null && zip[posi] != null) {
				if (listSerie[posi].getBanner() != null) {
					bmp = Helper
							.LoadImageFromWebOperations(getString(R.string.URL_FREE)
									+ getString(R.string.url_dossier_top)
									+ listSerie[posi].getId() + ".jpg");
				}
			}
			return bmp;
		}

		@Override
		protected void onPostExecute(Object bmp) {
			// afficher l'image
			if (listSerie != null && zip != null && zip[posi] != null) {
				if (bmp != null) {
					File output = new File(parent.getFilesDir().getPath()
							+ listSerie[posi].getId() + ".png");
					View vue = vuesSeries[posi];
					ImageView banner = (ImageView) vue
							.findViewById(R.id.banner);
					banner.setImageBitmap((Bitmap) bmp);
					banner.setVisibility(View.VISIBLE);
					Helper.readDirectory(parent.getFilesDir().getPath(), false);
					Helper.putImageInCache(output, (Bitmap) bmp);
				}
			}
			super.onPostExecute(bmp);
		}
	}

	/**
	 * Charge les favoris
	 * 
	 * @author ValohydTeam
	 * 
	 */
	class GetFavorisAsync extends OurAsyncTask<Integer, Integer, Object> {

		public GetFavorisAsync(ArrayList<AsyncTask<?, ?, ?>> tach) {
			super(tach);
		}

		@Override
		protected Object doInBackground(Integer... position) {
			return Helper.getListFavoris(parent);
		}

		@Override
		protected void onPostExecute(Object result) {
			super.onPostExecute(result);
			// afficher le chargement si ce n'est déjà fait
			if (dialog == null || !dialog.isShowing())
				dialog = EasyShowDialogs.showProgressDialogInfinite(parent, "",
						getResources().getString(R.string.loading));
			listSerie = (Serie[]) result;
			// en cas de liste vide : afficher no favoris
			if (listSerie == null || listSerie.length == 0) {
				selectBar.setVisibility(View.GONE);
				listSerie = null;
				occupe = false;
				pager.setVisibility(View.GONE);
				noFavoris.setVisibility(View.VISIBLE);
			} else {
				// créer le tableau de vue et de zip
				vuesSeries = new View[listSerie.length];
				zip = new Unzipper[listSerie.length];
				for (int j = 0; j < listSerie.length; j++)
					zip[j] = new Unzipper();

				// enlever toutes les vues du pager
				pager.removeAllViews();

				// inflate les vues des toutes les series
				for (int j = 0; j < listSerie.length; j++) {
					vuesSeries[j] = inflater.inflate(
							R.layout.my_series_pager_page_renderer, null);
				}

				// charger la premiere serie
				new DownloadSerieInfos(taches, 0).ourExecute(parent,
						getString(R.string.URL_API)
								+ getString(R.string.API_KEY) + "/series/"
								+ listSerie[0].getId()
								+ getString(R.string.LANG_ZIP));
				// lancer le planning de la 1ere serie
				new BackgroundAsyncTaskPlanning(taches, 0).ourExecute();
				// lancer le dl du status & co de la 1ere serie
				new ShowStatusAndCo(taches, 0).ourExecute();

				listChargees.add(0);
				dialog.dismiss();
			}

		}
	}
}
