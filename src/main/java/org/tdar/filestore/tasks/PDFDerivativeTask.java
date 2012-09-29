/**
 * 
 */
package org.tdar.filestore.tasks;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFImageWriter;
import org.tdar.core.bean.resource.InformationResourceFileVersion;
import org.tdar.core.bean.resource.VersionType;
import org.tdar.core.exception.TdarRecoverableRuntimeException;
import org.tdar.filestore.WorkflowContext;

/**
 * @author Adam Brin
 */

public class PDFDerivativeTask extends ImageThumbnailTask {

    private static final long serialVersionUID = -1138753863662695849L;

    public static void main(String[] args) {
        PDFDerivativeTask task = new PDFDerivativeTask();
        String baseDir = "C:\\Users\\Adam Brin\\Downloads\\";
        String orig = "dpctw08-01.pdf";
        File origFile = new File(baseDir, orig);
        WorkflowContext ctx = new WorkflowContext();
        ctx.setWorkingDirectory(new File(
                // FileUtils.getTempDirectoryPath()
                System.getProperty("java.io.tmpdir")
                ));
        task.setWorkflowContext(ctx);

        InformationResourceFileVersion vers = task.generateInformationResourceFileVersion(origFile, VersionType.UPLOADED);
        ctx.setOriginalFile(vers);
        try {
            task.run(origFile);
        } catch (Throwable e) {
            e.printStackTrace();
            throw new TdarRecoverableRuntimeException("processing error");
        }
    }

    @Override
    public void run() throws Exception {
        // unify the various "run" commands between main and inline
        run(getWorkflowContext().getOriginalFile().getFile());
    }

    @Override
    public void run(File originalFile) throws Exception {
        try {
            PDDocument document = openPDF("", originalFile);
            File imageFile = new File(extractPage(1, originalFile, document));
            // extractText(originalFile, document);
            closePDF(document);
            processImage(imageFile);
        } catch (Throwable t) {
            throw new TdarRecoverableRuntimeException("processing error", t);
        }
    }


    protected String extractPage(int pageNum, File pdfFile, PDDocument document) {
        // File pdfFile = new File(sourceFile);
        String imageFormat = "jpg";
        String color = "rgb";
        int resolution;
        try {
            resolution = Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (HeadlessException e) {
            resolution = 96;
        }

        String fn = getWorkflowContext().getOriginalFile().getFilename();
        String outputPrefix = fn.substring(0, fn.lastIndexOf('.'));
        outputPrefix = new File(getWorkflowContext().getWorkingDirectory(), outputPrefix).toString();

        if (document != null) {
            int imageType = determineImageType(color);

            try {
                PDFImageWriter imageWriter = new PDFImageWriter();
                boolean success = imageWriter.writeImage(document, imageFormat, "", pageNum, pageNum, outputPrefix, imageType, resolution);
                if (!success) {
                    getLogger().info("Error: no writer found for image format '" + imageFormat + "'");
                }
            } catch (Throwable e) {
                getLogger().debug("PDF image extraction failed", e);
            }
        }

        return outputPrefix + pageNum + "." + imageFormat;
    }

    private void closePDF(PDDocument document) {
        if (document != null) {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private PDDocument openPDF(String password, File pdfFile) {
        PDDocument document = null;
        try {
            document = PDDocument.load(pdfFile);
            if (document.isEncrypted()) {
                try {
                    document.decrypt(password);
                } catch (InvalidPasswordException e) {
                    getLogger().debug("Error: The document is encrypted.");
                }
            }
            getWorkflowContext().setNumPages(document.getNumberOfPages());
        } catch (IOException e) {
            getLogger().info(e);
            e.printStackTrace();
        } catch (CryptographyException ce) {
            getLogger().info(ce);
            ce.printStackTrace();
        }
        return document;
    }

    private int determineImageType(String color) {
        int imageType = 24;
        if ("bilevel".equalsIgnoreCase(color)) {
            imageType = BufferedImage.TYPE_BYTE_BINARY;
        } else if ("indexed".equalsIgnoreCase(color)) {
            imageType = BufferedImage.TYPE_BYTE_INDEXED;
        } else if ("gray".equalsIgnoreCase(color)) {
            imageType = BufferedImage.TYPE_BYTE_GRAY;
        } else if ("rgb".equalsIgnoreCase(color)) {
            imageType = BufferedImage.TYPE_INT_RGB;
        } else if ("rgba".equalsIgnoreCase(color)) {
            imageType = BufferedImage.TYPE_INT_ARGB;
        } else {
            getLogger().debug("Error: the number of bits per pixel must be 1, 8 or 24.");
        }
        return imageType;
    }

    @Override
    public String getName() {
        return "PDFDerivativeTask";
    }

}
