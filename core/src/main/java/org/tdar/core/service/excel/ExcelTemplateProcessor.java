package org.tdar.core.service.excel;

import org.apache.commons.lang3.StringUtils;
import org.jxls.common.Context;
import org.jxls.template.SimpleExporter;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.Map;


/**
 * Baseline processor that uses the jxls default template
 */
public class ExcelTemplateProcessor {

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * Instantiate template processor with a default template.
     *
     * @see  <a href="http://jxls.sourceforge.net/samples/simple_exporter.html">JXLS: SimpleExporter</a>
     *
     */
    public ExcelTemplateProcessor() {
    }


    public void process(List<String> headers, List<?> data, List<String> propertyNames, OutputStream xlsStream ) {
        String joinedNames = StringUtils.join(propertyNames, ", ");
        SimpleExporter exporter = new SimpleExporter();
        exporter.gridExport(headers, data, joinedNames, xlsStream);
    }


    public void process(File template, Map<String, Object> data, OutputStream xlsStream) throws IOException {
        Context context = new Context(data);
        try(InputStream inputStream = new FileInputStream(template)){
            JxlsHelper.getInstance().processTemplate(inputStream, xlsStream, context);
        }
    }


    public void process(File template, Map<String, Object> data, File outfile) throws IOException {
        try(OutputStream os = new FileOutputStream(outfile)) {
            process(template, data, os);
        }
    }

}
