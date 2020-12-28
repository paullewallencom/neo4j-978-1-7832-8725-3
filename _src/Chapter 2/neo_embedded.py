#Sample node to demonstrate neo4j embedded using python 
import neo4j 	
DB_PATH="/var/lib/neo4j/data/graph.db.test"
db_obj =  neo4j.GraphDatabase(DB_PATH)
# All write operations on graph database happens in transaction
with db_obj.transaction:
	node = db_obj.node(name="A")
db_obj.shutdown()
