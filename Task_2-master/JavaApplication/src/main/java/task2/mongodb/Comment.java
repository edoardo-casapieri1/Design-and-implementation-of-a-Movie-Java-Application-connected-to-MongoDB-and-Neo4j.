package task2.mongodb;

import java.util.Calendar;
import java.util.Date;

import org.bson.types.ObjectId;

public class Comment {
	
	private String comment_id;         // impostato dopo l'inserimento nel database
	private final String film_id;
	private String text;
	private String author;
	private Date date;
	private int comment_points;
	private byte type;                     // 0: applicazione, 1: IMDb, 2: MyMovies 
	private Integer user_rating;
	
	public Comment (String film_id, String text, String author, Date date, String type) {
		this.film_id = film_id;
		this.text = text;
		this.author = author;
		this.date = date;
		this.comment_points = 0;
		switch (type) {
			case "imdb_comment":
				this.type = 1;
				break;
			case "mymovies_comment":
				this.type = 2;
				break;
			default:
				this.type = 0;
		}
		
	}
	
	public void like_comment() {
		comment_points++;
	}
	
	public void dislike_comment() {
		comment_points--;
	}

	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}
	
	public String getComment_id() {
		return comment_id;
	}

	public void setComment_points(int comment_points) {
		this.comment_points = comment_points;
	}

	public void setUser_rating(Integer user_rating) {
		this.user_rating = user_rating;
	}

	public String getText() {
		return text;
	}
	
	public Date getDate() {
		return date;
	}
	
	public byte getType() {
		return type;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public int getComment_points() {
		return comment_points;
	}
	
	public String getFilm_id() {
		return film_id;
	}
}
