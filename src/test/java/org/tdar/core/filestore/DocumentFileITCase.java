/**
 * 
 */
package org.tdar.core.filestore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.tdar.TestConstants;
import org.tdar.core.bean.AbstractIntegrationTestCase;
import org.tdar.core.bean.resource.Document;
import org.tdar.core.bean.resource.InformationResourceFile;
import org.tdar.core.bean.resource.InformationResourceFile.FileStatus;
import org.tdar.core.bean.resource.InformationResourceFile.FileType;
import org.tdar.core.bean.resource.InformationResourceFileVersion;
import org.tdar.core.configuration.TdarConfiguration;
import org.tdar.core.service.workflow.MessageService;
import org.tdar.core.service.workflow.workflows.GenericDocumentWorkflow;
import org.tdar.core.service.workflow.workflows.PDFWorkflow;
import org.tdar.core.service.workflow.workflows.Workflow;
import org.tdar.filestore.FileAnalyzer;
import org.tdar.filestore.Filestore.ObjectType;
import org.tdar.filestore.PairtreeFilestore;

/**
 * @author Adam Brin
 * 
 */
public class DocumentFileITCase extends AbstractIntegrationTestCase {

    @Autowired
    private FileAnalyzer fileAnalyzer;

    @Autowired
    private MessageService messageService;

    @Test
    public void testBrokenImageStatus() throws Exception {
        String filename = "volume1-encrypted-test.pdf";
        File f = new File(TestConstants.TEST_DOCUMENT_DIR + "/sample_pdf_formats/", filename);
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);

        InformationResourceFile informationResourceFile = generateAndSend(Document.class, store, FileType.DOCUMENT, filename, f, false);
        informationResourceFile = genericService.find(InformationResourceFile.class, informationResourceFile.getId());
        assertEquals(FileStatus.PROCESSING_ERROR, informationResourceFile.getStatus());
    }

    @Test
    public void testRTFTextExtraction() throws Exception {
        String filename = "test-file.rtf";
        File f = new File(TestConstants.TEST_DOCUMENT_DIR, filename);
        PairtreeFilestore store = new PairtreeFilestore(TestConstants.FILESTORE_PATH);
        InformationResourceFile informationResourceFile = generateAndSend(Document.class, store, FileType.DOCUMENT, filename, f, true);
        informationResourceFile = genericService.find(InformationResourceFile.class, informationResourceFile.getId());
        assertEquals(FileStatus.PROCESSED, informationResourceFile.getStatus());
        InformationResourceFileVersion indexableVersion = informationResourceFile.getIndexableVersion();
        logger.info("version: {}", indexableVersion);
        String text = FileUtils.readFileToString(TdarConfiguration.getInstance().getFilestore().retrieveFile(ObjectType.RESOURCE, indexableVersion));
        logger.info(text);
        assertTrue(text.toLowerCase().contains("have fun digging"));
    }
}
