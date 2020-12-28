#Backup script for taking Neo4j backup
NEO4J_ROOT="/var/lib/neo4j"
mkdir -p /mnt/backup
$NEO4J_ROOT/bin/neo4j stop
cp -r $NEO4J_ROOT/data/graph.db "/mnt/backup/neo4j_backup.$(date +%y-%m-%d)"
$NEO4J_ROOT/bin/neo4j start
