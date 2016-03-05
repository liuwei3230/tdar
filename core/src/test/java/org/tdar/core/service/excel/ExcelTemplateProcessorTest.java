package org.tdar.core.service.excel;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.entity.Person;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

/**
 * Tests for basic template processor
 */
public class ExcelTemplateProcessorTest {

    File targetDir;
    Logger logger = LoggerFactory.getLogger(getClass());

    public ExcelTemplateProcessorTest() {}

    @Before
    public void beforeEach() {
        String path = System.getProperty("xlsdir", "target/generated/excel");
        targetDir = Paths.get(path).toFile();
        logger.debug("target: {}", targetDir.getAbsolutePath());

        if (!targetDir.exists()){
            targetDir.mkdirs();
        }
    }

    @After
    public void afterEach() {
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

}
