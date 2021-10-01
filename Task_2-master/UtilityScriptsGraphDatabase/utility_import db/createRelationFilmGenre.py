import json
import traceback

films_genres = []

with open("films.json", "r", encoding="utf-8") as fin:
	for line in fin:
		film = json.loads(line)
		_id = film['_id']['$oid']
		num_genres = 0
		try:
			num_genres = len(film['genres'])
		except Exception as e:
			track = traceback.format_exc()
		if( num_genres != 0):
			for y in  range(num_genres):
				name = film['genres'][y]
				film_genre_ = {'_id': _id, 'name': name}
				films_genres.append(film_genre_)

with open('FilmGenres.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for film_genre in films_genres:
    	fout.write("{}*{}\n"
    		.format((film_genre['_id']),(film_genre['name'])))
