from time import sleep
import urllib.request
import os
import re
import datetime
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from imdb import IMDb
import pymongo
from bson.objectid import ObjectId
from neo4j import GraphDatabase

try:
    os.makedirs('posters')
except FileExistsError:
    pass


def imdb_date_formatter(datestring):
    date_ = datestring.split()
    months = {
        "January": 1,
        "February": 2,
        "March": 3,
        "April": 4,
        "May": 5,
        "June": 6,
        "July": 7,
        "August": 8,
        "September": 9,
        "October": 10,
        "November": 11,
        "December": 12
    }
    day = int(date_[0])
    month = months.get(date_[1])
    year = int(date_[2])
    new_date = datetime.datetime(day=day, month=month, year=year)
    return new_date


def mymovies_date_formatter(datestring):
    date_ = datestring.split()
    months = {
        "gennaio": 1,
        "febbraio": 2,
        "marzo": 3,
        "aprile": 4,
        "maggio": 5,
        "giugno": 6,
        "luglio": 7,
        "agosto": 8,
        "settembre": 9,
        "ottobre": 10,
        "novembre": 11,
        "dicembre": 12
    }
    day = int(date_[1])
    month = months.get(date_[2])
    year = int(date_[3])
    new_date = datetime.datetime(day=day, month=month, year=year)
    return new_date


driver = webdriver.Chrome(executable_path="/Users/andreadidonato/Desktop/scraping/chromedriver")
films = []
comments_ = []

file = open("/Users/andreadidonato/Desktop/GitHub/Task_2/mongodb_2/scraping/film_list.txt", "r", encoding="utf-8")
ia = IMDb()
for line in file:
    line = line.strip()
    film_comments = []
    film = {}
    film_id = ObjectId()
    film['_id'] = film_id
    line = line.replace('\n', '').lower().title()
    try:
        movies = ia.search_movie(line)
        movie = movies[0]
        movie_id = movie.movieID
        movie_obj = ia.get_movie(movie_id)

        # print(sorted(movie_obj.keys()))
        print('Title:', end=" ")
        try:
            title = movie_obj.get('title')
            film['original_title'] = title
            film['italian_title'] = line
            print(title)
        except:
            pass

        print('Synopsis:')
        try:
            text_synopsis = movie_obj['plot'][0]
            synopsis = text_synopsis.split('::')
            film['synopsis'] = synopsis[0]
            print(synopsis[0])
        except:
            pass

        poster_url = movie.get('full-size cover url')

        filename = "".join([c for c in title if c.isalpha() or c.isdigit() or c == ' ']).rstrip().replace(' ', '_')
        path = 'posters/' + filename + '.jpg'
        urllib.request.urlretrieve(poster_url, path)
        film['poster'] = path

        print('Year:', end=" ")
        try:
            year = movie.get('year')
            film['year'] = year
            print(year)
        except:
            pass

        print('Countries:')
        try:
            countries = []
            for country in movie_obj['countries']:
                countries.append(country)
                print(country)
            film['countries'] = countries
        except:
            pass

        print('Genres:')
        try:
            genres = []
            for genre in movie_obj['genres']:
                genres.append(genre)
                print(genre)
            film['genres'] = genres
        except:
            pass

        try:
            runtime = int(movie_obj['runtimes'][0])
            film['runtime'] = runtime
        except:
            pass

        ratings = {}

        print('Grade:', end=" ")
        try:
            rate_imdb = movie_obj.get('rating')
            ratings['imdb_rating'] = float(rate_imdb)
        except:
            pass

        print("Imdb: " + str(rate_imdb) + "/10,", end=" ")

        driver.get("https://www.comingsoon.it/film/")
        try:
            hidden_form = driver.find_element_by_xpath('/html/body/div[3]/div[3]/div/div[1]/div[2]/div')
            driver.execute_script("arguments[0].click();", hidden_form)
            sleep(2)
            search = hidden_form.find_element_by_xpath(
                '/html/body/div[3]/div[3]/div/div[1]/div[3]/div/div/div[2]/div/div/div[1]/input')
            search.clear()
            search.send_keys(title)
            search.send_keys(Keys.RETURN)
            # sleep(1)
            link = driver.find_element_by_xpath('/html/body/div[3]/div[3]/div/div[1]/div[7]/div[2]/a').get_attribute(
                "href")
            driver.get(link)
            # sleep(1)
            substring = "di"
            for x in range(1, 4):
                try:
                    voto2 = driver.find_element_by_xpath(
                        '/html/body/div[3]/div[2]/div/div[2]/div[2]/div[3]/div[' + str(x) + ']/span').text
                    if substring in voto2:
                        break
                except:
                    pass

            for i in range(0, len(voto2)):
                if voto2[i] == 'd':
                    res = i + 1
                    break

            voto2_print = voto2[0:res - 1]
            voto2_print = voto2_print.replace(" ", "")
            ratings['comingsoon_rating'] = float((voto2_print).replace(',', '.')) * 2
            print("ComingSoon: " + voto2_print + "/5,", end=" ")

        except:
            print("CoomingSoon: " + ",", end=" ")

        driver.get("https://www.mymovies.it/database/")
        try:
            # search = driver.find_element_by_xpath('/html/body/div[9]/form/div[1]/div/input')
            # search.clear()
            # search.send_keys(line)
            search = driver.find_element_by_xpath('//*[@id="anno_prod"]')
            search.clear()
            search.send_keys(str(year))
            search = driver.find_element_by_xpath('/html/body/div[9]/form/div[2]/div/input')
            search.clear()
            search.send_keys(title)
            search.send_keys(Keys.RETURN)
            sleep(2)
            container = driver.find_element_by_xpath('//*[@id="home_centrale"]/div[2]/table[1]/tbody/tr/td[1]/div[3]')
            rate_myMovies = container.find_elements_by_tag_name("span")[2].text
            if rate_myMovies is not None:
                rate_myMovies = rate_myMovies.replace('(', '')
                rate_myMovies = rate_myMovies.replace(')', '')
                ratings['mymovies_rating'] = float(rate_myMovies.replace(',', '.')) * 2
                print("MyMovies: " + str(rate_myMovies) + "/5,")
        except:
            print("MyMovies: ")

        film['ratings'] = ratings

        print('Directors:')
        directors = []
        for director in movie_obj['directors']:
            directors.append(director['name'])
            print(director['name'])
        film['directors'] = directors

        cast = []
        for j in range(len(movie_obj['cast'])):
            person = {}
            actor = movie_obj['cast'][j]
            person['name'] = actor['name']
            print('Actor: ' + actor['name'] + ' | Role: ', end=" ")
            try:
                role = actor.currentRole
                person['role'] = str(role)
                print(role)
            except:
                print('Role: ')
            cast.append(person)

        film['cast'] = cast

        try:
            link = driver.find_element_by_xpath('//*[@id="home_centrale"]/div[2]/table[1]/tbody/tr/td[1]/div[3]/a')
            reference_1 = link.get_attribute("href")
            driver.get(reference_1)
            reference_2 = driver.current_url
            driver.get(reference_2 + 'forum/')
            comments = driver.find_element_by_xpath(
                '//*[@id="recensione_nocap"]/table/tbody/tr[2]/td').find_elements_by_tag_name("table")
            for comment in comments:
                comment_ = {}
                comment_id = ObjectId()
                autore = comment.find_elements_by_tag_name("a")[0].text
                data = comment.find_elements_by_tag_name("span")[0].text
                data_ = mymovies_date_formatter(data)
                testo = comment.find_element_by_class_name("linkrosa").text
                comment_['_id'] = comment_id
                comment_['film_id'] = film_id
                comment_['author'] = autore
                comment_['text'] = testo.replace(" [+]", "")
                comment_['date'] = data_
                comment_['type'] = 'mymovies_comment'
                comment_['comment_points'] = 0
                film_comments.append(comment_id)
                try:
                    user_rating = comment.find_element_by_tag_name("img").get_attribute("alt")
                    user_rating = int(re.sub("\D", "", user_rating)) * 2
                    comment_['user_rating'] = user_rating
                    print("User Rating: " + user_rating)
                except:
                    pass
                comments_.append(comment_)
                print("commentiMyMovies:" + autore + ":" + data + ":" + testo)
        except:
            pass

        try:
            driver.get(reference_2 + "premi/")
            awards = []
            print("Premi :")
            fest = driver.find_element_by_xpath('//*[@id="recensione"]/table/tbody/tr[2]/td').find_elements_by_tag_name(
                "table")
            k = 0
            for f in fest:
                festival = f.find_elements_by_tag_name("a")[1].text
                if k % 2 == 0:
                    awards.append(festival)
                    print(festival)
                k = k + 1
            film['awards'] = awards
        except:
            pass
        try:
            available_on = []
            driver.get(reference_2 + "shop/")
            i = 0
            rows = driver.find_element_by_xpath('/html/body/div[11]/div/div[1]/table').find_elements_by_tag_name("tr")
            for row in rows:
                if i >= 2 and i % 2 == 0:
                    streaming = {}
                    platform = row.find_elements_by_tag_name("td")[0].text.strip()
                    p = row.find_elements_by_tag_name("td")

                    if len(p) >= 5:
                        prize = p[4].text.strip()
                        if platform != 'Google Play' and prize != '-':
                            streaming['platform'] = platform
                            streaming['price'] = float(prize[1:].replace(",", "."))

                    else:
                        prize = "disponibile su " + platform
                    if streaming:
                        available_on.append(streaming)
                    print(platform + " : " + prize)
                i = i + 1
                film['available_on'] = available_on
        except:
            pass
        sleep(2)
        driver.get("https://www.imdb.com/title/tt" + movie_id)
        """print('Durata: ',end = " ")
                                try:
                                    durata = driver.find_element_by_xpath('//*[@id="title-overview-widget"]/div[1]/div[2]/div/div[2]/div[2]/div/time').text
                                    film['running_time'] = durata
                                    print(durata)
                                except:
                                    pass"""
        try:
            boxes = driver.find_element_by_id("titleDetails").find_elements_by_class_name("txt-block")
            for box in boxes:
                try:
                    ind = box.find_element_by_tag_name("h4").text
                    if ind == "Cumulative Worldwide Gross:":
                        box_office = re.sub(r'Cumulative Worldwide Gross: ', '', box.text)
                        film['box_office'] = int(re.sub("\D", "", box_office))
                        print('Box Office:' + box_office)
                        break
                except:
                    continue
        except:
            print("Box-office:  ")
        try:
            imdb_comments = []
            driver.find_element_by_xpath('//*[@id="quicklinksMainSection"]/a[3]').click()
            comments = driver.find_elements_by_class_name("lister-item-content")
            for comment in comments:
                comment_ = {}
                comment_id = ObjectId()
                autore = comment.find_element_by_class_name("display-name-date").find_elements_by_tag_name("span")[
                    0].text
                data = comment.find_element_by_class_name("display-name-date").find_elements_by_tag_name("span")[1].text
                data_ = imdb_date_formatter(data)
                testo = bytes(comment.find_element_by_class_name("content").find_elements_by_tag_name("div")[0].text,
                              'utf-8').decode('utf-8', 'ignore')
                user_rating = comment.text.partition('\n')[0].split('/', 1)[0]
                print(user_rating)
                comment_['_id'] = comment_id
                comment_['film_id'] = film_id
                comment_['author'] = autore
                comment_['text'] = testo
                comment_['date'] = data_
                comment_['comment_points'] = 0
                comment_['type'] = 'imdb_comment'
                try:
                    user_rating = int(user_rating)
                    comment_['user_rating'] = user_rating
                except ValueError:
                    pass
                comments_.append(comment_)
                film_comments.append(comment_id)
                print("Autore: " + autore)
                print("Testo: " + testo)
                print("Data: " + data)
        except:
            pass
        film['comments'] = film_comments
        films.append(film)
    except:
        pass


def add_movie(driver_, film_):
    with driver_.session() as session:
        with session.begin_transaction() as tx:
            _id = str(film_['_id'])
            original_title = film_['original_title']
            italian_title = film_['italian_title']
            year_ = film_['year']
            tx.run("CREATE (f:Film) "
                   "SET f = { _id: $_id, original_title: $original_title, italian_title: $italian_title, year: $year } "
                   , _id=_id, original_title=original_title, italian_title=italian_title, year=year_)
            try:
                genres_ = film_['genres']
                for genre_ in genres_:
                    tx.run("MATCH (f:Film {_id: $_id}) "
                           "MERGE (g:Genre {name: $genre})"
                           "CREATE (f)-[lab: LABELED]->(g) "
                           , _id=_id, genre=genre_)
            except:
                pass
            try:
                cast_ = film_['cast']
                for item in cast_:
                    actor_ = item['name']
                    tx.run("MATCH (f:Film {_id: $_id}) "
                           "MERGE (a:Actor {name: $actor})"
                           "CREATE (a)-[act: ACTED_IN]->(f) "
                           , _id=_id, actor=actor_)
            except:
                pass
            try:
                directors_ = film_['directors']
                for director_ in directors_:
                    tx.run("MATCH (f:Film {_id: $_id}) "
                           "MERGE (d:Director {name: $director})"
                           "CREATE (d)-[dir: DIRECTED]->(f) "
                           , _id=_id, director=director_)
            except:
                pass

            tx.commit()


client = pymongo.MongoClient(
    'mongodb://172.16.0.121:27017,172.16.0.122:27017,172.16.0.124:27017/task_2?replicaSet=task_2')


def callback(session_):
    check_ = True

    db.comments.insert_many(comments_, session=session_)
    db.films.insert_many(films, session=session_)

    try:
        driver_ = GraphDatabase.driver('bolt://172.16.0.125:7687', auth=('neo4j', 'root'), encrypted=False)
        for film_ in films:
            add_movie(driver_, film_)
        driver_.close()
    except:
        check_ = False

    if not check_:
        for comm in comments_:
            db.comments.delete_one({'_id': comm['_id']}, session=session_)

        for movie_ in films:
            db.films.delete_one({'_id': movie_['_id']}, session=session_)

    return check_


with client.start_session() as session:
    db = client.task_2
    result = session.with_transaction(callback)
    if result:
        print("Success!")
    else:
        print("Error. No changes were made.")

client.close()
