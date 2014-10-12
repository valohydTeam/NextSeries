package com.valohyd.nextseries.models;

import java.util.ArrayList;
import java.util.Date;

import com.valohyd.nextseries.R;

/**
 * Une série est à un titre et une liste d'episode
 * 
 * @author Valohyd Team
 * 
 */
public class Serie implements Comparable<Serie> {
	/** titre de la série **/
	private String titre;
	/** id de la série **/
	private String id;
	/** liste des episodes **/
	private ArrayList<Episode> eps;

	private String note;
	private String status;
	private String channel;
	private String nbEp;
	private String banner;

	/**
	 * Constructeur pour les recherches
	 * 
	 * @param titre
	 * @param id
	 */
	public Serie(String titre, String id) {
		this(titre, id, null, null, null, null, null);
	}

	/**
	 * Constructeur pour le top serie sur page d'accueil
	 * 
	 * @param titre
	 * @param id
	 * @param banner
	 */
	public Serie(String titre, String id, String banner) {
		this(titre, id, null, null, null, null, banner);
	}
	
	/**
	 * Constructeur pour mesSeries
	 * @param titre
	 * @param id
	 * @param chaine
	 * @param banner
	 */
	public Serie(String titre, String id, String chaine, String banner){
        this(titre, id, null, null, chaine, null, banner);
    }

	/**
	 * Constructeur pour la vue serie
	 * 
	 * @param titre
	 * @param id
	 * @param note
	 * @param status
	 * @param channel
	 * @param nbEp
	 * @param banner
	 */
	public Serie(String titre, String id, String note, String status,
			String channel, String nbEp, String banner) {
		super();
		this.titre = titre;
		this.id = id;
		this.note = note;
		this.channel = channel;
		this.nbEp = nbEp;
		this.status = status;
		this.banner = banner;
		eps = new ArrayList<Episode>();
	}

	public String getNote() {
		return note;
	}

	public String getStatus() {
		return status;
	}

	public String getChannel() {
		return channel;
	}

	public String getNbEp() {
		return nbEp;
	}

	public String getBanner() {
		return banner;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public void setNbEp(String nbEp) {
		this.nbEp = nbEp;
	}

	public void setBanner(String banner) {
		this.banner = banner;
	}

	public Serie(String titre) {
		this(titre, "0");
	}

	// getter et setter
	public String getTitre() {
		return titre;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public ArrayList<Episode> getEps() {
		return eps;
	}

	public void setEps(ArrayList<Episode> eps) {
		this.eps = eps;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	// ajoute un ep à la serie
	public void addEp(Episode ep) {
		eps.add(ep);
	}

	public int compareTo(Serie another) {
		Date d1 = new Date(this.eps.get(0).getDate());
		Date d2 = new Date(another.eps.get(0).getDate());
		if (d1.equals(d2)) {
			return 0;
		}
		if (d1.before(d2)) {
			return -1;
		} else {
			return 1;
		}
	}
}
