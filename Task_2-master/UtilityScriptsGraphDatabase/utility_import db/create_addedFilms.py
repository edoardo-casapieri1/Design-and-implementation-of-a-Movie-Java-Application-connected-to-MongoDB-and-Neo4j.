import json
import traceback

relationshipAdded = []

with open("users.json", "r", encoding="utf-8") as fin:
    for line in fin:
      user = json.loads(line)
      _idUser = user['_id']['$oid']
      num_watched_films = 0
      try:
       	num_watched_films = len(user['watched_films'])
      except Exception as e:
       	track = traceback.format_exc()
      if( num_watched_films != 0):
       	for x in  range(len(user['watched_films'])):
          _idFilm = user['watched_films'][x]['film_id']['$oid']
          date = user['watched_films'][x]['date']['$date']
          rating = user['watched_films'][x]['rating']
          relationship_ = {'_idUser': _idUser, '_idFilm': _idFilm, 'date': date, 'rating': rating}
          relationshipAdded.append(relationship_)		

with open('relationshipAdded.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for rel in relationshipAdded:
        fout.write("{}*{}*{}*{}\n"
                   .format((rel['_idUser']),(rel['_idFilm']), (rel['date']) ,(rel['rating'])))


