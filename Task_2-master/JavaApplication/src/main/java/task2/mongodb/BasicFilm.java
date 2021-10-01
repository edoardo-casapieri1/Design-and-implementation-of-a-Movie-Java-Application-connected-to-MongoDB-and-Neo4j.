package task2.mongodb;

public class BasicFilm {                
    protected String film_id;           
    protected String italian_title;
    private String original_title;
    protected Integer year;
    protected Integer runtime;

    public BasicFilm(String film_id, int year, int runtime, String italian_title) {
        this.film_id = film_id;
        this.italian_title = italian_title;
        this.year = year;
        this.runtime = runtime;
    }
    
    public String getFilm_id () {
        return film_id;
    }
    
    public String getTitle() {
        return italian_title;
    }
    
    public Integer getYear() {
        return year;
    }
    
    public Integer getRuntime() {
        return runtime;
    }
    
    public String getOriginal_title() {
		return original_title;
	}
    
    public String getItalian_title() {
		return italian_title;
	}
    
    public void setOriginal_title(String original_title) {
		this.original_title = original_title;
	}
}
