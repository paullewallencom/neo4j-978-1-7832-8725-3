import urllib2
import json

url = "http://127.0.0.1:7474/db/data/node"
payload = {'lon': 38.6, 'lat': 67.88, 'name': 'abc'}

req = urllib2.Request(url)
req.add_header('Content-Type', 'application/json')

response = urllib2.urlopen(req, json.dumps(payload))
node =  json.loads(response.read())['self']
url = "http://127.0.0.1:7474/db/data/index/node/geom"
payload = {'value': 'dummy', 'key': 'dummy', 'uri': node}
req = urllib2.Request(url)
req.add_header('Content-Type', 'application/json')

response = urllib2.urlopen(req, json.dumps(payload))
print response.read()
