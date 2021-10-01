import json
import traceback

genres = []

def check_fun(str):
	result = 0
	for x in range(len(genres)):
		if(str == genres[x]['name']):
			result = 1
			break
	return result
	
with open("films.json", "r", encoding="utf-8") as fin:
	for line in fin:
		film = json.loads(line)
		num_genres = 0
		try:
			num_genres = len(film['genres'])
		except Exception as e:
			track = traceback.format_exc()
		if( num_genres != 0):
			for y in  range(num_genres):
				if(check_fun(film['genres'][y]) == 0):
					genre_ = {'name': film['genres'][y]}
					genres.append(genre_)

with open('genres.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for genre in genres:
    	fout.write("{}\n"
    		 .format((genre['name'])))


		
