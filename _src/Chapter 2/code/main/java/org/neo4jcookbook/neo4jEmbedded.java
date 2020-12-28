package org.neo4jcookbook;



import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class neo4jEmbedded {
    public neo4jEmbedded() {
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Server is shutting down");
                graphDb.shutdown();
            }
        });
    }

    public static void main(String[] args) {
        final String NEO4J_DB_PATH = "/tmp/graph.db";
        GraphDatabaseService embed = (new GraphDatabaseFactory()).newEmbeddedDatabase(NEO4J_DB_PATH);
        System.out.println("Server is up and Running");
        try ( Transaction tx = embed.beginTx() )
        {
            // Database operations go here
            Node node = embed.createNode();
            node.setProperty("name", "neo4j");
            node.setProperty("Message","Hello World");
            System.out.print( node.getProperty( "name"));
            System.out.print( node.getProperty( "Message" ) );
            tx.success();
        }

        registerShutdownHook(embed);
    }
}

