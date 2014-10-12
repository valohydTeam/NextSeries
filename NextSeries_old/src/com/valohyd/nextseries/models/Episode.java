package com.valohyd.nextseries.models;


public class Episode {
	/** titre de l'épisode **/
	private String titre;
	/** numéro de l'épisode (S00E00) **/
	private String numéro;
	/** date de l'épisode **/
	private String date;
	/** id de l'episode **/
	private String id;
	/** acteurs de l'episode **/
	private String acteurs;
	/** image de l'episode **/
	private String image;
	/** note de l'episode **/
	private String rating;
	/** resume de l'episode **/
	private String resume;
	/** jours restants **/
	private String daysLeft;

	public Episode(String id, String titre, String numéro, String date,
			String acteurs, String image, String resume, String rating,String daysLeft) {
		super();
		this.titre = titre;
		this.numéro = numéro;
		this.date = date;
		this.id = id;
		this.acteurs = acteurs;
		this.image = image;
		this.resume = resume;
		this.rating = rating;
		this.daysLeft = daysLeft;
	}

	public Episode(String titre, String numéro, String date, String id,String daysLeft) {
		super();
		this.titre = titre;
		this.numéro = numéro;
		this.date = date;
		this.id = id;
		this.daysLeft = daysLeft;
	}

	/*
	 * Getters et setters
	 */
	public String getTitre() {
		return titre;
	}

	public String getActeurs() {
		return acteurs;
	}

	public String getRating() {
		return rating;
	}

	public String getResume() {
		return resume;
	}

	public String getImage() {
		return image;
	}
	
	public String getDaysLeft() {
		return daysLeft;
	}

	public void setTitre(String titre) {
		this.titre = titre;
	}

	public String getNuméro() {
		return numéro;
	}

	public void setNuméro(String numéro) {
		this.numéro = numéro;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
