#Bash Script for importing nodes
NEO4J_ROOT="/var/lib/neo4j"
while read LINE
do
   name=`echo $LINE | awk -F "," '{print $3}'`
   ${NEO4J_ROOT}/bin/neo4j-shell -c mknode  --np  \"{'name':$name}\" -v
done
