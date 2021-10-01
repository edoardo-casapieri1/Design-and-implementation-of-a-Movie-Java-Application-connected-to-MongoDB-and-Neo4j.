import json
import traceback

films_actors = []

with open("films.json", "r", encoding="utf-8") as fin:
	for line in fin:
		film = json.loads(line)
		_id = film['_id']['$oid']
		num_actors = 0
		try:
			num_actors = len(film['cast'])
		except Exception as e:
			track = traceback.format_exc()
		if( num_actors != 0):
			for y in  range(num_actors):
				name = film['cast'][y]['name']
				film_actor_ = {'_id': _id, 'name': name}
				films_actors.append(film_actor_)

with open('FilmActors.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for film_actor in films_actors:
    	fout.write("{}*{}\n"
    		.format((film_actor['_id']),(film_actor['name'])))