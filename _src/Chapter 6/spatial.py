import urllib2
import json

url = "http://127.0.0.1:7474/db/data/index/node/"
payload= {
	"name" : "geom",
	"config" : {
		"provider" : "spatial",
		"geometry_type" : "point",
		"lat" : "lat",
		"lon" : "lon"
	}
}

req = urllib2.Request(url)
req.add_header('Content-Type', 'application/json')

response = urllib2.urlopen(req, json.dumps(payload))
print response.read()
