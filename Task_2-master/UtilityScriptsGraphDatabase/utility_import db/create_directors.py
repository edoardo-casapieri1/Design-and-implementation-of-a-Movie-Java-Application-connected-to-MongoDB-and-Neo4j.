import json
import traceback

directors = []

def check_fun(str):
	result = 0
	for x in range(len(directors)):
		if(str == directors[x]['name']):
			result = 1
			break
	return result

with open("films.json", "r", encoding="utf-8") as fin:
	for line in fin:
		film = json.loads(line)
		num_directors = 0
		try:
			num_directors = len(film['directors'])
		except Exception as e:
			track = traceback.format_exc()
		if( num_directors != 0):
			for y in  range(num_directors):
				if(check_fun(film['directors'][y]) == 0):
					director_ = {'name': film['directors'][y]}
					directors.append(director_)

with open('directors.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for director in directors:
    	fout.write("{}\n"
    		 .format((director['name'])))

