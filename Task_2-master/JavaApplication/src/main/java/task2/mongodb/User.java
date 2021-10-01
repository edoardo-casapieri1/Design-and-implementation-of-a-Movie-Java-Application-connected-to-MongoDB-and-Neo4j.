package task2.mongodb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public class User {

	private String user_id;
	private String first_name;
	private String last_name;
	private String username;
	private String country;
	private Integer year_of_birth;
	private Integer role;
	private String password;

	private List<WatchedFilm> watched_films = new ArrayList<WatchedFilm>();
	private List<Comment> user_comments = new ArrayList<Comment>();
	private List<Request> user_requests = new ArrayList<Request>();

	public boolean add_comment(String film_id, String text, Dao dao) {       
		String author = username;
		Date today = Calendar.getInstance().getTime();
		String type = "";		  													// di default il commento è di tipo "applicazione"
		String comment_id = new ObjectId().toString();
		Comment comment = new Comment(film_id, text, author, today, type);
		comment.setComment_id(comment_id);
		user_comments.add(comment);
		return dao.addComment(film_id, this, comment);
	}

	public boolean deleteComment(String commentId, Dao dao) {
		boolean result = user_comments.removeIf(comment -> ((comment.getComment_id()).equals(commentId)));	// rimuove dall'ArrayList il commento con l'id specificato
		return (dao.removeComment(commentId) && result); 

	}

	public Integer getRole() {

		return	role;
	}

	public void setRole(Integer r) {

		role=r;
	}

	public boolean likeComment(Comment comment, Dao dao, int totalIncrement) {
		comment.like_comment();
		return dao.commentAction(this, comment, totalIncrement, 1);
	}

	public boolean dislikeComment(Comment comment, Dao dao, int totalDecrement) {
		comment.dislike_comment();
		return dao.commentAction(this, comment, totalDecrement, -1);
	}

	public boolean add_request(String film_title, Dao dao) {
		Date today = Calendar.getInstance().getTime();
		Request request_send = new Request(film_title, today,"waiting");
		user_requests.add(request_send);
		return dao.addRequest(film_title, this, request_send);
	}

	public boolean add_watchedFilm(String film_id, String film_title, int rate, Dao dao) {
		Date today = Calendar.getInstance().getTime();
		WatchedFilm watched_new = new WatchedFilm(film_id, film_title, today, rate, false);
		watched_films.add(watched_new);
		return dao.addWatched(watched_new, this);
	}

	public int checkComment(Comment comment, Dao dao) {
		return dao.checkLikedComment(this, comment);
	}

	public void fill_Request(Dao dao) {
		user_requests = dao.fillRequest(this);
	}
	
	public void setPassword(String pass) {
	    password = pass;
	  }
	
	public String getPassword() {
	    return password ;
	  }

	public String getUserId() {
		return this.user_id;
	}

	public void setUserId(String id) {
		user_id = id;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String usrnm) {
		username = usrnm;
	}

	public void setFirst_name(String name) {
		first_name = name;
	}

	public void setLast_name(String last) {
		last_name = last;
	}

	public void setCountry(String cntry) {
		country = cntry;
	}

	public String getCountry() {
		return this.country;
	}

	public void setYear_of_birth(Integer year_of_birth) {
		this.year_of_birth = year_of_birth;
	}

	public void setWatched_films(List<WatchedFilm> watched_films) {
		this.watched_films = watched_films;
	}

	public List<WatchedFilm> getWatched_films() {
		return watched_films;
	}

	public List<Request>getRequest(){
		return user_requests;
	}

	public List<WatchedFilm>getWatched(){
		return watched_films;
	}

	public List<Comment> getUser_comments() {
		return user_comments;
	}

	public void setUser_comments(List<Comment> user_comments) {
		this.user_comments = user_comments;
	}

	public String getFirst_name() {
		return first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public Integer getYear_of_birth() {
		return year_of_birth;
	}

}