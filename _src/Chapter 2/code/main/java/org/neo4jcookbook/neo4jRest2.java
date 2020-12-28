package org.neo4jcookbook;


import org.neo4j.graphdb.*;
import org.neo4j.rest.graphdb.*;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.index.impl.lucene.LuceneIndexImplementation;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.entity.RestRelationship;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.neo4j.rest.graphdb.index.RestIndexManager;
import org.neo4j.rest.graphdb.util.TestHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class neo4jRest2 {
    public neo4jRest2() {
    }

    public static void main(String[] args) {
        RestAPI restAPI = new RestAPIFacade("http://localhost:7474/db/data");
        Map<String,Object> pr=new HashMap<String, Object>();
        pr.put("id",1);
        pr.put("name","A");
        restAPI.createNode(pr);





    }

}
