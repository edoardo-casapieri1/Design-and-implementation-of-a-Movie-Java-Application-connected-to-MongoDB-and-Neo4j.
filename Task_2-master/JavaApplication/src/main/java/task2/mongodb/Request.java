package task2.mongodb;

import java.lang.*;
import java.util.ArrayList;
import java.util.Date;
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

public class Request {

	private String request_id; 
	private final String film_title;
	private Date date;
	private byte status; //0:rejected 1: waiting 2:approved
	
	public Request (String film_title, Date date, String status) {
		this.film_title = film_title;
		this.date = date;
		switch (status) {
			case "waiting":
				this.status = 1;
				break;
			case "approved":
				this.status = 2;
				break;
			default:
				this.status = 0;
		}
		
	}
	
	public void setStatus(String status) {
		switch (status) {
		case "waiting":
			this.status = 1;
			break;
		case "approved":
			this.status = 2;
			break;
		default:
			this.status = 0;
		}
	}
	
	public Date getDate() {
		return date;
	}
	
	public Byte getStatus() {
		return status;
	}
	
	public String get_FilmTitle() {
		return film_title;
	}
	
}