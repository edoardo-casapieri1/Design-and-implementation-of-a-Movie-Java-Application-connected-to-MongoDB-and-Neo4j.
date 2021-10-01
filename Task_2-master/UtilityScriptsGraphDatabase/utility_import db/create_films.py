import json
import traceback

films = []

with open("films.json", "r", encoding="utf-8") as fin:
    for line in fin:
        film = json.loads(line)
        _id = film['_id']['$oid']
        original_title = film['original_title']
        italian_title = film['italian_title']
        year = film['year']
        film_ = {'_id': _id, 'original_title': original_title, 'italian_title': italian_title,'year': year}
        films.append(film_)

with open('films.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for film in films:
        fout.write("{}*{}*{}*{}\n"
                   .format((film['_id']),(film['original_title']),(film['italian_title']),(film['year'])))
