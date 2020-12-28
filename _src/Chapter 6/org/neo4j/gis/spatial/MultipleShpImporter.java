//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.gis.spatial;

import java.io.File;
import java.io.FilenameFilter;
import org.neo4j.gis.spatial.ShapefileImporter;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class MultipleShpImporter {
	public MultipleShpImporter() {
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDb) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Server is shutting down");
				graphDb.shutdown();
			}
		});
	}

	public static void main(String[] args) throws Exception {
		GraphDatabaseService esri_database = (new GraphDatabaseFactory()).newEmbeddedDatabase("~/graph.db");
		System.out.println("Server is up and Running");
		ShapefileImporter importer = new ShapefileImporter(esri_database);
		File dir = new File("/data");
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".shp");
			}
		};
		File[] listOfFiles = dir.listFiles(filter);
		File[] var6 = listOfFiles;
		int var7 = listOfFiles.length;

		for(int var8 = 0; var8 < var7; ++var8) {
			File fileEntry = var6[var8];
			System.out.println("FileEntry Directory " + fileEntry);

			try {
				importer.importFile(fileEntry.toString(), "layer_afganistan");
			} catch (Exception var11) {
				esri_database.shutdown();
			}
		}

	}
}
