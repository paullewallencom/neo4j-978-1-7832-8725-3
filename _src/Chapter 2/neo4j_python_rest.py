#Sample code to demonstrate Neo4j REST API calls using python
from py2neo import neo4j,node
ENDPOINT_URL="http://localhost:7474/db/data"
graph = neo4j.Graph(ENDPOINT_URL)
graph.create(node(name="A"))