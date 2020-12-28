#Bash Script for creating relationships
#Format of csv should be startnode,endnode,type,direction
NEO4J_ROOT="/var/lib/neo4j"
IFS=","
while read LINE
do
   echo $LINE
   array=($LINE)
   ${NEO4J_ROOT}/bin/neo4j-shell -c cd -a ${array[0]} mkrel -d ${array[3]} -t ${array[2]} ${array[1]}
done
