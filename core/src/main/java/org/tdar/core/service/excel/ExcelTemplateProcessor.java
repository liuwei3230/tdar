package org.tdar.core.service.excel;

import org.jxls.template.SimpleExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;


/**
 * Baseline processor that uses the jxls default template
 */
public class ExcelTemplateProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());

    SimpleExporter exporter;

    /**
     * Instantiate template processor with a default  template.
     *
     * @see  <a href="http://jxls.sourceforge.net/samples/simple_exporter.html">JXLS: SimpleExporter</a>
     *
     */
    public ExcelTemplateProcessor() {
        exporter = new SimpleExporter();
    }

    /**
     * Instantiate template processor with specified JXLS template.
     *
     * @param template JXLS to that this processor will apply when rendering an excel
     *                 spreadsheet.
     *
     * @see <a href="http://jxls.sourceforge.net/reference/excel_markup.html">JXLS: Excel mark-up</a>
     */
    public ExcelTemplateProcessor(File template) throws IOException{
        this();
        try (InputStream is = new FileInputStream(template)) {
            exporter.registerGridTemplate(is);
        }
    }

    public void process(List<String> headers, List<?> data, List<String> propertyNames, OutputStream xlsStream ) {

        //String joinedNames = StringUtils.join(propertyNames, ", ");

        //The power of Java 8 turns one line into four!!!
        String joinedNames = propertyNames
                .stream()
                .reduce( (t, u) -> t + ", " + u)
                .get();

        exporter.gridExport(headers, data, joinedNames, xlsStream);
    }

}
