package com.valohyd.nextseries.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import android.content.Context;

import com.valohyd.nextseries.R;

public class GetResearch {
    // constantes
    // private static final String APIKEY = "F28A515607A21CA9";
    // private static final String URL_API = "http://www.thetvdb.com/api/";
    private static final String SEARCH_REQUEST = "GetSeries.php?seriesname=";
    private static final int NB_RESULTAT = 10;

    // attributs
    private Element racine;
    private Document document;
    private ArrayList<ArrayList<String>> liste;

    public GetResearch(Context c, String research, boolean entier) {
        SAXBuilder sxb = new SAXBuilder();
        try {
            // se connecter au xml
            research = research.replaceAll(" ", "%20");
            document = sxb.build(c.getString(R.string.URL_API) + SEARCH_REQUEST
                    + research);
            racine = document.getRootElement();
            liste = new ArrayList<ArrayList<String>>();
        } catch (Exception e) {
//            Helper.displayExceptionMessage(c,
//                    "Impossible d'accéder aux données\nConnexion internet requise ! ");
            return;
        }
        if(racine == null || document == null){
            return;
        }
        fillSearch(c, entier);
        freeSpace();
        sxb = null;
    }

    @SuppressWarnings("unchecked")
    private void fillSearch(Context c, boolean entier) {
        // affiche les résultat de la recherche dans la liste
        Iterator<Element> i = null;
        List<Element> l = racine.getChildren("Series");
        if(l.size() == 0){
//            Helper.displayExceptionMessage(c, "Aucun résultat trouvé");
            return;
        }
        i = l.iterator();
        
        //compteur pour limiter la liste
        int cpt = 0;
        //parcourir le xml et ajouter les resultats
        while (i.hasNext() && (entier || cpt < NB_RESULTAT)) {
        	ArrayList<String> tmp = new ArrayList<String>();
            Element courant = i.next();
            tmp.add(courant.getChildText("id"));
            tmp.add(courant.getChildText("SeriesName"));
            liste.add(tmp);
            cpt++;
        }
        //ajouter le dernier item : "afficher plus de resultat
        if(!entier && cpt == NB_RESULTAT){
        	ArrayList<String> tmp = new ArrayList<String>();
        	tmp.add(c.getString(R.string.id_afficher_plus));
        	tmp.add(c.getString(R.string.message_recherche_plus));
           
            liste.add(tmp);
        }
    }
    
    /**
     * libérer la mémoire
     */
    private void freeSpace() {
        racine = null;
        document = null;        
    }

    public ArrayList<ArrayList<String>> getListe() {
        return liste;
    }
}
