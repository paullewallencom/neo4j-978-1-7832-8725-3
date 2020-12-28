//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.gis.spatial;

import org.neo4j.gis.spatial.osm.OSMImporter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class OsmImporter {
    public OsmImporter() {
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
        GraphDatabaseService osm_database = (new GraphDatabaseFactory()).newEmbeddedDatabase("~/graph.db");
        System.out.println("Server is up and Running");
        OSMImporter importer = new OSMImporter("africa");

        try {
            importer.importFile(osm_database, "/data/botswana-latest.osm", false, 5000, true);
        } catch (Exception var4) {
            osm_database.shutdown();
        }

        importer.reIndex(osm_database, 10000);
        registerShutdownHook(osm_database);
    }
}
