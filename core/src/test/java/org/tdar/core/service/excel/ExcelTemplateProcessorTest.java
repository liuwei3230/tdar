package org.tdar.core.service.excel;

import org.junit.*;
import org.jxls.util.JxlsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.TestConstants;
import org.tdar.core.bean.entity.Person;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.jxls.common.Context;

import static org.tdar.TestConstants.TEST_JXLS_TEMPLATE_DIR;

/**
 * Tests for basic template processor
 */
public class ExcelTemplateProcessorTest {

    File targetDir;
    File templateDir;
    Logger logger = LoggerFactory.getLogger(getClass());

    public ExcelTemplateProcessorTest() {
        targetDir = Paths.get(System.getProperty("xlsdir", TestConstants.TEST_JXLS_DEST_DIR)).toFile();

        //FIXME: the maven process-resources phase isn't copying the jxjs-template dir to target/test-resources.
        templateDir = Paths.get(System.getProperty("xlstemplatedir", TEST_JXLS_TEMPLATE_DIR)).toFile();
    }

    void mkdirs(File dir) {
        if(!dir.exists()) {
            dir.mkdirs();
            logger.debug("directory created:{}", dir.getAbsolutePath());
        }
    }

    @Before
    public void beforeEach() {
        mkdirs(targetDir);
        mkdirs(templateDir);
    }


    @Test
    public void testSimpleOutput() throws IOException {
        ExcelTemplateProcessor processor = new ExcelTemplateProcessor();
        File outfile = File.createTempFile("foo", ".xls", targetDir);

        List<String> headers = Arrays.asList("FirstName", "LastName", "Email");
        List<Person> data = new ArrayList<Person>() {{
            add(new Person("jim", "devos", "email1"));
            add(new Person("john", "doe", "email2"));
            add(new Person("jane", "doe", "email3"));
        }};

        List<String> names = Arrays.asList("firstName", "lastName", "email");

        OutputStream os = new FileOutputStream(outfile);
        processor.process(headers, data, names, os);

        // TODO: use POI to open the resultant file and slurp the data back in, then verify w/ data in test
    }


    @Test
    public void testTemplate() throws IOException {
        File outfile = new File(targetDir, "object_collection_output.xls");

        List<Employee> employees = generateSampleEmployeeData(20);
        try(InputStream is = new FileInputStream(new File(TEST_JXLS_TEMPLATE_DIR, "object_collection_template.xls"))) {
            try (OutputStream os = new FileOutputStream(outfile)) {
                Context context = new Context();
                context.putVar("employees", employees);
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }

    }

    @Test
    public void testSearchResultsTemplate() throws IOException {
        File outfile = new File(targetDir, "search_results_output.xls");

        List<Employee> employees = generateSampleEmployeeData(20);
        try(InputStream is = new FileInputStream(new File(TEST_JXLS_TEMPLATE_DIR, "search_results_template.xls"))) {
            try (OutputStream os = new FileOutputStream(outfile)) {
                Context context = new Context();
                context.putVar("employees", employees);
                context.putVar("info", new HashMap(){{
                    put("dateCreated", new Date(0));
                    put("url", "http://www.tdar.org");
                    put("description", "hello mom!");
                }});
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }

    }


    private List<Employee> generateSampleEmployeeData(int size) {
        return IntStream
                .rangeClosed(0, size - 1)
                .boxed()
                .map((i) -> Employee.randomEmployee())
                .collect(Collectors.toList());
    }

}
