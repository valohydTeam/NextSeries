package com.valohyd.nextseries.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import android.content.Context;
import android.util.Log;

import com.valohyd.nextseries.R;
import com.loopj.android.image.SmartImageView;
import com.valohyd.nextseries.models.Serie;

public class TopSeries {
	// attributs
	private Element racine;
	private Document document;
	private ArrayList<Serie> liste = new ArrayList<Serie>();;
	int nbTop;
	ArrayList<String> nbFollowers = new ArrayList<String>();
	HashMap<Integer, String> banners = new HashMap<Integer, String>();
	Context context;
	SAXBuilder sxb;

	public TopSeries(Context c, String chemin, int nbTop) {
		this.nbTop = nbTop;
		this.context = c;

		sxb = new SAXBuilder();
		try {
			// se connecter au xml
			document = sxb.build(chemin);
			racine = document.getRootElement();
			liste = new ArrayList<Serie>();
		} catch (Exception e) {
			Log.e("NextSeries", "Top : Exception caught : " + e);
			// Helper.displayExceptionMessage(c,
			// "Impossible d'accéder aux données\nConnexion internet requise ! ");
			return;
		}
		if (racine == null || document == null) {
			return;
		}
		fillTop();
		sxb = null;
	}

	private void fillTop() {
		int cpt = 0;
		// affiche les résultat de la recherche dans la liste
		Iterator<Element> i = null;
		List<Element> l = racine.getChildren("serie");
		if (l.size() == 0) {
			// Helper.displayExceptionMessage(c, "Aucun résultat trouvé");
			return;
		}
		i = l.iterator();

		// parcourir le xml et ajouter les resultats
		Serie s;
		Logger.print("Top : parcourt du fichier distant");
		while (i.hasNext() && cpt < nbTop) {
			Element courant = i.next();
			s = new Serie(courant.getChildText("nom"),
					courant.getChildText("id"));
			Logger.print("Top : ajout de la serie " + s.getTitre()
					+ " à la liste des tops");
			liste.add(s);
			cpt++;
		}
	}

	public ArrayList<Serie> getListe() {
		return liste;
	}

	public void setBanner(final int position, SmartImageView imageView) {
		imageView.setImageUrl(banners.get(position));
	}

	/**
	 * Recupere les bannieres
	 */
	public void getBanners() {
		for (int i = 0; i < liste.size(); i++) {
			banners.put(
					i,
					context.getString(R.string.URL_FREE)
							+ context.getString(R.string.url_dossier_top)
							+ liste.get(i).getId() + ".jpg");
		}
	}
}
