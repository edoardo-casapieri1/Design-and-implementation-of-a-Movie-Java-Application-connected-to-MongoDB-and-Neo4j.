import json
import traceback

films_directors = []

with open("films.json", "r", encoding="utf-8") as fin:
	for line in fin:
		film = json.loads(line)
		_id = film['_id']['$oid']
		num_directors = 0
		try:
			num_directors = len(film['directors'])
		except Exception as e:
			track = traceback.format_exc()
		if( num_directors != 0):
			for y in  range(num_directors):
				name = film['directors'][y]
				film_director_ = {'_id': _id, 'name': name}
				films_directors.append(film_director_)

with open('FilmDirectors.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for film_director in films_directors:
    	fout.write("{}*{}\n"
    		.format((film_director['_id']),(film_director['name'])))