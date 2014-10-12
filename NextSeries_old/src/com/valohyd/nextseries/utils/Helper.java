package com.valohyd.nextseries.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.valohyd.nextseries.R;
import com.valohyd.nextseries.models.DialogInfo;
import com.valohyd.nextseries.models.Serie;

public class Helper {

	Boolean retour = false;
	Dialog dialog;

	public static void displayExceptionMessage(Context context, String msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static Bitmap LoadImageFromWebOperations(String url) {
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 4; // 1 = 100% if you write 4 means 1/4 = 25%
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			is.close();
			is = null;
			return bmp;
		} catch (MalformedURLException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
			return null;
		}
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	public static int GetDipsFromPixel(Context c, float pixels) {
		// Get the screen's density scale
		final float scale = c.getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	public boolean getNetworkStatus(Context c) {
		// check for wifi or 3g

		ConnectivityManager cm = (ConnectivityManager) c
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
			return true;
		}

		// set up dialog
		new DialogInfo(c, "Vous devez être connecté à internet !", 2);
		return retour;
	}

	public static boolean isInCache(File file) {
		return file.exists();
	}

	public static void putImageInCache(File file, Bitmap bmp) {
		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
			Log.d("CACHE", "FILE : " + out.toString());
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Bitmap getImageFromCache(Context c, String file) {
		FileInputStream fis = null;
		Bitmap bmp = null;
		try {
			fis = new FileInputStream(file);
			bmp = BitmapFactory.decodeStream(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			Log.d("NextSeries", "Fichier non trouvé : " + file);
			return ((BitmapDrawable) c.getResources().getDrawable(
					R.drawable.unknown)).getBitmap();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmp;
	}

	public static void readDirectory(String dir, boolean delete) {
		Log.d("NextSeries", "Debut lecture du dossier");
		File file = new File(dir);
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory() == true) {
					Log.d("NextSeries",
							"Dossier : " + files[i].getAbsolutePath());
				} else {
					Log.d("NextSeries", "Fichier : " + files[i].getName());
					if (delete) {
						files[i].delete();
						Log.d("NextSeries", "Fichier : " + files[i].getName()
								+ " effacé !");
					}
				}
				if (files[i].isDirectory() == true) {
					readDirectory(files[i].getAbsolutePath(), delete);
				}
			}
		}
		Log.d("NextSeries", "Fin lecture du dossier");
	}

	public static boolean isFavoris(Context c, String idSerie) {
		return isFavorisSqlite(c, idSerie);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/*
	 * AJOUT FAVORIS AU SERVEUR
	 */
	private static void ajouterServeur(Context c, String serie, String id,
			String statut, String note, String nb_ep) {
		// ajouter au serveur de la communauté
		String adresse = c.getString(R.string.URL_FREE) + "/addFavoris.php?";
		String adresse2 = c.getString(R.string.URL_FREE)
				+ "/getPlanningThisWeek.php?";
		adresse += "id=" + id + "&nom=" + serie + "&statut=" + statut
				+ "&note=" + note + "&nbep=" + nb_ep + "&ajout=true";
		adresse2 += "ids=" + id;
		adresse = adresse.replaceAll(" ", "%20");
		Log.d("NextSeries", "URL : " + adresse);
		ServerCom sv = new ServerCom(c, adresse);
		sv.ourExecute();
		new ServerCom(c, adresse2).ourExecute();
	}

	/** permet d'ajouter et supprimer les favoris au serveur **/
	public static class ServerCom extends OurAsyncTask<Void, Integer, Void> {
		boolean done = false;
		String adresse;
		Context c;

		public ServerCom(Context ctxt, String s) {
			super();
			c = ctxt;
			adresse = s;
		}

		@Override
		protected void onPostExecute(Void result) {
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Void doInBackground(Void... params) {
			// MAJ favoris
			try {
				Log.d("NextSeries", "URL : " + adresse);
				URL url = new URL(adresse);
				InputStream in = url.openStream();
				in.close();
			} catch (Exception e) {
				// displayExceptionMessage(c,
				// "Impossible de communiquer avec le serveur :" + e);
				Log.e("NextSeries",
						"Impossible de communiquer avec le serveur :" + e);
			}
			return null;
		}
	}

	public static void ajouterFavoris(Context c, String serie, String id) {
		ajouterFavoris(c, serie, id, "null", "null", "null", "null", "null");
	}

	/**
	 * Ajoute la serie au xml
	 * 
	 * @param serie
	 * @param id
	 * @param channel
	 * @param status
	 * @param note
	 * @param nbEp
	 */
	public static void ajouterFavoris(Context c, String serie, String id,
			String channel, String status, String note, String nbEp,
			String banner) {
		// ajouterFavorisBool(c, serie, id, channel, status, note, nbEp, banner,
		// true);
		ajouterFavorisBoolSqlite(c, new Serie(serie, id, note, status, channel,
				nbEp, banner), true);
	}

	/**
	 * ajoute la serie au favoris sans dialog avec l'user
	 * 
	 * @param c
	 * @param serie
	 * @param id
	 * @param channel
	 * @param status
	 * @param note
	 * @param nbEp
	 * @param banner
	 */
	public static void ajouterFavorisSilent(Context c, String serie, String id,
			String channel, String status, String note, String nbEp,
			String banner) {
		ajouterFavorisBoolSqlite(c, new Serie(serie, id, note, status, channel,
				nbEp, banner), false);
	}

	/**
	 * retire la serie sans dialog avec l'user
	 * 
	 * @param c
	 * @param serie
	 * @param id
	 */
	public static void retirerFavorisSilent(Context c, String serie, String id) {
		retirerFavorisBoolSqlite(c, serie, id, false);
	}

	/*
	 * RETIRER LES FAVORIS DU SERVEUR
	 */
	public static void retirerFavoris(Context c, String serie, String id) {
		retirerFavorisBoolSqlite(c, serie, id, true);
	}

	private static void retirerServeur(Context c, String serie, String id) {
		// supprimer du serveur
		String adresse = c.getString(R.string.URL_FREE) + "/addFavoris.php?";
		String adresse2 = c.getString(R.string.URL_FREE)
				+ "/getPlanningThisWeek.php?";
		adresse += "id=" + id + "&nom=" + serie + "&ajout=false";
		adresse2 += "ids=" + id;
		adresse = adresse.replaceAll(" ", "%20");
		Log.d("NextSeries", "URL : " + adresse);
		ServerCom sv = new ServerCom(c, adresse);
		sv.ourExecute();
		new ServerCom(c, adresse2).ourExecute();
	}

	// ---------------------------------------------
	// ---------------------------------------------
	// --------- Partie SQLITE ---------------------
	// ---------------------------------------------
	/**
	 * ajouter la serie à la base
	 * 
	 * @param c
	 *            : context
	 * @param s
	 *            : serie
	 * @param dialogue
	 *            : dialogue avec l'user ?
	 */
	private static void ajouterFavorisBoolSqlite(Context c, Serie s,
			boolean dialogue) {
		// obtenir l'objet de base
		OurSqliteBase baseLocale = new OurSqliteBase(c,
				c.getString(R.string.BASE_NAME), null, Integer.valueOf(c
						.getString(R.string.BASE_VERSION)));
		SQLiteDatabase bdd = baseLocale.getWritableDatabase();

		// si impossible d'ouvrir la base
		if (bdd == null) {
			if (dialogue)
				displayExceptionMessage(c,
						"impossible d'accéder au fichier des favoris");
			return;
		}

		// ajouter la serie
		ContentValues values = new ContentValues();
		values.put(c.getString(R.string.COL_ID), s.getId());
		values.put(c.getString(R.string.COL_BANNER), s.getBanner());
		values.put(c.getString(R.string.COL_CHAINE), s.getChannel());
		values.put(c.getString(R.string.COL_NB_EP), s.getNbEp());
		values.put(c.getString(R.string.COL_NOM_SERIE), s.getTitre());
		values.put(c.getString(R.string.COL_NOTE), s.getNote());
		values.put(c.getString(R.string.COL_STATUS), s.getStatus());

		bdd.insert(c.getString(R.string.TABLE_FAVORIS), null, values);

		if (dialogue)
			new DialogInfo(c, "Vous suivez maintenant " + s.getTitre() + " !",
					0);

		// ajouter au serveur
		ajouterServeur(c, s.getTitre(), s.getId(), s.getStatus(), s.getNote(),
				s.getNbEp());

		bdd.close();
	}

	/**
	 * retirer la serie de la base locale
	 * 
	 * @param c
	 * @param serie
	 * @param id
	 * @param dialog
	 */
	private static void retirerFavorisBoolSqlite(Context c, String serie,
			String id, boolean dialog) {
		// obtenir l'objet de base
		OurSqliteBase baseLocale = new OurSqliteBase(c,
				c.getString(R.string.BASE_NAME), null, Integer.valueOf(c
						.getString(R.string.BASE_VERSION)));
		SQLiteDatabase bdd = baseLocale.getWritableDatabase();

		// si impossible d'ouvrir la base
		if (bdd == null) {
			if (dialog)
				displayExceptionMessage(c,
						"impossible d'accéder au fichier des favoris");
			return;
		}

		// retirer de la base
		bdd.delete(c.getString(R.string.TABLE_FAVORIS),
				c.getString(R.string.COL_ID) + "=" + id, null);

		// retirer du serveur
		retirerServeur(c, serie, id);

		bdd.close();
	}

	/**
	 * regarde si la série est dans la base locale
	 * 
	 * @param idSerie
	 * @return
	 */
	public static boolean isFavorisSqlite(Context c, String idSerie) {
		if (!idSerie.equals(c.getString(R.string.id_afficher_plus))) {
			// obtenir l'objet de base
			OurSqliteBase baseLocale = new OurSqliteBase(c,
					c.getString(R.string.BASE_NAME), null, Integer.valueOf(c
							.getString(R.string.BASE_VERSION)));
			SQLiteDatabase bdd = baseLocale.getWritableDatabase();

			// si impossible d'ouvrir la base
			if (bdd == null)
				return false;

			// vérifier que la serie n'est pas deja présente
			String[] temp = new String[] { c.getString(R.string.COL_ID) };
			Cursor rs = bdd.query(c.getString(R.string.TABLE_FAVORIS), temp,
					c.getString(R.string.COL_ID) + "=" + idSerie, null, null,
					null, null);
			if (rs.getCount() > 0) {
				rs.close();
				bdd.close();
				return true;
			}
			rs.close();
			bdd.close();
		}
		return false;
	}

	/**
	 * retourne les ids des favoris (séparés par | )
	 * 
	 * @return les ids des favoris (séparés par | )
	 */
	public static String recupFavoris(Context c) {
		// obtenir l'objet de base
		OurSqliteBase baseLocale = new OurSqliteBase(c,
				c.getString(R.string.BASE_NAME), null, Integer.valueOf(c
						.getString(R.string.BASE_VERSION)));
		SQLiteDatabase bdd = baseLocale.getWritableDatabase();

		if (bdd == null)
			return "";

		// rechercher les favoris et construire le string résultat
		String[] cols = new String[] { c.getString(R.string.COL_ID) };
		Cursor rs = bdd.query(c.getString(R.string.TABLE_FAVORIS), cols, null,
				null, null, null, null);
		StringBuilder res = new StringBuilder();
		rs.moveToFirst();
		for (int i = 0; i < rs.getCount(); i++) {
			res.append(rs.getInt(0));
			res.append('|');
			rs.moveToNext();
		}
		rs.close();
		Log.d("NextSeries", "Planning : les ids : " + res.toString());

		bdd.close();
		return res.toString();
	}

	/**
	 * retourne les ids des favoris (séparés par | )
	 * 
	 * @return les ids des favoris (séparés par | )
	 */
	public static Serie[] getListFavoris(Context c) {
		// obtenir l'objet de base
		OurSqliteBase baseLocale = new OurSqliteBase(c,
				c.getString(R.string.BASE_NAME), null, Integer.valueOf(c
						.getString(R.string.BASE_VERSION)));
		SQLiteDatabase bdd = baseLocale.getWritableDatabase();

		if (bdd == null)
			return null;

		// rechercher les favoris et ajouter à la liste
		String[] cols = new String[] { c.getString(R.string.COL_NOM_SERIE),
				c.getString(R.string.COL_ID), c.getString(R.string.COL_CHAINE),
				c.getString(R.string.COL_BANNER) };

		Cursor rs = bdd.query(c.getString(R.string.TABLE_FAVORIS), cols, null,
				null, null, null, c.getString(R.string.COL_NOM_SERIE));

		if (rs.getCount() == 0) {
			rs.close();
			bdd.close();
			return null;
		}
		rs.moveToFirst();
		Serie[] res = new Serie[rs.getCount()];
		Log.d("NextSeries", "Series favorites : " + rs.getCount());
		for (int i = 0; i < rs.getCount(); i++) {
			res[i] = new Serie(rs.getString(0), rs.getString(1),
					rs.getString(2), rs.getString(3));
			rs.moveToNext();
		}
		rs.close();
		bdd.close();
		return res;
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}
}
