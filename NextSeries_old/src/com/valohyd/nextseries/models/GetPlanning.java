package com.valohyd.nextseries.models;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import com.valohyd.nextseries.utils.Helper;

public class GetPlanning {
    // attributs
    private Element racine;
    private Document document;
    private ArrayList<Serie> liste;
    private ArrayList<Pair<String, Episode>> listByDate;
	TextView title,nomEp,date;

	/**
	 * Constructeur pour lancer la récupération du planning par serie
	 * @param c
	 * @param chemin
	 */
    public GetPlanning(Context c, String chemin) {
        this(c, chemin, Helper.recupFavoris(c), false);
    }
    
    /**
     * Constructeur pour lancer la récupération du planning par date (ou non, suivant le paramètre)
     * @param c
     * @param chemin
     * @param byDate par date ou non
     */
    public GetPlanning(Context c, String chemin, boolean byDate) {
        this(c, chemin, Helper.recupFavoris(c), byDate);
    }
    
    /**
     * constructeur privé qui fait réellement le boulot en fonction du choix
     * @param c
     * @param chemin
     * @param ids
     * @param byDate
     */
    private GetPlanning(Context c, String chemin, String ids, boolean byDate) {
		SAXBuilder sxb = new SAXBuilder();
        try {
            // se connecter au xml
            document = sxb.build(chemin + ids);
            racine = document.getRootElement();
            liste = new ArrayList<Serie>();
            listByDate = new ArrayList<Pair<String,Episode>>();
        } catch (Exception e) {
            Log.e("NextSeries", "Planning : Exception caught : "+e);
            return;
        }
        if(racine == null || document == null){
            return;
        }
        
        // si on veut les episodes par date et non pas par serie
        if(byDate){
            fillPlanningByDate();
        }
        else{
            fillPlanning();
        }
        freeSpace();
        sxb = null;
    }
    
    /**
     * Constructeur pour récupérer le planning d'un série (met à jour la serie)
     * @param c
     * @param chemin
     * @param s
     */
    public GetPlanning(Context c, String chemin, Serie s){
        Log.d("NextSeries", "Planning : construction");
        //inflater = (LayoutInflater) c
        //        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
        SAXBuilder sxb = new SAXBuilder();
        try {
            // se connecter au xml
            Log.d("NextSeries", "planning : "+chemin + s.getId());
            document = sxb.build(chemin
                    + s.getId());
            racine = document.getRootElement();
        } catch (Exception e) {
            Log.e("NextSeries", "Planning : Exception caught : "+e);
            return;
        }
        if(racine == null || document == null){
            Log.d("NextSeries", "Planning : racine ou doc null");
            return;
        }
        fillPlanningSerie(s);
        freeSpace();
        sxb = null;
    }
    
    /**
     * maj la serie en ajoutant le planning
     * @param c
     * @param s
     */
    @SuppressWarnings("unchecked")
    private void fillPlanningSerie(Serie s) {
        // affiche les résultat de la recherche dans la liste
        Iterator<Element> i = null;
        List<Element> l = racine.getChildren("semaine");
        if(l.size() == 0){
            Log.d("NextSeries", "Planning : liste d'episode vide");
            return;
        }
        i = l.iterator();
        
        //parcourir le xml et ajouter les resultats
        Log.d("NextSeries", "Planning : parcourt du fichier distant");
        while (i.hasNext()) {
            Element courant = i.next();
            
            //pour chaque ep : les ajouter à la serie
            List<Element> l2 = courant.getChildren("episode");
            Iterator<Element> i2 = l2.iterator();
            Episode e;
            while(i2.hasNext()){
                Element ep = i2.next();
                e = new Episode(ep.getChildText("ep_title"), ep.getChildText("ep_num"), ep.getChildText("ep_date"), ep.getChildText("ep_id"), ep.getChildText("ep_days"));
                s.addEp(e);
                Log.d("NextSeries","Planning : Episode "+e.getTitre());
            }
        }
    }

    /**
     * récupère le planning par série 
     */
    @SuppressWarnings({ "unchecked" })
    private void fillPlanning() {
        // affiche les résultat de la recherche dans la liste
        Iterator<Element> i = null;
        List<Element> l = racine.getChildren("serie");
        if(l.size() == 0){
//            Helper.displayExceptionMessage(c, "Aucun résultat trouvé");
            return;
        }
        i = l.iterator();
        
        //parcourir le xml et ajouter les resultats
        Serie s;
        Log.d("NextSeries", "Planning : parcourt du fichier distant");
        while (i.hasNext()) {
            Element courant = i.next();
            //TODO mettre le titre
        	s = new Serie(courant.getChildText("title"));
            s.setId(courant.getChildText("id"));
            
            //pour chaque ep : les ajouter à la serie
            List<Element> l2 = courant.getChildren("episode");
            Iterator<Element> i2 = l2.iterator();
            Episode e;
            while(i2.hasNext()){
                Element ep = i2.next();
                e = new Episode(ep.getChildText("ep_title"), ep.getChildText("ep_num"), ep.getChildText("ep_date"), ep.getChildText("ep_id"),ep.getChildText("ep_days"));
                s.addEp(e);
                Log.d("NextSeries","Planning : Episode "+e.getTitre());
            }
            Log.d("NextSeries", "Planning : ajout de la serie à la liste");
            liste.add(s);
            
            Log.d("PLANNING",liste.toString());
            
        }
    }
    
    /**
     * récupère le planning par date
     */
    @SuppressWarnings("unchecked")
	private void fillPlanningByDate() {
        // affiche les résultat de la recherche dans la liste
        Iterator<Element> i = null;
        List<Element> l = racine.getChildren("episode");
        if(l.size() == 0){
            return;
        }
        i = l.iterator();
        
        //parcourir le xml et ajouter les resultats
        Log.d("NextSeries", "Planning : parcourt du fichier distant");
        Episode episode;
        while (i.hasNext()) {
            Element courant = i.next();
            //TODO mettre le titre
            episode = new Episode(courant.getChildText("ep_title"), courant.getChildText("ep_num"), courant.getChildText("ep_date"), courant.getChildText("ep_id"),courant.getChildText("ep_days"));
            Pair<String, Episode> p = new Pair<String, Episode>(courant.getChildText("serie_name"), episode);
            listByDate.add(p);
        }
    }
    
    /**
     * libérer la mémoire
     */
    private void freeSpace() {
        racine = null;
        document = null;        
    }

    public ArrayList<Serie> getListe() {
        return liste;
    }
    
    public ArrayList<Pair<String, Episode>> getListeByDate(){
        return listByDate;
    }

}
