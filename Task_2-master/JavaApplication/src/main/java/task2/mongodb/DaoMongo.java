package task2.mongodb;

import com.mongodb.ReadConcern;

import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.TransactionBody;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.ClientSession;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.exceptions.ServiceUnavailableException;
import org.w3c.dom.ranges.DocumentRange;

import static com.mongodb.client.model.Projections.*;

import javax.print.Doc;
import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

public class DaoMongo implements Dao {

	private MongoClient mongoClient;

	private Driver driver;

	private MongoCollection<Document> users;
	private MongoCollection<Document> films;
	private MongoCollection<Document> comments;

	private DaoMongo() {
		try {
			mongoClient = MongoClients.create(
					"mongodb://172.16.0.121:27017,172.16.0.122:27017,172.16.0.124:27017/?replicaSet=task_2&readPreference=primary");
			MongoDatabase database = mongoClient.getDatabase("task_2").withWriteConcern(WriteConcern.W2);
			users = database.getCollection("users");
			films = database.getCollection("films");
			comments = database.getCollection("comments");
			Config config = Config.builder().withMaxTransactionRetryTime(30, TimeUnit.SECONDS).build();
			driver = GraphDatabase.driver("bolt://172.16.0.125:7687", AuthTokens.basic("neo4j", "root"), config);
			driver.verifyConnectivity();
		} catch (Exception e) {
			System.err.println("Unable to connect to the database!");
			exit();
			System.exit(-1);
		}
	}

	private static class MongoSingleton {
		private static final DaoMongo INSTANCE = new DaoMongo();
	}

	public static DaoMongo getInstance() {
		return MongoSingleton.INSTANCE;
	}

	public Driver getDriver() {
		return driver;
	}

	public boolean film_already_watched(String username, Film film) {
		MongoCursor<org.bson.Document> cursor = users.find(
				Filters.and(Filters.eq("watched_films.film_id", film.getFilm_id()), Filters.eq("username", username)))
				.iterator();
		if (cursor.hasNext())
			return false;
		else
			return true;
	}

	@Override
	public boolean addUser(User u) {

		boolean result = false;
		ObjectId userId = new ObjectId();

		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody<Boolean> txnBody = new TransactionBody<Boolean>() {
			boolean check = true;

			public Boolean execute() {
				Document doc = new Document("_id", userId).append("username", u.getUsername())
						.append("password", u.getPassword())
						.append("general_info",
								new Document("first_name", u.getFirst_name()).append("last_name", u.getLast_name())
										.append("country", u.getCountry())
										.append("year_of_birth", u.getYear_of_birth()))
						.append("role", 1);

				users.insertOne(doc);

				try (Session session = driver.session()) {
					session.writeTransaction(new TransactionWork<Boolean>() {
						@Override
						public Boolean execute(Transaction tx) {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("userId", userId.toString());
							params.put("username", u.getUsername());
							params.put("fname", u.getFirst_name());
							params.put("lname", u.getLast_name());
							params.put("country", u.getCountry());
							params.put("year", u.getYear_of_birth());
							Result result = tx.run(
									"CREATE (:User { _id: $userId, username: $username, first_name: $fname, last_name: $lname, country: $country, year_of_birth: $year})",
									params);
							return true;
						}
					});
				} catch (ServiceUnavailableException ex) {
					System.out.println("Unable to connect to Neo4j server!");
					check = false;
				}

				if (!check)
					users.deleteOne(doc);

				return check;
			}
		};
		try {
			result = clientSession.withTransaction(txnBody);
		} finally {
			clientSession.close();
		}

		return result;
	}

	@Override
	public List<Request> fillRequest(User user) {
		ObjectId user_oid = new ObjectId(user.getUserId());
		MongoCursor<Document> cursor = users.find(Filters.and(Filters.eq("_id", user_oid))).iterator();
		List<Request> requests = new ArrayList<Request>();
		try {
			if (cursor.hasNext()) {
				Document user_i = cursor.next();
				List<Document> messages = (List<Document>) user_i.get("messages");
				for (int j = 0; j < messages.size(); j++) {
					String title = messages.get(j).getString("film_title");
					Date date = messages.get(j).getDate("date");
					Integer status = messages.get(j).getInteger("status");
					String st;
					switch (status) {
					case 1:
						st = "waiting";
						break;
					case 2:
						st = "approved";
						break;
					default:
						st = "rejected";
					}
					Request request_i = new Request(title, date, st);
					requests.add(request_i);
				}
			}
		} finally {
			cursor.close();
		}

		return requests;

	}

	@Override
	public List<RequestAdmin> fillRequestAdmin(Admin admin) {
		ObjectId user_oid = new ObjectId(admin.getUserId());
		MongoCursor<Document> cursor = users.find(Filters.and(Filters.eq("_id", user_oid))).iterator();
		List<RequestAdmin> requests = new ArrayList<RequestAdmin>();
		try {
			if (cursor.hasNext()) {
				Document user_i = cursor.next();
				List<Document> messages = (List<Document>) user_i.get("messages");
				for (int j = 0; j < messages.size(); j++) {
					String title = messages.get(j).getString("film_title");
					Date date = messages.get(j).getDate("date");
					Integer status = messages.get(j).getInteger("status");
					String st;
					switch (status) {
					case 1:
						st = "waiting";
						break;
					case 2:
						st = "approved";
						break;
					default:
						st = "rejected";
					}
					String user_id = messages.get(j).getObjectId("user_id").toString();
					// System.out.println("title: " + title + " date: " + date + " status: " + st +
					// " user_id " + user_id);
					RequestAdmin request_i = new RequestAdmin(title, date, st, user_id);
					requests.add(request_i);
				}
			}
		} finally {
			cursor.close();
		}

		return requests;
	}

	public Document getUserInfo(String username) {
		MongoCursor<org.bson.Document> cursor = users
				.find(Filters.eq("username", username)).projection(Projections.fields(Projections
						.include("general_info.country", "general_info.first_name", "general_info.last_name")))
				.iterator();
		try {
			if (cursor.hasNext()) {
				org.bson.Document doc = cursor.next();
				// System.out.println(doc.toJson());
				return doc;
			} else {
				return null;
			}
		} finally {
			cursor.close();
		}
	}

	@Override
	public boolean deleteUser(String username) {
		boolean result = false;
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody<Boolean> txnBody = new TransactionBody<Boolean>() {
			boolean check = true;
			Document userToDelete = new Document();
			List<Document> likedCommentsList = new ArrayList<>();
			List<Document> userCommentsList = new ArrayList<>();
			Map<ObjectId, Document> userRatings = new HashMap<ObjectId, Document>();
			Map<ObjectId, ObjectId> filmCommentMap = new HashMap<ObjectId, ObjectId>();
			ArrayList<Document> userRequests = new ArrayList<>();

			public Boolean execute() {
				try {
					MongoCursor<Document> cursor2 = users
							.aggregate(Arrays.asList(Aggregates.match(Filters.eq("username", username)),
									Aggregates.unwind("$liked_comments"),
									Aggregates.project(Projections.fields(Projections.include("liked_comments")))))
							.iterator();
					userToDelete = users.find(Filters.eq("username", username)).first();
					try {
					while (cursor2.hasNext()) {
						Document doc = cursor2.next();
						Document doc1 = (Document) doc.get("liked_comments");
						likedCommentsList.add(doc1);
						int action = doc1.getInteger("action");
						int points = 0;
						ObjectId comment_id = doc1.getObjectId("comment_id");
						MongoCursor<org.bson.Document> cursorPoint = comments.find(Filters.eq("_id", comment_id))
								.projection(Projections.fields(Projections.include("comment_points"))).iterator();
						while (cursorPoint.hasNext()) {
							Document docPoint = cursorPoint.next();
							points = docPoint.getInteger("comment_points");
							if (action == -1)
								points++;
							else
								points--;
						}
						comments.updateOne(Filters.eq("_id", comment_id), Updates.set("comment_points", points));
					}
					} finally {
						cursor2.close();
					}
				} catch (NullPointerException e) {
				}
				MongoCursor<org.bson.Document> cursor = comments.find(Filters.eq("author", username)).iterator();
				while (cursor.hasNext()) {
					Document doc = cursor.next();
					userCommentsList.add(doc);
					ObjectId commentId = doc.getObjectId("_id");
					ObjectId filmId = doc.getObjectId("film_id");
					filmCommentMap.put(filmId, commentId);
					// System.out.println("sto cancellando i commenti :" + commentId);
					try {
						films.updateMany(Filters.eq("comments", commentId), Updates.pull("comments", commentId));
					} catch (NullPointerException e) {
					}
					
					// perché fare questo?
					try {
						users.updateMany(Filters.eq("liked_comments.comment_id", commentId),
								new org.bson.Document("$pull", new org.bson.Document("liked_comments",
										new org.bson.Document("comment_id", commentId))));
					} catch (NullPointerException e) {
					}
				}
				try {
					films.updateMany(Filters.eq("film_ratings.user", username), new org.bson.Document("$pull",
							new org.bson.Document("film_ratings", new org.bson.Document("user", username))));
					MongoCursor<Document> cursor3 = films.find(Filters.eq("film_ratings.user", username)).projection(Projections.fields(Projections.include("film_ratings.$"))).iterator();
					try {
						while (cursor3.hasNext()) {
							Document document = cursor3.next();
							ObjectId filmId = document.getObjectId("_id");
							ArrayList<Object> ratingList = (ArrayList<Object>) document.get("film_ratings");
							for (Object item : ratingList) {
								Document rating = (Document) item;
								userRatings.put(filmId, rating);
							}		
							
						}
					} finally {
						cursor3.close();
					}
				} catch (NullPointerException e) {
				}
				MongoCursor<org.bson.Document> cursor1 = users.find(Filters.eq("username", username))
						.projection(Projections.fields(Projections.include("_id"))).iterator();
				while (cursor1.hasNext()) {
					Document doc = cursor1.next();
					ObjectId userId = doc.getObjectId("_id");
					// System.out.println("sto cancellando le richieste di :" + userId);
					try {
						users.updateMany(Filters.and(Filters.eq("role", 0), Filters.eq("messages.user_id", userId)),
								new org.bson.Document("$pull",
										new org.bson.Document("messages", new org.bson.Document("user_id", userId))));
						MongoCursor<Document> cursor_ = users.find(Filters.and(Filters.eq("role", 0), Filters.eq("messages.user_id", userId)))
								.projection(Projections.fields(Projections.include("messages.$"))).iterator();
						try {
							while (cursor_.hasNext()) {
								Document document = cursor_.next();
								userRequests = (ArrayList<Document>) document.get("messages");	
								
							}
						} finally {
							cursor_.close();
						}
					} catch (NullPointerException e) {
					}
					try {
						films.updateMany(Filters.eq("favorited_by", userId), Updates.pull("favorited_by", userId));
					} catch (NullPointerException e) {
					}
					try {
						films.updateMany(Filters.eq("watched_by.user_id", userId), new org.bson.Document("$pull",
								new org.bson.Document("watched_by", new org.bson.Document("user_id", userId))));
					} catch (NullPointerException e) {
					}
				}
				try {
					comments.deleteMany(Filters.eq("author", username));
				} catch (NullPointerException e) {
				}
				users.deleteOne(Filters.eq("username", username));
				
				try (Session session = driver.session()) {
					session.writeTransaction(new TransactionWork<Boolean>() {
						@Override
						public Boolean execute(Transaction tx) {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("username", username);
							tx.run("MATCH (n: User { username: $username }) DETACH DELETE n", params);
							tx.commit();
							return true;
						}
					});
					check = true;
				} catch (ServiceUnavailableException ex) {
					System.out.println("Unable to connect to Neo4j server!");
					check = false;
				}

				if (!check) {
					
					users.insertOne(userToDelete);
					
					for (Document likedComment : likedCommentsList) {
						int action = likedComment.getInteger("action");
						ObjectId comment_id = likedComment.getObjectId("comment_id");
							if (action == -1)
								comments.updateOne(Filters.eq("_id", comment_id), Updates.inc("comment_points", -1));
							else
								comments.updateOne(Filters.eq("_id", comment_id), Updates.inc("comment_points", 1));
					}
					
					if (!userCommentsList.isEmpty())
						comments.insertMany(userCommentsList);
					
					for (Map.Entry<ObjectId, Document> entry : userRatings.entrySet()) {
					    ObjectId filmId = entry.getKey();
					    Document rating = entry.getValue();
					    films.updateOne(Filters.eq("_id", filmId), Updates.addToSet("film_ratings", rating));
					}
					
					for (Map.Entry<ObjectId, ObjectId> entry : filmCommentMap.entrySet()) {
					    ObjectId filmId = entry.getKey();
					    ObjectId commentId = entry.getValue();
					    films.updateOne(Filters.eq("_id", filmId), Updates.addToSet("comments", commentId));
					}
					
					if (!userRequests.isEmpty())
						users.updateOne(Filters.eq("role", 0), Updates.addEachToSet("messages", userRequests));
					
					List<Document> watchedList = (ArrayList<Document>) userToDelete.get("watched_films", new ArrayList<Document>());
					ObjectId userId = userToDelete.getObjectId("_id");
					if (!watchedList.isEmpty()) {
						for (Document item : watchedList) {
							ObjectId film_id = item.getObjectId("film_id");
							Date date = item.getDate("date");
							boolean favorite = item.getBoolean("favorite", false);
							Document watched = new Document("user_id", userId).append("date", date);
							films.updateOne(Filters.eq("_id", film_id), Updates.addToSet("watched_by", watched));
							if (favorite)
								films.updateOne(Filters.eq("_id", film_id), Updates.addToSet("favorited_by", userId));
						}
					}
					
				}
				
				return check;
			}
		};
		try {
			result = clientSession.withTransaction(txnBody);
		} finally {
			clientSession.close();
		}

		return result;
	}

	@Override
	public boolean modifyFavouriteList(WatchedFilm film, boolean state, User user) {
		boolean result = false;
		ObjectId userId = new ObjectId(user.getUserId());
		ObjectId filmId = new ObjectId(film.getFilm_id());
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody<String> txnBody = new TransactionBody<String>() {
			public String execute() {
				if (state == true) {
					// System.out.println("aggiungo");
					org.bson.Document doc = new org.bson.Document("film_id", filmId).append("italian_title",
							film.getItalian_title());
					users.updateOne(
							Filters.and(Filters.eq("username", user.getUsername()),
									Filters.eq("watched_films.film_id", filmId)),
							Updates.set("watched_films.$.favorite", true));
					films.updateOne(Filters.eq("_id", filmId),
							new org.bson.Document("$push", new org.bson.Document("favorited_by", userId)));
				} else {
					// System.out.println("Cancello");
					users.updateOne(
							Filters.and(Filters.eq("username", user.getUsername()),
									Filters.eq("watched_films.film_id", filmId)),
							Updates.set("watched_films.$.favorite", false));
					films.updateOne(Filters.eq("_id", filmId),
							new org.bson.Document("$pull", new org.bson.Document("favorited_by", userId)));
				}
				return "Favorite list updated!";
			}
		};
		try {
			clientSession.withTransaction(txnBody);
			result = true;
		} finally {
			clientSession.close();
		}
		return result;
	}

	@Override
	public List<BasicFilm> searchFilms(String search, int year) {
		List<BasicFilm> result = new ArrayList<>();
		MongoCursor<Document> cursor;
		if (year == -1 && search.equals(""))// displays all films
		{
			cursor = films.find(Filters.gt("year", 1900))
					.projection(Projections.include("italian_title", "runtime", "year")).iterator();
		} else if (year == -1 && !search.equals(""))// displays films that matches the entered string (all years)
			cursor = films.find(Filters.text(search))
					.projection(Projections.include("italian_title", "runtime", "year")).iterator();

		else if (year != -1 && search.equals(""))//// displays films of a given year
			cursor = films.find(Filters.eq("year", year))
					.projection(Projections.include("italian_title", "runtime", "year")).iterator();

		else //// displays films that matches the entered string and the given year
			cursor = films.find(Filters.and(Filters.text(search), Filters.eq("year", year)))
					.projection(Projections.include("italian_title", "runtime", "year")).iterator();

		Integer year1 = 0;
		Integer runtime1 = 0;
		String title = "";
		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				String film_id = doc.getObjectId("_id").toString();
				title = doc.getString("italian_title");
				try {
					year1 = doc.getInteger("year");
				} catch (NullPointerException e) {
				}
				try {
					runtime1 = ((Number) doc.get("runtime")).intValue();
				} catch (NullPointerException e) {
					runtime1 = 0;
				}
				try {
					result.add(new BasicFilm(film_id, year1, runtime1, title));
				} catch (NullPointerException e) {
				}
			}
		} finally {
			cursor.close();
		}
		return result;
	}

	@Override
	public int checkLogin(String username, String password) {
		MongoCursor<org.bson.Document> cursor = users
				.find(Filters.and(Filters.eq("username", username), Filters.eq("password", password)))
				.projection(Projections.fields(Projections.include("_id"), Projections.include("role"))).iterator();
		int role = -1;
		try {
			if (cursor.hasNext()) {
				Document doc = cursor.next();
				role = doc.getInteger("role");
			}
		} finally {
			cursor.close();
		}

		return role;
	}

	@Override
	public boolean changePassword(String id, String old, String newP) {
		ObjectId user_id = new ObjectId(id);
		UpdateResult doc = users.updateOne(Filters.and(Filters.eq("_id", user_id), Filters.eq("password", old)),
				Updates.set("password", newP));
		long count = doc.getMatchedCount();
		if (count == 1)
			return true;
		return false;
	}

	@Override
	public Film getMoreComments(int index, Film current) {
		String filmId = current.getFilm_id();
		ObjectId id = new ObjectId(filmId);
		MongoCursor<Document> cursor = comments
				.aggregate(Arrays.asList(Aggregates.match(Filters.eq("film_id", id)),
						Aggregates.sort(Sorts.descending("date")), Aggregates.skip(index), Aggregates.limit(10)))
				.iterator();
		try {
			while (cursor.hasNext()) {
				Document comment = cursor.next();
				String comment_id = comment.getObjectId("_id").toString();
				String film_id = comment.getObjectId("film_id").toString();
				String text = comment.getString("text");
				String author = comment.getString("author");
				Date date = comment.getDate("date");
				Integer comment_points = comment.getInteger("comment_points");
				String type = comment.getString("type");
				Integer user_rating = comment.getInteger("user_rating");
				Comment comment1 = new Comment(film_id, text, author, date, type);
				comment1.setComment_points(comment_points);
				comment1.setUser_rating(user_rating);
				comment1.setComment_id(comment_id);
				current.getComments().add(comment1);
			}
		} finally {
			cursor.close();
		}
		return current;
	}

	@Override
	public boolean setRequestStatusById(User user, String title, int outcome) {
		boolean result = false;
		ObjectId user_id = new ObjectId(user.getUserId());
		int role_target = 0;
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody<String> txnBody = new TransactionBody<String>() {
			public String execute() {
				users.updateOne(Filters.and(Filters.eq("username", user.getUsername()),
						Filters.eq("messages.film_title", title)), Updates.set("messages.$.status", outcome));
				users.updateOne(
						Filters.and(Filters.eq("username", "admin"),
								Filters.elemMatch("messages",
										Filters.and(Filters.eq("user_id", user_id), Filters.eq("film_title", title)))),
						Updates.set("messages.$.status", outcome));
				return "Lo stato della richiesta è stato modificato";
			}
		};
		try {
			clientSession.withTransaction(txnBody);
			result = true;
		} finally {
			clientSession.close();
		}
		return result;
	}

	@Override
	public boolean checkRequest(User user, String title_requested) {
		boolean result = false;
		Object user_id = new ObjectId(user.getUserId());
		MongoCursor<org.bson.Document> cursor = users
				.find(Filters.and(Filters.eq("_id", user_id), Filters.eq("messages.film_title", title_requested)))
				.iterator();
		try {
			if (cursor.hasNext()) {
				result = true;
			}
		} finally {
			cursor.close();
		}
		return result;
	}

	@Override
	public int checkLikedComment(User user, Comment comment) {
		ObjectId commentoid = new ObjectId(comment.getComment_id());
		MongoCursor<org.bson.Document> cursor = users.find(Filters
				.and(Filters.eq("liked_comments.comment_id", commentoid), Filters.eq("username", user.getUsername())))
				.iterator();
		int result = 0;
		try {
			if (cursor.hasNext()) {
				Document user_i = cursor.next();
				List<Document> liked_comments = (List<Document>) user_i.get("liked_comments");
				for (int j = 0; j < liked_comments.size(); j++) {
					String comments_id = liked_comments.get(j).getObjectId("comment_id").toString();
					if (comment.getComment_id().equals(comments_id) == true) {
						result = liked_comments.get(j).getInteger("action");
					}
				}
			}
		} finally {
			cursor.close();
		}
		return result;
	}

	@Override
	public String getUsernameById(String UserId) {
		ObjectId User_Id = new ObjectId(UserId);
		MongoCursor<org.bson.Document> cursor = users.find(Filters.and(Filters.eq("_id", User_Id))).iterator();
		String result = null;
		try {
			if (cursor.hasNext()) {
				Document user_i = cursor.next();
				result = user_i.getString("username");
			}
		} finally {
			cursor.close();
		}
		return result;
	}

	@Override
	public Film get_film(String film_id) { // chiamata quando si seleziona un certo film e si vogliono sapere tutte le
											// informazioni
		ObjectId film_oid = new ObjectId(film_id);
		MongoCursor<Document> cursor = films.find(Filters.eq("_id", film_oid)).iterator();
		Film result = new Film();
		try {
			if (cursor.hasNext()) {
				Document film = cursor.next();
				String original_title;
				String italian_tile;
				String synopsis;
				Integer year;
				Integer runtime;
				Number box_office;
				try {
					original_title = film.getString("original_title");
				} catch (NullPointerException e) {
					original_title = "";
				}
				try {
					italian_tile = film.getString("italian_title");
				} catch (NullPointerException e) {
					italian_tile = "";
				}
				try {
					synopsis = film.getString("synopsis");
				} catch (NullPointerException e) {
					synopsis = "";
				}
				try {
					year = film.getInteger("year");
				} catch (NullPointerException e) {
					year = 0;
				}
				try {
					runtime = ((Number) film.get("runtime")).intValue();
				} catch (NullPointerException e) {
					runtime = 0;
				}
				try {
					box_office = ((Number) film.get("box_office"));
				} catch (NullPointerException e) {
					box_office = 0;
				}
				result.setFilm_id(film_id);
				result.setOriginal_title(original_title);
				result.setItalian_title(italian_tile);
				result.setSynopsis(synopsis);
				result.setYear(year);
				result.setRuntime(runtime);
				result.setBox_office(box_office);
				ArrayList<String> countries_list = new ArrayList<>();
				try {
					Object countries = film.get("countries");
					if (countries instanceof ArrayList) {
						countries_list = (ArrayList<String>) countries;

					}
				} catch (NullPointerException e) {
				}
				result.setCountries(countries_list);

				ArrayList<String> awards_list = new ArrayList<>();
				try {
					Object awards = film.get("awards");
					if (awards instanceof ArrayList) {
						awards_list = (ArrayList<String>) awards;

					}
				} catch (NullPointerException e) {
				}
				result.setAwards(awards_list);

				ArrayList<String> genres_list = new ArrayList<>();
				try {
					Object genres = film.get("genres");
					if (genres instanceof ArrayList) {
						genres_list = (ArrayList<String>) genres;
					}
				} catch (NullPointerException e) {
				}
				result.setGenres(genres_list);
				ArrayList<String> directors_list = new ArrayList<>();
				try {
					Object directors = film.get("directors");
					if (directors instanceof ArrayList) {
						directors_list = (ArrayList<String>) directors;
					}
				} catch (NullPointerException e) {
				}
				result.setDirectors(directors_list);
				ArrayList<Actor> cast_ = new ArrayList<>();
				try {
					Object cast = film.get("cast");
					if (cast instanceof ArrayList) {
						ArrayList<Document> cast_list = (ArrayList<Document>) cast;
						for (Document actor : cast_list) {
							String name = actor.getString("name");
							String role = actor.getString("role");
							cast_.add(new Actor(name, role));
						}
					}
				} catch (NullPointerException e) {
				}
				result.setCast(cast_);
				/*
				 * try { Object comments = film.get("comments"); if (comments instanceof
				 * ArrayList) { ArrayList<ObjectId> comment_oids = (ArrayList<ObjectId>)
				 * comments; ArrayList<String> comment_ids = new ArrayList<>(); for (ObjectId
				 * comment : comment_oids) { comment_ids.add(comment.toString()); }
				 * result.setComments(getCommentsById(c )); } } catch (NullPointerException e) {
				 * e.printStackTrace(); result.setComments(new ArrayList<Comment>()); }
				 */
				try {
					Object ratings = film.get("ratings");
					if (ratings instanceof Document) {
						Map<String, Float> ratings_map = new HashMap<>();
						Document doc = (Document) ratings;
						try {
							ratings_map.put("imdb_rating", ((Number) doc.get("imdb_rating")).floatValue());
						} catch (NullPointerException e) {
						}
						try {
							ratings_map.put("comingsoon_rating", ((Number) doc.get("comingsoon_rating")).floatValue());
						} catch (NullPointerException e) {
						}
						try {
							ratings_map.put("mymovies_rating", ((Number) doc.get("mymovies_rating")).floatValue());
						} catch (NullPointerException e) {
						}
						try {
							ratings_map.put("application_rating",
									((Number) doc.get("application_rating")).floatValue());
						} catch (NullPointerException e) {
						}
						result.setRatings(ratings_map);
					}
				} catch (NullPointerException e) {
					result.setRatings(new HashMap<String, Float>());
				}
				try {
					Object platform = film.get("available_on");
					if (platform instanceof ArrayList) {
						ArrayList<Platform> plat_ = new ArrayList<>();
						ArrayList<Document> plat_list = (ArrayList<Document>) platform;
						for (Document p : plat_list) {
							String name = p.getString("platform");
							float price = ((Number) p.get("price")).floatValue();
							plat_.add(new Platform(name, price));
						}
						result.setPlatforms(plat_);
					}
				} catch (NullPointerException e) {
					result.setPlatforms(new ArrayList<Platform>());
				}
			}

		} finally {
			cursor.close();
		}
		return result;
	}

	@Override
	public BasicFilm getBasicFilm(String film_id) {
		ObjectId filmId = new ObjectId(film_id);
		MongoCursor<Document> cursor = films.find(Filters.eq("_id", filmId))
				.projection(Projections.include("italian_title", "runtime", "year")).iterator();
		Integer year1 = null;
		Integer runtime1 = null;
		String title = "";
		try {
			if (cursor.hasNext()) {

				Document doc = cursor.next();
				title = doc.getString("italian_title");
				try {
					year1 = doc.getInteger("year");
				} catch (NullPointerException e) {
				}
				try {
					runtime1 = ((Number) doc.get("runtime")).intValue();
					if (runtime1 == null)
						runtime1 = 0;
				} catch (NullPointerException e) {
					runtime1 = 0;
				}
			}
		} finally {
			cursor.close();
		}

		return new BasicFilm(film_id, year1, runtime1, title);
	}

	@Override
	public List<BasicFilm> getBasicFilmsIndex(int index) {
		List<BasicFilm> basicFilms = new ArrayList<>();
		MongoCursor<Document> cursor = films
				.aggregate(Arrays.asList(Aggregates.sort(Sorts.ascending("italian_title")), Aggregates.skip(index * 40),
						Aggregates.limit(40),
						Aggregates
								.project(Projections.fields(Projections.include("italian_title", "year", "runtime")))))
				.iterator();
		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				String film_id = doc.getObjectId("_id").toString();
				String title = doc.getString("italian_title");
				int year1 = 0;
				try {
					year1 = doc.getInteger("year");
				} catch (NullPointerException e) {
				}
				Integer runtime1;
				try {
					runtime1 = ((Number) doc.get("runtime")).intValue();
				} catch (NullPointerException e) {
					runtime1 = 0;
				}
				BasicFilm film = new BasicFilm(film_id, year1, runtime1, title);
				basicFilms.add(film);
			}
		} finally {
			cursor.close();
		}
		return basicFilms;
	}

	// Funzione utilizzata per aggiungere una richiesta di aggiunta di un film
	@Override
	public boolean addRequest(final String film_title, User user, Request request) {
		boolean result = false;
		final ObjectId userId = new ObjectId(user.getUserId());
		final Date date = request.getDate();
		final Byte status = request.getStatus();
		final ObjectId requestId = new ObjectId();

		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody txnBody = new TransactionBody<String>() {
			public String execute() {
				Document request_user = new Document("_id", requestId).append("film_title", film_title)
						.append("date", date).append("status", status);
				users.updateOne(Filters.eq("_id", userId), Updates.addToSet("messages", request_user));

				Document request_admin = new Document("_id", requestId).append("film_title", film_title)
						.append("date", date).append("status", status).append("user_id", userId);

				users.updateOne(Filters.eq("username", "admin"), Updates.addToSet("messages", request_admin));

				return "Request insert successfully!";
			}
		};
		try {
			clientSession.withTransaction(txnBody);
			result = true;
		} finally {
			clientSession.close();
		}
		return result;
	}

	double avg = 0;

	// Funzione utilizzata per aggiungere i film
	@Override
	public boolean addWatched(WatchedFilm watched, final User user) {
		boolean result = false;
		final ObjectId userId = new ObjectId(user.getUserId());
		final ObjectId filmId = new ObjectId(watched.getFilm_id());
		final Date date = watched.getAdded_on();
		final String italian_title = watched.getItalian_title();
		final int rate = watched.get_rate();
		final Date today = Calendar.getInstance().getTime();
		
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody<Boolean> txnBody = new TransactionBody<Boolean>() {
			boolean check = true;
			
			public Boolean execute() {
				MongoCursor<Document> cursor = films.find(Filters.and(Filters.eq("_id", filmId))).iterator();

				double total = 0;
				boolean empty = false;
				double size = 0;
				double prev_avg = 0;

				try {
					if (cursor.hasNext()) {
						Document film_i = cursor.next();
						try {
							List<Document> films_rated = (List<Document>) film_i.get("film_ratings");
							empty = films_rated.isEmpty();

							if (empty == false) {
								size = films_rated.size();
							}

							for (int k = 0; k < films_rated.size(); k++) {
								total = total + films_rated.get(k).getInteger("rating");
							}
							
							prev_avg = Math.floor((total / size) * 100) / 100;
									
						} catch (Exception e2) {
							// System.out.println("Il Film non è stato votato precedentemente");
						}

					}
				} finally {
					cursor.close();
				}

				avg = 0;

				if (empty) {
					avg = rate;
				} else {
					avg = (total + rate) / (size + 1);
					avg = Math.floor(avg * 100) / 100;
				}
				
				Document user_watchedMovie = new Document("film_id", filmId).append("italian_title", italian_title)
						.append("date", date).append("rating", rate);
				users.updateOne(Filters.eq("_id", userId), Updates.addToSet("watched_films", user_watchedMovie));

				Document film_rating_info = new Document("user", user.getUsername())
						.append("country", user.getCountry()).append("rating", rate);
				films.updateOne(Filters.eq("_id", filmId), Updates.addToSet("film_ratings", film_rating_info));

				films.updateOne(Filters.eq("_id", filmId),
						new Document("$set", new Document("ratings.application_rating", avg)));

				Document user_watchedBy = new Document("user_id", userId).append("date", today);
				films.updateOne(Filters.eq("_id", filmId), Updates.addToSet("watched_by", user_watchedBy));

				try (Session session = driver.session()) {
					session.writeTransaction(new TransactionWork<Boolean>() {
						@Override
						public Boolean execute(Transaction tx) {
							TimeZone tz = TimeZone.getTimeZone("UTC");
							DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
							df.setTimeZone(tz);
							String date = df.format(watched.getAdded_on());
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("username", user.getUsername());
							params.put("_id", watched.getFilm_id());
							params.put("date", date);
							params.put("rate", (float) rate);
							tx.run("MATCH (u: User {username: $username}), (f:Film {_id: $_id}) CREATE (u)-[add: ADDED {date: datetime($date), rating: $rate}]->(f)",
									params);
							tx.commit();
							return true;
						}
					});
					
				} catch (ServiceUnavailableException ex) {
					System.out.println("Unable to connect to Neo4j server!");
					check = false;
				}

				if (!check) {
					
					users.updateOne(Filters.eq("_id", userId), Updates.pull("watched_films", user_watchedMovie));
					
					films.updateOne(Filters.eq("_id", filmId), Updates.pull("film_ratings", film_rating_info));
					
					films.updateOne(Filters.eq("_id", filmId), Updates.pull("watched_by", user_watchedBy));
					
					if (empty)
						films.updateOne(Filters.eq("_id", filmId), Updates.unset("ratings.application_rating"));
					else {
						films.updateOne(Filters.eq("_id", filmId), Updates.set("ratings.application_rating", prev_avg));
					}
					
				}
				

				return check;
			}
		};
		try {
			result = clientSession.withTransaction(txnBody);
		} finally {
			clientSession.close();
		}

		return result;
	}

	@Override
	public boolean addComment(String film_id, User user, Comment comment) {
		boolean result = false;
		final ObjectId filmId = new ObjectId(film_id);
		final ObjectId userId = new ObjectId(user.getUserId());
		final String author = user.getUsername();
		final String commentText = comment.getText();
		final Date date = comment.getDate();
		final ObjectId commentId = new ObjectId(comment.getComment_id());
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody txnBody = new TransactionBody<String>() {
			public String execute() {
				Document comment = new Document("_id", commentId).append("film_id", filmId).append("author", author)
						.append("text", commentText).append("date", date).append("comment_points", 0)
						.append("type", "application_comment");
				comments.insertOne(comment);
				users.updateOne(Filters.eq("_id", userId), Updates.addToSet("user_comments", commentId));
				films.updateOne(Filters.eq("_id", filmId), Updates.addToSet("comments", commentId));
				return "Comment insert successful!";
			}
		};
		try {
			clientSession.withTransaction(txnBody);
			result = true;
		} finally {
			clientSession.close();
		}
		return result;
	}

	@Override
	public boolean removeComment(String comment_id) {
		boolean result = false;
		ObjectId commentId = new ObjectId(comment_id);
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody txnBody = new TransactionBody<String>() {
			public String execute() {
				comments.deleteOne(Filters.eq("_id", commentId));
				users.updateOne(Filters.eq("user_comments", commentId), Updates.pull("user_comments", commentId));
				films.updateOne(Filters.eq("comments", commentId), Updates.pull("comments", commentId));
				return "Comment delete successful!";
			}
		};
		try {
			clientSession.withTransaction(txnBody);
			result = true;
		} finally {
			clientSession.close();
		}
		return result;
	}

	@Override
	public boolean commentAction(User user, Comment comment, int action, int userAction) {
		boolean result = false;
		ObjectId commentId = new ObjectId(comment.getComment_id());
		String username = user.getUsername();
		Document liked_comment = new Document("comment_id", commentId).append("action", userAction);
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody txnBody = new TransactionBody<String>() {
			public String execute() {
				comments.updateOne(Filters.eq("_id", commentId), Updates.inc("comment_points", action));
				users.updateOne(Filters.eq("username", username),
						Updates.pull("liked_comments", new Document("comment_id", commentId)));
				users.updateOne(Filters.eq("username", username), Updates.addToSet("liked_comments", liked_comment));
				return "Comment action completed successfully!";
			}
		};
		try {
			clientSession.withTransaction(txnBody);
			result = true;
		} finally {
			clientSession.close();
		}
		return result;
	}

	@Override
	public User getUser(String username) {
		MongoCursor<Document> cursor = users.find(Filters.eq("username", username)).iterator();
		User user = new User();
		try {
			if (cursor.hasNext()) {
				Document result = cursor.next();
				user.setUserId(result.getObjectId("_id").toString());
				user.setUsername(result.getString("username"));
				Document doc1 = (Document) result.get("general_info");
				user.setFirst_name(doc1.getString("first_name"));
				user.setLast_name(doc1.getString("last_name"));
				user.setCountry(doc1.getString("country"));
				user.setYear_of_birth(doc1.getInteger("year_of_birth"));
				List<WatchedFilm> user_watched = new ArrayList<>();
				try {
					Object watched_films = result.get("watched_films");
					if (watched_films instanceof ArrayList) {
						ArrayList<?> watched_array = (ArrayList<?>) watched_films;
						for (Object watched : watched_array) {
							if (watched instanceof Document) {
								try {
									String film_id = ((Document) watched).getObjectId("film_id").toString();
									String title = ((Document) watched).getString("italian_title");
									Date date = ((Document) watched).getDate("date");
									boolean favorite = ((Document) watched).getBoolean("favorite", false);
									int rating = ((Document) watched).getInteger("rating");
									user_watched.add(new WatchedFilm(film_id, title, date, rating, favorite));
								} catch (NullPointerException e) {
								}
							}
						}
						user.setWatched_films(user_watched);
						;
					}
				} catch (NullPointerException e) {
				}

			}

			user.setUser_comments(getCommentsByAuthor(user.getUsername())); // get comments for the user

		} finally {
			cursor.close();
		}

		return user;
	}

	@Override
	public Admin getAdmin(String username) {
		MongoCursor<Document> cursor = users.find(Filters.eq("username", username)).iterator();
		Admin user = new Admin();
		try {
			if (cursor.hasNext()) {
				Document result = cursor.next();
				user.setUserId(result.getObjectId("_id").toString());
				user.setUsername(result.getString("username"));
				Document doc1 = (Document) result.get("general_info");
				user.setFirst_name(doc1.getString("first_name"));
				user.setLast_name(doc1.getString("last_name"));
				user.setCountry(doc1.getString("country"));
				user.setYear_of_birth(doc1.getInteger("year_of_birth"));
			}
		} finally {
			cursor.close();
		}

		return user;
	}

	@Override
	public List<Comment> getCommentsByAuthor(String author) {
		List<Comment> result = new ArrayList<>();
		MongoCursor<Document> cursor = comments.find(Filters.eq("author", author)).iterator();
		try {
			while (cursor.hasNext()) {
				Document comment = cursor.next();
				String comment_id = comment.getObjectId("_id").toString();
				String film_id = comment.getObjectId("film_id").toString();
				String text = comment.getString("text");
				Date date = comment.getDate("date");
				Integer comment_points = comment.getInteger("comment_points");
				String type = comment.getString("type");
				Integer user_rating = comment.getInteger("user_rating");
				Comment comment1 = new Comment(film_id, text, author, date, type);
				comment1.setComment_points(comment_points);
				comment1.setUser_rating(user_rating);
				comment1.setComment_id(comment_id);
				result.add(comment1);
			}
		} finally {
			cursor.close();
		}
		return result;
	}

	@Override
	public List<Document> mostWatchedByGenre(String genre) {
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		multiIdMap.put("italian_title", "$italian_title");
		multiIdMap.put("genres", "$genres");
		multiIdMap.put("id", "$_id");
		Document groupFields = new Document(multiIdMap);
		List<Document> top = new ArrayList<Document>();
		MongoCursor<Document> cursor = films.aggregate(Arrays.asList(Aggregates.match(Filters.in("genres", genre)),
				Aggregates.unwind("$genres"), Aggregates.match(Filters.eq("genres", genre)),
				Aggregates.unwind("$watched_by"), Aggregates.group(groupFields, Accumulators.sum("watched_by", 1)),
				Aggregates.sort(Sorts.descending("watched_by")), Aggregates.limit(10),
				Aggregates.project(Projections.fields(Projections.include("_id.italian_title"))))).iterator();
		try {
			while (cursor.hasNext()) {
				org.bson.Document doc = cursor.next();
				top.add(doc);
			}

		} finally {
			cursor.close();
		}

		return top;
	}

	@Override
	public List<Document> ViewByNationality(String film_id) {
		List<Document> result = new ArrayList<Document>();
		MongoCursor<Document> cursor = films
				.aggregate(Arrays.asList(Aggregates.match(Filters.eq("_id", new ObjectId(film_id))),
						Aggregates.unwind("$film_ratings"),
						Aggregates.group("$film_ratings.country", Accumulators.sum("viewers", 1)),
						Aggregates.project(Projections.fields(Projections.include("film_ratings.country", "viewers")))))
				.iterator();
		try {
			int total_visual = 0;
			while (cursor.hasNext()) {
				org.bson.Document doc = cursor.next();
				total_visual = total_visual + doc.getInteger("viewers");
				result.add(doc);
			}

			Document total = new Document("total_visual", total_visual);
			result.add(total);

		} finally {
			cursor.close();
		}

		return result;
	}

	@Override
	public List<String> mostPopular() {
		List<String> top = new ArrayList<>();
		Map<String, Object> multiIdMap = new HashMap<String, Object>();
		multiIdMap.put("italian_title", "$italian_title");
		multiIdMap.put("id", "$_id");
		Document groupFields = new Document(multiIdMap);
		Calendar c = Calendar.getInstance();
		int month = c.get(Calendar.MONTH);
		int month_prec;
		if (month != 0)
			month_prec = month - 1;
		else
			month_prec = 11;
		int year = c.get(Calendar.YEAR);
		if (month == 0)
			year = year - 1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		c.set(year, month_prec, day);
		Date date = c.getTime();
		// System.out.println(c.getTime().toString());
		MongoCursor<Document> cursor = films.aggregate(Arrays.asList(Aggregates.unwind("$watched_by"),
				Aggregates.match(Filters.gte("watched_by.date", date)),
				Aggregates.group(groupFields, Accumulators.sum("watched_by", 1)),
				Aggregates.sort(Sorts.descending("watched_by")),
				Aggregates.project(Projections.fields(Projections.include("_id.italian_title"))), Aggregates.limit(20)

		)).iterator();
		try {
			while (cursor.hasNext()) {
				org.bson.Document doc = cursor.next();
				top.add(((Document) doc.get("_id")).getString("italian_title"));
			}
		} finally {
			cursor.close();
		}
		return top;
	}

	@Override
	public List<BasicFilm> findBasicFilms(String italianTitle) {
		List<BasicFilm> basicFilms = new ArrayList<>();
		MongoCursor<Document> cursor = films.find(Filters.eq("italian_title", italianTitle))
				.projection(Projections.include("original_title", "italian_title", "runtime", "year")).iterator();
		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				String film_id = doc.getObjectId("_id").toString();
				String italian_title = doc.getString("italian_title");
				String original_title = doc.getString("original_title");
				int year1 = 0;
				try {
					year1 = doc.getInteger("year");
				} catch (NullPointerException e) {
				}
				Integer runtime1;
				try {
					runtime1 = ((Number) doc.get("runtime")).intValue();
				} catch (NullPointerException e) {
					runtime1 = 0;
				}
				BasicFilm film = new BasicFilm(film_id, year1, runtime1, italian_title);
				film.setOriginal_title(original_title);
				basicFilms.add(film);
			}
		} finally {
			cursor.close();
		}
		return basicFilms;
	}

	@Override
	public List<BasicFilm> findBasicFilms(String italianTitle, int year) {
		List<BasicFilm> basicFilms = new ArrayList<>();
		MongoCursor<Document> cursor = films
				.find(Filters.and(Filters.eq("year", year), Filters.eq("italian_title", italianTitle)))
				.projection(Projections.include("original_title", "italian_title", "runtime", "year")).iterator();
		try {
			while (cursor.hasNext()) {
				Document doc = cursor.next();
				String film_id = doc.getObjectId("_id").toString();
				String italian_title = doc.getString("italian_title");
				String original_title = doc.getString("original_title");
				int year1 = 0;
				try {
					year1 = doc.getInteger("year");
				} catch (NullPointerException e) {
				}
				Integer runtime1;
				try {
					runtime1 = ((Number) doc.get("runtime")).intValue();
				} catch (NullPointerException e) {
					runtime1 = 0;
				}
				BasicFilm film = new BasicFilm(film_id, year1, runtime1, italian_title);
				film.setOriginal_title(original_title);
				basicFilms.add(film);
			}
		} finally {
			cursor.close();
		}
		return basicFilms;
	}

	@Override
	public boolean deleteFilm(String film_id) {
		boolean result = false;
		ObjectId filmId = new ObjectId(film_id);
		final ClientSession clientSession = mongoClient.startSession();
		TransactionBody<Boolean> txnBody = new TransactionBody<Boolean>() {
			boolean check = true;
			Map<ObjectId, Document> watchedByMap = new HashMap<ObjectId, Document>();
			List<Document> filmComments = new ArrayList<>();
			public Boolean execute() {
				Document filmToDelete = films.find(Filters.eq("_id", filmId)).first();
				films.deleteOne(Filters.eq("_id", filmId));
				MongoCursor<Document> cursor = users.find(Filters.eq("watched_films.film_id", filmId)).projection(Projections.fields(Projections.include("watched_films.$"))).iterator();
				try {
					while (cursor.hasNext()) {
						Document document = cursor.next();
						ObjectId userId = document.getObjectId("_id");
						ArrayList<Object> watchedList = (ArrayList<Object>) document.get("watched_films");
						for (Object item : watchedList) {
							Document watched = (Document) item;
							watchedByMap.put(userId, watched);
						}		
					}
				} finally {
					cursor.close();
				}
				users.updateMany(Filters.eq("watched_films.film_id", filmId),
						Updates.pull("watched_films", new Document("film_id", filmId)));
				MongoCursor<Document> cursor2 = comments.find(Filters.eq("film_id", filmId)).iterator();
				try {
					while (cursor2.hasNext()) {
						Document comment = cursor2.next();
						filmComments.add(comment);
					}
				} finally {
					cursor2.close();
				}
				comments.deleteMany(Filters.eq("film_id", filmId));
				
				try (Session session = driver.session()) {
					session.writeTransaction(new TransactionWork<Boolean>() {
						@Override
						public Boolean execute(Transaction tx) {
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("_id", film_id);
							tx.run("MATCH (f: Film {_id: $_id}) DETACH DELETE f", params);
							tx.commit();
							return true;
						}
					});
				} catch (ServiceUnavailableException ex) {
					System.out.println("Unable to connect to Neo4j server!");
					check = false;
				}
				
				if (!check) {
					
					films.insertOne(filmToDelete);
					
					for (Map.Entry<ObjectId, Document> entry : watchedByMap.entrySet()) {
					    ObjectId userId = entry.getKey();
					    Document watched = entry.getValue();
					    users.updateOne(Filters.eq("_id", userId), Updates.addToSet("watched_films", watched));
					}
					
					if (!filmComments.isEmpty())
						comments.insertMany(filmComments);
				}
				
				return check;
			}
		};
		try {
			result = clientSession.withTransaction(txnBody);
		} finally {
			clientSession.close();
		}
		return result;
	}

	@Override
	public String[] getMovieYears() {
		List<String> yearsStringList = new ArrayList<>();
		try {
			ArrayList<Integer> movieYearsList = (ArrayList<Integer>) films.distinct("year", Integer.class)
					.into(new ArrayList<Integer>());
			for (Integer integer : movieYearsList) {
				try {
					yearsStringList.add(Integer.toString(integer));
				} catch (NullPointerException e) {
				}
			}
			yearsStringList.add("all years");
			Collections.reverse(yearsStringList);
		} catch (NullPointerException e) {
		}
		return yearsStringList.toArray(new String[0]);
	}

	@Override
	public String[] getGenres() {
		List<String> genresList = new ArrayList<>();
		try {
			genresList = (ArrayList<String>) films.distinct("genres", String.class).into(new ArrayList<String>());
		} catch (NullPointerException e) {
		}
		return genresList.toArray(new String[0]);
	}

	@Override
	public List<String> moreLikeThis(String filmId) {
		List<String> likeThis = new ArrayList<>();
		try (Session session = driver.session()) {
			likeThis = session.readTransaction(new TransactionWork<List<String>>() {
				@Override
				public List<String> execute(Transaction tx) {
					List<String> resultQuery = new ArrayList<>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("_id", filmId);
					Result result = tx.run(
							"CALL { MATCH (m1:Film {_id : $_id})-[r1:LABELED]->(g1:Genre) RETURN count(r1) AS NumGenresTarget  }  "
									+ "MATCH (m:Film {_id: $_id}) " + "MATCH (m)-[:LABELED]->(g:Genre) "
									+ "MATCH (movie:Film)-[r:LABELED]->(g) "
									+ "MATCH (movie)-[r2:LABELED]->(g1: Genre) " + "WHERE m <> movie "
									+ "WITH movie, count(DISTINCT r) AS commonGenres, count(DISTINCT r2) AS totalGenresAdvice, NumGenresTarget "
									+ "RETURN movie.italian_title "
									+ "ORDER BY (NumGenresTarget - commonGenres ), totalGenresAdvice " + "LIMIT 35",
							params);
					while (result.hasNext()) {
						resultQuery.add(result.next().get(0).asString());
					}
					return resultQuery;
				}
			});
		} catch (ServiceUnavailableException ex) {
			System.out.println("Errore nella connessione - SERVER NON RAGGIUNGIBILE");
			exit();
			System.exit(-1);
		}
		return likeThis;

	}

	@Override
	public List<BasicFilm> similarRating(String username) {
		List<BasicFilm> similar = new ArrayList<>();
		try (Session session = driver.session()) {
			similar = session.readTransaction(new TransactionWork<List<BasicFilm>>() {
				@Override
				public List<BasicFilm> execute(Transaction tx) {
					List<BasicFilm> resultQuery = new ArrayList<>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("username", username);
					Result result = tx.run("MATCH (me:User {username: $username})-[my:ADDED]->(m:Film) "
							+ "MATCH (other:User)-[their:ADDED]->(m) " + "WHERE me <> other "
							+ "AND abs(my.rating - their.rating) < 2 " + "WITH other,m,me "
							+ "MATCH (other)-[otherRating:ADDED]->(movie:Film) "
							+ "WHERE NOT EXISTS ((me)-[:ADDED]->(movie)) "
							+ "WITH avg(otherRating.rating) AS avgRating, movie "
							+ "RETURN movie.italian_title, movie._id " + "ORDER BY avgRating desc " + "LIMIT 35",
							params);
					while (result.hasNext()) {
						resultQuery.add(
								new BasicFilm(result.peek().get(1).asString(), 0, 0, result.peek().get(0).asString()));
						result.next();
					}
					return resultQuery;
				}
			});
		} catch (ServiceUnavailableException ex) {
			System.out.println("Errore nella connessione - SERVER NON RAGGIUNGIBILE");
		}
		return similar;
	}

	@Override
	public List<BasicFilm> sameActors(String username) {
		List<BasicFilm> actors = new ArrayList<>();
		try (Session session = driver.session()) {
			actors = session.readTransaction(new TransactionWork<List<BasicFilm>>() {
				@Override
				public List<BasicFilm> execute(Transaction tx) {
					List<BasicFilm> resultQuery = new ArrayList<>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("username", username);
					Result result = tx.run("MATCH (u:User {username: $username})-[:ADDED]->(f:Film) "
							+ "MATCH (a:Actor)-[:ACTED_IN]->(f) " + "MATCH (a)-[r:ACTED_IN]->(movie:Film) "
							+ "WHERE NOT EXISTS ((u)-[:ADDED]->(movie)) "
							+ "WITH movie, count(DISTINCT r) AS commonActors "
							+ "RETURN movie.italian_title, movie._id " + "ORDER BY commonActors DESC " + "LIMIT 15",
							params);
					while (result.hasNext()) {
						resultQuery.add(
								new BasicFilm(result.peek().get(1).asString(), 0, 0, result.peek().get(0).asString()));
						result.next();
					}
					return resultQuery;
				}
			});
		} catch (ServiceUnavailableException ex) {
			System.out.println("Errore nella connessione - SERVER NON RAGGIUNGIBILE");
		}
		return actors;
	}

	@Override
	public List<BasicFilm> sameDirectors(String username) {
		List<BasicFilm> directors = new ArrayList<>();
		try (Session session = driver.session()) {
			directors = session.readTransaction(new TransactionWork<List<BasicFilm>>() {
				@Override
				public List<BasicFilm> execute(Transaction tx) {
					List<BasicFilm> resultQuery = new ArrayList<>();
					Map<String, Object> params = new HashMap<String, Object>();
					params.put("username", username);
					Result result = tx.run("MATCH (u:User {username: $username})-[:ADDED]->(f:Film) "
							+ "MATCH (d:Director)-[:DIRECTED]->(f) " + "MATCH (d)-[r:DIRECTED]->(movie:Film) "
							+ "WHERE NOT EXISTS ((u)-[:ADDED]->(movie)) "
							+ "WITH movie, count(DISTINCT r) AS commonDirectors "
							+ "RETURN movie.italian_title, movie._id " + "ORDER BY commonDirectors DESC " + "LIMIT 20 ",
							params);
					while (result.hasNext()) {
						resultQuery.add(
								new BasicFilm(result.peek().get(1).asString(), 0, 0, result.peek().get(0).asString()));
						result.next();
					}
					return resultQuery;
				}
			});
		} catch (ServiceUnavailableException ex) {
			System.out.println("Errore nella connessione - SERVER NON RAGGIUNGIBILE");
		}
		return directors;
	}

	@Override
	public void exit() {
		System.out.println("Closing connection to database...");
		mongoClient.close();
		driver.close();
	}

}