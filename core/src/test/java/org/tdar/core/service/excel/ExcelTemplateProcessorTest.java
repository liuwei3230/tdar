package org.tdar.core.service.excel;

import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.TestConstants;
import org.tdar.core.bean.entity.Person;
import org.tdar.core.bean.resource.InformationResource;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.tdar.TestConstants.TEST_JXLS_TEMPLATE_DIR;

/**
 * Tests for basic template processor
 */
// TODO: use ExcelUnit to assert contents of generated files
public class ExcelTemplateProcessorTest {

    Logger logger = LoggerFactory.getLogger(getClass());
    File targetDir = Paths.get(System.getProperty("xlsdir", TestConstants.TEST_JXLS_DEST_DIR)).toFile();
    File templateDir = Paths.get(System.getProperty("xlstemplatedir", TEST_JXLS_TEMPLATE_DIR)).toFile();;
    ExcelTemplateProcessor templateProcessor = new ExcelTemplateProcessor();


    void mkdirs(File dir) {
        if(!dir.exists()) {
            dir.mkdirs();
            logger.debug("directory created:{}", dir.getAbsolutePath());
        }
    }

    //todo: this map initializer is handy.   consider promoting, at least to other tests
    // return a Map.Entry
    static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    // return immutable map from an array of Map.Entry objects (do not insert nulls, okay?).
    static <K, U> Map<K, U> newMap(Map.Entry<K, U>... entries) {
        return Stream.of(entries).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
    }


    @Before
    public void beforeEach() {
        mkdirs(targetDir);
        mkdirs(templateDir);
    }

    private List<Employee> generateSampleEmployeeData(int size) {
        //Pretty sure a for-loop would have been easier here too, but who cares.  Onward!!
        return IntStream
                .rangeClosed(0, size - 1)
                .boxed()
                .map((i) -> Employee.randomEmployee())
                .collect(Collectors.toList());
    }


    @Test
    public void testSimpleOutput() throws IOException {
        File outfile = new File(targetDir, "test-simple-output.xls");
        List<String> headers = Arrays.asList("FirstName", "LastName", "Email");

        List<String>  colnames = Arrays.asList("firstName", "lastName", "email");
        List<Person> rows = new ArrayList<Person>() {{
            add(new Person("jim", "devos", "email1"));
            add(new Person("john", "doe", "email2"));
            add(new Person("jane", "doe", "email3"));
        }};

        OutputStream os = new FileOutputStream(outfile);
        templateProcessor.process(headers, rows, colnames, os);
    }


    @Test
    // essentially the same as jxls website example, adapted to use our wrapper function
    public void testSampleTemplate() throws IOException {
        File outfile = new File(targetDir, "object_collection_output.xls");
        File template = new File(templateDir,"object_collection_template.xls");
        Map<String, Object> data = newMap(entry("employees", generateSampleEmployeeData(20)));
        templateProcessor.process(template, data, outfile);
    }

    @Test
    public void testSearchResultsTemplate() throws IOException {
        File template = new File(templateDir, "search_results_template.xls");
        File outfile = new File(targetDir, "search_results_output.xls");
        Map<String, Object> data = newMap(
                entry("employees", generateSampleEmployeeData(20)),
                entry("info", newMap(
                        entry("dateCreated", new Date(0)),
                        entry("url", "http://www.tdar.org"),
                        entry("description", "this is a test")
                )));
        templateProcessor.process(template, data, outfile);
    }

    @Test
    public void testSearchResultEditorTemplate() throws IOException {
        File template = new File(templateDir, "search_results_editor_template.xls");
        File outfile = new File(targetDir, "search_results_editor_output.xls");

        Map<String, Object> data = newMap(
                entry("info", newMap(
                        entry("generatedFor", "John Dow, 1/1/2015"),
                        entry("searchUrl", "http://core.tdar.org/search/advanced?query=foo&query=bar")
                )),
                entry("results", Arrays.asList(
                        newMap(
                                entry("id", 1111L),
                                entry("title", "Hello World")
                        ),
                        newMap(
                                entry("id", 2222L),
                                entry("title", "Hello Mom")
                        )
                ))
        );

        templateProcessor.process(template, data, outfile);
    }

}
