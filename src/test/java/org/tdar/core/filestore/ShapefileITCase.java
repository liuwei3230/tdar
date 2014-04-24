/**
 * 
 */
package org.tdar.core.filestore;

import java.io.File;

import org.apache.log4j.Logger;
import org.hibernate.search.query.ObjectLookupMethod;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.tdar.TestConstants;
import org.tdar.core.bean.AbstractIntegrationTestCase;
import org.tdar.core.bean.FileContainer;
import org.tdar.core.bean.resource.Geospatial;
import org.tdar.core.bean.resource.InformationResourceFile;
import org.tdar.core.bean.resource.InformationResourceFileVersion;
import org.tdar.core.service.workflow.workflows.Workflow;
import org.tdar.filestore.FileAnalyzer;
import org.tdar.filestore.Filestore.ObjectType;
import org.tdar.filestore.PairtreeFilestore;
import org.tdar.filestore.WorkflowContext;
import org.tdar.filestore.tasks.ShapefileReaderTask;

/**
 * @author Adam Brin
 * 
 */
public class ShapefileITCase extends AbstractIntegrationTestCase {

    @SuppressWarnings("unused")
    private Logger logger = Logger.getLogger(getClass());

    @Autowired
    FileAnalyzer fileAnalyzer;

    @Test
    @Rollback
    public void testGeoTiffArc10WithWorldFile() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        InformationResourceFile originalFile = generateAndSend(Geospatial.class, store, null, "untitled.tif", new File(TestConstants.TEST_GEOTIFF), true).getInformationResourceFile();
        InformationResourceFile supportingFile = generateAndSend(Geospatial.class, store, null, "untitled.tfw", new File(TestConstants.TEST_GEOTIFF_TFW), true).getInformationResourceFile();

        Workflow workflow = fileAnalyzer.getWorkflow(originalFile.getLatestUploadedVersion(), supportingFile.getLatestUploadedVersion());
        wc.getOriginalFiles().add(originalFile.getLatestUploadedVersion());
        wc.getOriginalFiles().add(supportingFile.getLatestUploadedVersion());

        workflow.run(wc);
        task.setWorkflowContext(wc);
        task.run();
    }

    @Test
    @Rollback
    public void testGeoTiffCombined() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        InformationResourceFile originalFile = generateAndSend(Geospatial.class, store, null, "untitled.tif", new File(TestConstants.TEST_GEOTIFF), true).getInformationResourceFile();
        wc.getOriginalFiles().add(originalFile.getLatestUploadedVersion());
        task.setWorkflowContext(wc);
        task.run();
    }

    @Test
    @Rollback
    public void testKml() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        InformationResourceFile originalFile = generateAndSend(Geospatial.class, store, null, "doc.kml", new File(TestConstants.TEST_KML), true).getInformationResourceFile();
        wc.getOriginalFiles().add(originalFile.getLatestUploadedVersion());
        task.setWorkflowContext(wc);
        task.run();
    }

    @Test
    @Rollback
    public void testPolyShapeWithData() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        String name = "Occ_3l";
        String string = TestConstants.TEST_SHAPEFILE_DIR + name;
        InformationResourceFile originalFile = generateAndSend(Geospatial.class, store, null, "untitled.tif", new File(TestConstants.TEST_GEOTIFF), true).getInformationResourceFile();
        wc.getOriginalFiles().add(originalFile.getLatestUploadedVersion());

        for (String ext : new String[] { ".dbf", ".sbn", ".sbx", ".shp.xml", ".shx", ".xml" }) {
            InformationResourceFile file = generateAndSend(Geospatial.class, store, null, name + ext, new File(string + ext), true).getInformationResourceFile();
            wc.getOriginalFiles().add(file.getLatestUploadedVersion());


        }
        task.setWorkflowContext(wc);
        task.run();
    }

    @Test
    @Rollback
    @Ignore("not implemented")
    public void testGeoTiffAUX() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        String name = "CAmpusMap1950new";
        String string = TestConstants.TEST_GEOTIFF_DIR + name;
        InformationResourceFile originalFile = generateAndSend(Geospatial.class, store, null,  name + ".tif", new File(string + ".tif"), true).getInformationResourceFile();
        wc.getOriginalFiles().add(originalFile.getLatestUploadedVersion());
        for (String ext : new String[] { ".tif.aux.xml" }) {
            InformationResourceFile file = generateAndSend(Geospatial.class, store, null,  name + ext, new File(string + ext), true).getInformationResourceFile();
            wc.getOriginalFiles().add(file.getLatestUploadedVersion());
        }
        task.setWorkflowContext(wc);
        task.run();
    }

    @Test
    @Rollback
    public void testFAIMSKML() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        String name = "Tracklog";
        String string = TestConstants.TEST_ROOT_DIR + TestConstants.TEST_GIS_DIR + "/kml/" + name;
        InformationResourceFile originalFile = generateAndSend(Geospatial.class, store, null,  name + ".kml", new File(string + ".kml"), true).getInformationResourceFile();
        wc.getOriginalFiles().add(originalFile.getLatestUploadedVersion());
        task.setWorkflowContext(wc);
        task.run();
    }

    @Test
    @Rollback
    public void testExtendedDataKml() throws Exception {
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        ShapefileReaderTask task = new ShapefileReaderTask();
        WorkflowContext wc = new WorkflowContext();
        String name = "extendedData";
        String string = TestConstants.TEST_ROOT_DIR + TestConstants.TEST_GIS_DIR + "/kml/" + name;
        FileContainer container = generateAndSend(Geospatial.class, store, null,  name + ".kml", new File(string + ".kml"), true);
        InformationResourceFile originalFile = container.getInformationResourceFile();
        InformationResourceFileVersion version = originalFile.getLatestUploadedVersion();
        version.setInformationResourceId(container.getInformationResource().getId());
        store.retrieveFile(ObjectType.RESOURCE, version);
        
        wc.getOriginalFiles().add(version);
        task.setWorkflowContext(wc);
        task.run();
    }

}
