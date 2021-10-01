package task2.mongodb;

import java.util.Date;

public class WatchedFilm  {

	private String id;
	private String title;
	private Date added_on;
	private int rating;
	private boolean favorite;
	
	
	public WatchedFilm(String film_id, String title, Date date, int rating, boolean favorite) {
		this.id = film_id;
		this.title = title;
		this.added_on = date;
		this.rating = rating;
		this.favorite = favorite;
	}

	public String getFilm_id() {
		return id;
	}

	public String getItalian_title() {
		return title;
	}

	public void setItalian_title(String italian_title) {
		this.title = italian_title;
	}

	public Date getAdded_on() {
		return added_on;
	}

	public void setAdded_on(Date added_on) {
		this.added_on = added_on;
	}
	
	public int get_rate() {
		return rating;
	}
	
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}
	
	public boolean getFavorite() {
		return favorite;
	}

}
