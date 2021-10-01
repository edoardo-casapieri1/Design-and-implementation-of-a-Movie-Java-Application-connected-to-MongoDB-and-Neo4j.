package task2.mongodb;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.neo4j.driver.Driver;

public interface Dao {
    User getUser(String username);
    Admin getAdmin(String username);
    boolean addComment(String film_id, User user, Comment comment);
    boolean removeComment(String comment_id);
    boolean commentAction(User user, Comment comment, int action, int userAction);
    List<Request> fillRequest(User user);
    boolean addRequest(String film_title, User user, Request request);
    boolean modifyFavouriteList(WatchedFilm film, boolean state, User user);
    boolean addWatched(WatchedFilm watched, User user);
    BasicFilm getBasicFilm(String film_id);
    List<BasicFilm> getBasicFilmsIndex(int index);
    int checkLogin(String username, String password);
    Film getMoreComments(int index, Film current);
    Film get_film(String film_id);
    List<Comment> getCommentsByAuthor(String author);
    List<Document> mostWatchedByGenre(String genre);
    List<Document> ViewByNationality(String film_id);
    List<String> mostPopular();
    int checkLikedComment(User user, Comment comment);
    boolean changePassword(String id,String old, String newP);
    List<BasicFilm> searchFilms(String search,int year);
    String getUsernameById(String UserId);
    List<RequestAdmin> fillRequestAdmin(Admin admin);
    void exit();
	Document getUserInfo(String username);
	boolean deleteUser(String username);
	boolean setRequestStatusById(User user, String request_id, int outcome);
	boolean checkRequest(User user, String title_requested);
	List<BasicFilm> findBasicFilms(String italianTitle);
	List<BasicFilm> findBasicFilms(String italianTitle, int year);
	boolean deleteFilm(String film_id);
	boolean addUser(User u);
	String[] getMovieYears();
	String[] getGenres();
	List<String> moreLikeThis(String filmId);
	List<BasicFilm> similarRating(String username);
	List<BasicFilm> sameActors(String username);
	List<BasicFilm> sameDirectors(String username);
	Driver getDriver();
}
