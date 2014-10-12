/**
 * 1) Download le zip
 * 2) Le garder de coté et fire event apres DL
 * 3) Appeler les autres asynctask avec un Fire Event 
 * 	a) Task SerieInfo
 * 	b) Picture Serie
 *  c) Picture Last & Next en une seule classe avec switch bool
 *  
 */
package com.valohyd.nextseries.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;

import android.content.Context;
import android.util.Log;

import com.valohyd.nextseries.models.Episode;

public class Unzipper {

    private static Document document;
    private static Element racine;
    private HashMap<String, String> infos;
    private Element last;
    private File file = null;

    public HashMap<String, String> getSerieInfos(Element root) {
        if (root != null) {
            infos = new HashMap<String, String>();

            racine = root;

            Element serie = racine.getChild("Series");
            infos.put("id", serie.getChildText("id"));
            infos.put("name", serie.getChildText("SeriesName"));
            infos.put("channel", " (" + serie.getChildText("Network") + ")");
            infos.put("status", serie.getChildText("Status"));
            infos.put("banner", serie.getChildText("banner"));
            infos.put("rating", serie.getChildText("Rating"));
            infos.put("resume", serie.getChildText("Overview"));
            infos.put("actors", serie.getChildText("Actors"));
            getLastAndNextEp();
        }
        return infos;
    }

    public void delete() {
        if (file != null)
            file.delete();
    }

    /**
     * Retourne la liste des saisons
     * 
     * @param c
     * @param url
     * @return
     */
    public ArrayList<String> getSeasons(Context c, String url) {
        ArrayList<String> seasons = new ArrayList<String>();

        racine = new DownloadSerieZip().downloadSerieInfos(c, url, 0);
        while (racine == null) {
            Log.d("NextSeries-getSeasons", "racine null");
            racine = new DownloadSerieZip().downloadSerieInfos(c, url, 0);
        }
        Iterator listIt = racine.getChildren("Episode").iterator();
        while (listIt.hasNext()) {
            Element currentElem = (Element) listIt.next();
            String currentSeason = checkElem(currentElem.getChild("Combined_season"));
            if (!seasons.contains(currentSeason)) {
                seasons.add(currentSeason);
            }
        }
        delete();
        if(!seasons.contains("0"))
            seasons.add(0, "0");
            
        return seasons;
    }

    /**
     * Retourne les episodes d'une saison
     * 
     * @param season
     * @return
     */

    public ArrayList<Episode> getEpisodes(int season) {
        ArrayList<Episode> episodes = new ArrayList<Episode>();

        Iterator listIt = racine.getChildren("Episode").iterator();
        while (listIt.hasNext()) {
            Element currentElem = (Element) listIt.next();
            if (checkElem(currentElem.getChild("Combined_season")).equals(Integer.toString(season))) {
                Episode tmp = new Episode(
                        currentElem.getChildText("id"),
                        currentElem.getChildText("EpisodeName"),
                        ("S" + epToString(checkElem(currentElem.getChild("Combined_season"))) + "E" + epToString(checkElem(currentElem
                                .getChild("EpisodeNumber")))), currentElem.getChildText("FirstAired"), currentElem
                                .getChildText("GuestStars"), currentElem.getChildText("filename"), currentElem
                                .getChildText("Overview"), currentElem.getChildText("Rating"),"");
                episodes.add(tmp);
            }
        }

        return episodes;
    }

    /**
     * renvoie le dernier bon episode de la serie
     */
    private void getLastAndNextEp() {
        Iterator listIt = racine.getChildren("Episode").iterator();
        int nb = 0;
        while (listIt.hasNext()) {
            nb++;
            Element currentElem = (Element) listIt.next();
            String currentDate = currentElem.getChildText("FirstAired");
            if (!currentDate.equals("")) {
                Date current = null;
                try {
                    current = stringToDate(currentDate, "yyyy-MM-dd");
                } catch (Exception e) {
                    Log.e("ERROR", "Erreur de format !");
                }
                if (current.after(new Date()) && checkEp(currentElem) && last != null && currentElem != null) {
                    infos.put(
                            "last_ep",
                            "S" + epToString(checkElem(last.getChild("Combined_season"))) + "E"
                                    + epToString(checkElem(last.getChild("EpisodeNumber"))) + " - "
                                    + checkElem(last.getChild("EpisodeName")) + "\t\n Date : "
                                    + getRealDate(checkElem(last.getChild("FirstAired"))));
                    infos.put("pic_episode_last", checkElem(last.getChild("filename")));
                    infos.put("next_ep", "S" + epToString(checkElem(currentElem.getChild("Combined_season"))) + "E"
                            + epToString(checkElem(currentElem.getChild("EpisodeNumber"))) + " - "
                            + checkElem(currentElem.getChild("EpisodeName")) + "\t\n Date : "
                            + getRealDate(checkElem(currentElem.getChild("FirstAired"))));
                    infos.put("pic_episode_next", checkElem(currentElem.getChild("filename")));
                    break;
                }
            }
            last = currentElem;
        }
        infos.put("nb_episodes", "" + nb);
    }

    /**
     * renvoie vrai si l'episode est bon
     */
    private boolean checkEp(Element episode) {
        return !(Integer.parseInt(episode.getChildText("Combined_season")) < 1 || Integer.parseInt(episode
                .getChildText("EpisodeNumber")) < 1);
    }

    /** Verifie si l'info existe **/
    public String checkInfos(String info) {
        if (info != null && !info.equals("")) {
            return info;
        } else {
            return "NC";
        }
    }

    /** Verifie si l'element existe **/
    public String checkElem(Element elem) {
        if (elem != null && !elem.equals("")) {
            return elem.getText();
        } else {
            return "NC";
        }
    }

    private String getRealDate(String date) {
        Locale locale = Locale.getDefault();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);
        String str = "";
        try {
            str = dateFormat.format(stringToDate(date, "yyyy-MM-dd"));
        } catch (Exception e) {
            Log.e("ERROR", "Erreur de format !");
        }
        return str;
    }

    /**
     * permet d'afficher le 0 devant les numéros d'épisodes < 10
     */
    public static String epToString(String epNumber) {
        if (Integer.parseInt(epNumber) < 10) {
            return "0" + epNumber;
        }
        return epNumber;
    }

    public static Date stringToDate(String sDate, String sFormat) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(sFormat);
        return sdf.parse(sDate);
    }

    public HashMap<String, String> getSerieInfosStatusAndCo(Element root) {
        if (root != null) {
            infos = new HashMap<String, String>();

            racine = root;

            Element serie = racine.getChild("serie");
            if(serie != null){
	            infos.put("status", serie.getChildText("statut"));
	            infos.put("rating", serie.getChildText("note"));
	            infos.put("nb_episodes", serie.getChildText("nb_ep"));
            }
        }
        return infos;
    }

}
