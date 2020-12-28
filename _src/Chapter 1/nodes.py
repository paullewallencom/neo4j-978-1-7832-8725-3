#Sample python code to create nodes from csv file
import csv
from py2neo import neo4j, cypher
from py2neo import node,  rel
graph_db = neo4j.Graph("http://localhost:7474/db/data/")
ifile  = open('nodes.csv', "rb")
reader = csv.reader(ifile)
rownum = 0
for row in reader:
	nodes = graph_db.create({"name":row[2]})
ifile.close()
