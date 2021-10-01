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

public class RequestAdmin extends Request {

	private String user_id;
	
	public RequestAdmin (String film_title, Date date, String status, String id) {
		super(film_title, date, status);
		this.user_id = id;
	}
	
	public String get_UserId() {
		return user_id;
	}
	
}