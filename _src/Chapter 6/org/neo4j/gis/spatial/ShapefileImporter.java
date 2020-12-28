//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.neo4j.gis.spatial;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import org.geotools.data.shapefile.prj.PrjFileReader;
import org.geotools.data.shapefile.shp.JTSUtilities;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;
import org.neo4j.gis.spatial.Constants;
import org.neo4j.gis.spatial.EditableLayerImpl;
import org.neo4j.gis.spatial.OrderedEditableLayer;
import org.neo4j.gis.spatial.SpatialDatabaseService;
import org.neo4j.gis.spatial.WKBGeometryEncoder;
import org.neo4j.gis.spatial.rtree.Listener;
import org.neo4j.gis.spatial.rtree.NullListener;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class ShapefileImporter implements Constants {
    private int commitInterval;
    private boolean maintainGeometryOrder;
    private Listener monitor;
    private GraphDatabaseService database;
    private SpatialDatabaseService spatialDatabase;
    private Envelope filterEnvelope;

    public ShapefileImporter(GraphDatabaseService database, Listener monitor, int commitInterval, boolean maintainGeometryOrder) {
        this.maintainGeometryOrder = false;
        this.maintainGeometryOrder = maintainGeometryOrder;
        if(commitInterval < 1) {
            throw new IllegalArgumentException("commitInterval must be > 0");
        } else {
            this.commitInterval = commitInterval;
            this.database = database;
            this.spatialDatabase = new SpatialDatabaseService(database);
            if(monitor == null) {
                monitor = new NullListener();
            }

            this.monitor = (Listener)monitor;
        }
    }

    public ShapefileImporter(GraphDatabaseService database, Listener monitor, int commitInterval) {
        this(database, monitor, commitInterval, false);
    }

    public ShapefileImporter(GraphDatabaseService database, Listener monitor) {
        this(database, monitor, 1000, false);
    }

    public ShapefileImporter(GraphDatabaseService database) {
        this(database, (Listener)null, 1000, false);
    }

    public static void main(String[] args) throws Exception {
        int commitInterval = 1000;
        if(args.length >= 2 && args.length <= 4) {
            String neoPath = args[0];
            String shpPath = args[1];
            shpPath = shpPath.substring(0, shpPath.lastIndexOf("."));
            String layerName;
            if(args.length == 2) {
                layerName = shpPath.substring(shpPath.lastIndexOf(File.separator) + 1);
            } else if(args.length == 3) {
                layerName = args[2];
            } else {
                layerName = args[2];
                commitInterval = Integer.parseInt(args[3]);
            }

            GraphDatabaseService database = (new GraphDatabaseFactory()).newEmbeddedDatabase(neoPath);

            try {
                ShapefileImporter importer = new ShapefileImporter(database, new NullListener(), commitInterval);
                importer.importFile(shpPath, layerName);
            } finally {
                database.shutdown();
            }

        } else {
            throw new IllegalArgumentException("Parameters: neo4jDirectory shapefile [layerName commitInterval]");
        }
    }

    public void importFile(String dataset, String layerName) throws ShapefileException, FileNotFoundException, IOException {
        this.importFile(dataset, layerName, Charset.defaultCharset());
    }

    public void importFile(String dataset, String layerName, Charset charset) throws ShapefileException, FileNotFoundException, IOException {
        Class layerClass = this.maintainGeometryOrder?OrderedEditableLayer.class:EditableLayerImpl.class;
        EditableLayerImpl layer = (EditableLayerImpl)this.spatialDatabase.getOrCreateLayer(layerName, WKBGeometryEncoder.class, layerClass);
        GeometryFactory geomFactory = layer.getGeometryFactory();
        boolean strict = false;
        boolean shpMemoryMapped = true;
        long startTime = System.currentTimeMillis();
        ShpFiles shpFiles = null;

        try {
            shpFiles = new ShpFiles(new File(dataset));
        } catch (Exception var74) {
            try {
                shpFiles = new ShpFiles(new File(dataset + ".shp"));
            } catch (Exception var73) {
                throw new IllegalArgumentException("Failed to access the shapefile at either \'" + dataset + "\' or \'" + dataset + ".shp\'", var74);
            }
        }

        CoordinateReferenceSystem crs = this.readCRS(shpFiles);
        ShapefileReader shpReader = new ShapefileReader(shpFiles, strict, shpMemoryMapped, geomFactory);

        try {
            Class stopTime = JTSUtilities.findBestGeometryClass(shpReader.getHeader().getShapeType());
            Integer geometryType = Integer.valueOf(SpatialDatabaseService.convertJtsClassToGeometryType(stopTime));
            DbaseFileReader dbfReader = new DbaseFileReader(shpFiles, shpMemoryMapped, charset);

            try {
                DbaseFileHeader dbaseFileHeader = dbfReader.getHeader();
                String[] fieldsName = new String[dbaseFileHeader.getNumFields() + 1];
                fieldsName[0] = "ID";

                for(int tx = 1; tx < fieldsName.length; ++tx) {
                    fieldsName[tx] = dbaseFileHeader.getFieldName(tx - 1);
                }

                Transaction var82 = this.database.beginTx();

                try {
                    if(crs != null) {
                        layer.setCoordinateReferenceSystem(crs);
                    }

                    if(geometryType != null) {
                        layer.setGeometryType(geometryType);
                    }

                    layer.mergeExtraPropertyNames(fieldsName);
                    var82.success();
                } finally {
                    var82.close();
                }

                this.monitor.begin(dbaseFileHeader.getNumRecords());

                try {
                    ArrayList fields = new ArrayList();
                    int recordCounter = 0;
                    int filterCounter = 0;

                    while(shpReader.hasNext() && dbfReader.hasNext()) {
                        var82 = this.database.beginTx();

                        try {
                            int committedSinceLastNotification = 0;

                            for(int i = 0; i < this.commitInterval; ++i) {
                                if(shpReader.hasNext() && dbfReader.hasNext()) {
                                    Record record = shpReader.nextRecord();
                                    ++recordCounter;
                                    ++committedSinceLastNotification;

                                    try {
                                        fields.clear();
                                        Geometry geometry = (Geometry)record.shape();
                                        if(this.filterEnvelope != null && !this.filterEnvelope.intersects(geometry.getEnvelopeInternal())) {
                                            ++filterCounter;
                                        } else {
                                            Object[] values = dbfReader.readEntry();
                                            fields.add(Integer.valueOf(recordCounter));
                                            Collections.addAll(fields, values);
                                            if(geometry.isEmpty()) {
                                                this.log("warn | found empty geometry in record " + recordCounter);
                                            } else {
                                                layer.add(geometry, fieldsName, fields.toArray(values));
                                            }
                                        }
                                    } catch (IllegalArgumentException var75) {
                                        this.log("warn | found invalid geometry: index=" + recordCounter, var75);
                                    }
                                }
                            }

                            this.monitor.worked(committedSinceLastNotification);
                            var82.success();
                            this.log("info | inserted geometries: " + (recordCounter - filterCounter));
                            if(filterCounter > 0) {
                                this.log("info | ignored " + filterCounter + "/" + recordCounter + " geometries outside filter envelope: " + this.filterEnvelope);
                            }
                        } finally {
                            var82.close();
                        }
                    }
                } finally {
                    this.monitor.done();
                }
            } finally {
                dbfReader.close();
            }
        } finally {
            shpReader.close();
        }

        long var81 = System.currentTimeMillis();
        this.log("info | elapsed time in seconds: " + 1.0D * (double)(var81 - startTime) / 1000.0D);
    }

    private CoordinateReferenceSystem readCRS(ShpFiles shpFiles) {
        try {
            PrjFileReader e = new PrjFileReader(shpFiles);

            CoordinateReferenceSystem var3;
            try {
                var3 = e.getCoodinateSystem();
            } finally {
                e.close();
            }

            return var3;
        } catch (IOException var8) {
            var8.printStackTrace();
            return null;
        }
    }

    private void log(String message) {
        System.out.println(message);
    }

    private void log(String message, Exception e) {
        System.out.println(message);
        e.printStackTrace();
    }

    public void setFilterEnvelope(Envelope filterEnvelope) {
        this.filterEnvelope = filterEnvelope;
    }
}
