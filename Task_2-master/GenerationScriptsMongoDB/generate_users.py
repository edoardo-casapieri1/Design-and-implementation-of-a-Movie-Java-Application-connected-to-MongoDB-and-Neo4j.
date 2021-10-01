import json
import random
import datetime
import numpy as np
import string


def random_date():
    start = datetime.datetime(2019, 10, 1)
    end = datetime.datetime(2019, 12, 31)
    delta = end - start
    int_delta = (delta.days * 24 * 60 * 60) + delta.seconds
    random_second = random.randrange(int_delta)
    date = {'$date': (start + datetime.timedelta(seconds=random_second)).isoformat() + 'Z'}
    return date


def random_rating():
    mu, sigma = 6.5, 2.5
    rating = round(np.random.normal(mu, sigma))
    if rating > 10:
        rating = 10
    if rating < 1:
        rating = 1
    return rating


fids = []
with open("film_ids.json", "r") as ids:
    for line in ids:
        id_ = json.loads(line)
        fids.append(id_['_id'])
film_ids = tuple(fids)

names_list = []
with open("names.txt", "r") as fnames:
    for line in fnames:
        names_list.append(line.strip('\n'))
names = tuple(names_list)

coutries_list = []
with open("countries.txt", "r") as fc:
    for line in fc:
        coutries_list.append(line.strip('\n'))
countries = tuple(coutries_list)


def random_userdata():
    name = random.choice(names)
    f_name = name
    l_name = random.choice(names)
    uname = name.lower() + str(random.randrange(10)) + str(random.randrange(10)) + str(random.randrange(10))
    return f_name, l_name, uname


def random_password():
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=8))


for i in range(10000):
    user = {}
    first_name, last_name, username = random_userdata()
    country = random.choice(countries)
    password = random_password()
    year_of_birth = random.randrange(1940, 2006)
    general_info = {'first_name': first_name, 'last_name': last_name,
                    'country': country, 'year_of_birth': year_of_birth}
    user['username'] = username
    user['password'] = password
    user['general_info'] = general_info
    user['role'] = 1
    films_sample = random.sample(film_ids, random.randrange(3, 31))
    watched_films = []
    grades = []
    for film in films_sample:
        grade = {}
        viewed_film = {}
        grade['film_id'] = film
        grade['rating'] = random_rating()
        viewed_film['film_id'] = film
        viewed_film['date'] = random_date()
        watched_films.append(viewed_film)
        grades.append(grade)
    user['watched_films'] = watched_films
    watched_films_ = [d['film_id'] for d in watched_films]
    user['favorite_films'] = random.sample(watched_films_, random.randrange(len(watched_films_)))
    user['film_ratings'] = grades
    with open('users.json', 'a', encoding='utf8', errors='ignore') as fout:
        json.dump(user, fout)
        fout.write('\n')

