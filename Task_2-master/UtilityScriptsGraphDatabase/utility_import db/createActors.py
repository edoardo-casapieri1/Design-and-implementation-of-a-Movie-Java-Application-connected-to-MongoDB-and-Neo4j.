import json
import traceback

actors = []

def check_fun(str):
	result = 0
	for x in range(len(actors)):
		if(str == actors[x]['name']):
			result = 1
			break
	return result

with open("films.json", "r", encoding="utf-8") as fin:
	for line in fin:
		film = json.loads(line)
		num_actors = 0
		try:
			num_actors = len(film['cast'])
		except Exception as e:
			track = traceback.format_exc()
		if( num_actors != 0):
			for y in  range(num_actors):
				if(check_fun(film['cast'][y]['name']) == 0):
					actor_ = {'name': film['cast'][y]['name']}
					actors.append(actor_)

with open('actors.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for actor in actors:
    	fout.write("{}\n"
    		 .format((actor['name'])))
