import json

users = []

with open("users.json", "r", encoding="utf-8") as fin:
    for line in fin:
        user = json.loads(line)
        _id = user['_id']['$oid']
        username = user['username']
        user_ = {'_id': _id, 'username': username}
        users.append(user_)

with open('users.csv', 'a', encoding='utf8', errors='ignore') as fout:
    for user in users:
        fout.write("{}*{}\n"
                   .format((user['_id']),(user['username'])))
