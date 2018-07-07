/**
 * 
 */
package org.tdar.fileprocessing.tasks;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.tdar.TestConstants;
import org.tdar.configuration.TdarConfiguration;
import org.tdar.fileprocessing.workflows.ImageWorkflow;
import org.tdar.fileprocessing.workflows.WorkflowContext;
import org.tdar.filestore.FileStoreFile;
import org.tdar.filestore.Filestore;
import org.tdar.filestore.FilestoreObjectType;
import org.tdar.filestore.VersionType;

/**
 * @author Adam Brin
 * 
 */
public class ImageFileTest {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    @Rollback
    public void testMissingImageStatus() throws Exception {
        String filename = "grandcanyon_cmyk.jpg";
        WorkflowContext wc = testFileProcessing(filename, false);
        assertFalse(CollectionUtils.isEmpty(wc.getExceptions()));
    }

    @Test
    @Rollback
    public void testMissingImageStatusWithJAI() throws Exception {
        String filename = "grandcanyon_cmyk.jpg";
        WorkflowContext wc = testFileProcessing(filename, true);
        assertTrue(CollectionUtils.isEmpty(wc.getExceptions()));
    }

    @Test
    @Rollback
    public void testGEoTIFF() throws Exception {
        WorkflowContext wc = testFileProcessing("PUEBLO_ALTO_50.tif", true);
        assertTrue(CollectionUtils.isEmpty(wc.getExceptions()));
    }

    @Test
    @Rollback
    public void testGPSImageStatus() throws Exception {
        String filename = "gps_photo.jpg";
        WorkflowContext wc = testFileProcessing(filename, true);
        assertTrue(CollectionUtils.isEmpty(wc.getExceptions()));
    }

    @Test
    @Rollback
    public void testImageFormatMissingStatus() throws Exception {
        String filename = "grandcanyon_cmyk.jpg";
        WorkflowContext wc = testFileProcessing(filename, false);
        assertFalse(CollectionUtils.isEmpty(wc.getExceptions()));
    }

    @Test
    @Rollback
    public void testImageCorrupt() throws Exception {
        String filename = "grandcanyon_lzw_corrupt.tif";
        WorkflowContext wc = testFileProcessing(filename, false);
        assertFalse(CollectionUtils.isEmpty(wc.getExceptions()));
    }

    private WorkflowContext testFileProcessing(File f, boolean successful) throws InstantiationException, IllegalAccessException, IOException,
            Exception {
        Filestore store = TdarConfiguration.getInstance().getFilestore();
        ImageWorkflow iw = new ImageWorkflow();
        WorkflowContext wc = new WorkflowContext();
        FileStoreFile version = new FileStoreFile(FilestoreObjectType.RESOURCE, VersionType.UPLOADED, 100L, f.getName() );
        version.setInformationResourceFileId(100L);
        version.setPersistableId(100L);
        version.setVersion(1);
        String store2 = store.store(FilestoreObjectType.RESOURCE, f, version);
        logger.debug(store2);
        wc.getOriginalFiles().add(version);
        iw.run(wc);
        logger.error("{}", wc.getExceptions());
        logger.error(wc.getExceptionAsString());
//        SensoryData doc = generateAndStoreVersion(SensoryData.class, f.getName(), f, store);
//        InformationResourceFileVersion originalVersion = doc.getLatestUploadedVersion();
//        FileType fileType = fileAnalyzer.getFileTypeForExtension(originalVersion, doc.getResourceType());
//        assertEquals(FileType.IMAGE, fileType);
//        Workflow workflow = fileAnalyzer.getWorkflow(ResourceType.IMAGE, originalVersion);
//        assertEquals(ImageWorkflow.class, workflow.getClass());
//        boolean result = messageService.sendFileProcessingRequest(workflow, originalVersion);
//        FileProxy proxy = new FileProxy(f.getName(), f, VersionType.UPLOADED);
//        proxy.setInformationResourceFileVersion(originalVersion);
//        proxy.setInformationResourceFile(originalVersion.getInformationResourceFile());
//        WorkflowResult workflowResult = new WorkflowResult(Arrays.asList(proxy));
//        ErrorTransferObject errorsAndMessages = workflowResult.getActionErrorsAndMessages();
//        logger.debug("ACTION ERRORS  : {}", errorsAndMessages.getActionErrors());
//        logger.debug("ACTION Messages: {}", errorsAndMessages.getActionMessages());
//        InformationResourceFile informationResourceFile = originalVersion.getInformationResourceFile();
//        informationResourceFile = genericService.find(InformationResourceFile.class, informationResourceFile.getId());
//        assertEquals(successful, result);
        return wc;
    }

    private WorkflowContext testFileProcessing(String filename, boolean successful) throws InstantiationException, IllegalAccessException, IOException,
            Exception {
        File f = TestConstants.getFile(TestConstants.TEST_IMAGE_DIR + "/sample_image_formats/", filename);
        return testFileProcessing(f, successful);
    }
}
