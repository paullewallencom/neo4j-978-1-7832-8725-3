//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.gis.spatial;

import org.neo4j.gis.spatial.ShapefileImporter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class ShpImporter {
	public ShpImporter() {
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
		GraphDatabaseService esri_database = (new GraphDatabaseFactory()).newEmbeddedDatabase("~/graph.db");
		System.out.println("Server is up and Running");
		ShapefileImporter importer = new ShapefileImporter(esri_database);

		try {
			importer.importFile("/data/AFG_adm1.shp", "layer_afganistan");
		} catch (Exception var4) {
			esri_database.shutdown();
		}

		registerShutdownHook(esri_database);
	}
}
