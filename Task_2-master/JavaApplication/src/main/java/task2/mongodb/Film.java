package task2.mongodb;

import java.lang.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.spi.DirStateFactory.Result;
import javax.swing.text.Document;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.json.JSONException;
import org.json.JSONObject;

public class Film {

	private String film_id;
	private String original_title;      	
	private String italian_title;
	private String synopsis;
	private Integer year;
	private Integer runtime;
	private Number box_office;
	private List<String> countries;
	private List<String> genres;
	private List<String> directors;
	private List<Actor> cast = new ArrayList<>();
	private List<Comment> comments = new ArrayList<Comment>();
	private Map<String, Float> ratings = new HashMap<>();
	private List<Platform> platforms = new ArrayList<>();
	private List<String> awards = new ArrayList<>();
	//E' necessario creare le classi Actor e Rating?

	//public Film(org.bson.Document doc) {
		// dao.get_film(doc.getObjectId("_id").toString());
		 
    //}
	
	public Film() {	
	
	}


    public boolean add_comment(Comment comment) {
		boolean result = true;
		comments.add(comment);
		return result;
	}

	public String getFilm_id() {
		return film_id;
	}

	public void setFilm_id(String film_id) {
		this.film_id = film_id;
	}

	public String getOriginal_title() {
		return original_title;
	}

	public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}

	public String getItalian_title() {
		return italian_title;
	}

	public void setItalian_title(String italian_title) {
		this.italian_title = italian_title;
	}

	public void setSynopsis(String synopsis) {
		this.synopsis = synopsis;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Number getBox_office() {
		return box_office;
	}

	public void setBox_office(Number box_office) {
		this.box_office = box_office;
	}

	public Integer getRuntime() {
		return runtime;
	}

	public void setRuntime(Integer runtime) {
		this.runtime = runtime;
	}

	public List<String> getCountries() {
		return countries;
	}
	
	public String getSynopsis() {
		return synopsis;
	}

	public void setCountries(List<String> countries) {
		this.countries = countries;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public List<String> getDirectors() {
		return directors;
	}

	public void setDirectors(List<String> directors) {
		this.directors = directors;
	}

	public List<Actor> getCast() {
		return cast;
	}

	public void setCast(List<Actor> cast) {
		this.cast = cast;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setRatings(Map<String, Float> ratings) {
		this.ratings = ratings;
	}

	public Map<String, Float> getRatings() {
		return ratings;
	}
	
	public void setPlatforms(List<Platform> platforms) {
		this.platforms = platforms;
	}
	
	public List<Platform> getPlatforms() {
		return platforms;
	}
	
	public List<String> getAwards() {
		return awards;
	}
	
	public void setAwards(List<String> awards) {
		this.awards = awards;
	}
}