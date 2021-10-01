package task2.mongodb;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

public class Admin {

	private String user_id;
	private String first_name;
	private String last_name;
	private String username;
	private String country;
	private Integer year_of_birth;
	private String password;

	private List<RequestAdmin> user_requestsAdmin = new ArrayList<RequestAdmin>();
	
    public boolean deleteComment(String commentId, Dao dao) {
	    return (dao.removeComment(commentId));
    }

	public void fill_Request(Dao dao) {
		user_requestsAdmin = dao.fillRequestAdmin(this);
	}

	public void setPassword(String pass) {
		password = pass;
	}

	public String getPassword() {
		return password;
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

	public List<RequestAdmin> getRequestAdmin() {
		return user_requestsAdmin;
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
