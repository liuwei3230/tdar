package org.tdar.core.service.excel;

import org.fluttercode.datafactory.impl.DataFactory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.TestConstants;
import org.tdar.core.bean.entity.Person;
import org.tdar.core.bean.util.UrlUtils;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.StringUtils.join;
import static org.tdar.TestConstants.TEST_JXLS_TEMPLATE_DIR;
import static org.tdar.core.service.excel.Employee.randomEmployee;


/**
 * Tests for basic template processor
 */
// TODO: use ExcelUnit to assert contents of generated files
public class ExcelTemplateProcessorTest {

    Logger logger = LoggerFactory.getLogger(getClass());
    File targetDir = Paths.get(System.getProperty("xlsdir", TestConstants.TEST_JXLS_DEST_DIR)).toFile();
    File templateDir = Paths.get(System.getProperty("xlstemplatedir", TEST_JXLS_TEMPLATE_DIR)).toFile();;
    ExcelTemplateProcessor templateProcessor = new ExcelTemplateProcessor();

    // Use constant seed for "random" data to be repeatable across tests.
    DataFactory dataFactory = new DataFactory();
    Random random = new Random(1L);

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
        //return Stream.of(entries).collect(Collectors.toMap((e) -> e.getKey(), (e) -> e.getValue()));
        Map<K, U> map = new HashMap<>();
        for(Map.Entry<K, U> entry : entries) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }


    @Before
    public void beforeEach() {
        mkdirs(targetDir);
        mkdirs(templateDir);
    }


    @Test
    public void testSimpleOutput() throws IOException {
        File outfile = new File(targetDir, "test-simple-output.xls");
        List<String> headers = asList("FirstName", "LastName", "Email");

        List<String>  colnames = asList("firstName", "lastName", "email");
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
        Map<String, Object> data = newMap(entry("employees", generateItems(20, 20, () -> randomEmployee())));
        templateProcessor.process(template, data, outfile);
    }

    @Test
    public void testSearchResultsTemplate() throws IOException {
        File template = new File(templateDir, "search_results_template.xls");
        File outfile = new File(targetDir, "search_results_output.xls");
        Map<String, Object> data = newMap(
                entry("employees", generateItems(20, 20, () -> randomEmployee())),
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
                entry("results", generateItems(500, 1000, () -> generateEditorSearchResult()))
        );

        templateProcessor.process(template, data, outfile);
    }

    /**
     * Generate list of pseudorandom size.
     * @param minSize
     * @param maxSize
     * @param supplier
     * @param <T>
     * @return
     */
    public <T> Iterable<T> generateItems(int minSize, int maxSize, Supplier<T> supplier ) {
        List<T> list = new ArrayList<>();
        int length = minSize;
        if(minSize < maxSize) {
            length = (random.nextInt() % (maxSize - minSize)) + minSize;
        }
        for(int i = 0; i < length; i++) {
            list.add(supplier.get());
        }
        return list;
    }


    public Map<String, Object> generateSearchResult() {
        String resourceType = dataFactory.getItem(asList("docuement", "dataset", "coding-sheet", "image", "ontology", "taco"));
        String id = dataFactory.getNumberText(6);
        String title = dataFactory.getBusinessName();
        Map<String, Object> result =
            newMap(
                    entry("id", id),
                    entry("resourceType", resourceType),
                    entry("title", title),
                    entry("authors", join(generateItems(0, 3, () -> dataFactory.getName()), "; ")),
                    entry("project", dataFactory.getBusinessName() + " Project"),
                    entry("description", dataFactory.getRandomText(20, 40)),
                    entry("numberOfFiles", random.nextInt(10)),
                    entry("url", "http://core.tdar.org/" + resourceType + "/" + id + "/" + UrlUtils.slugify(title)),
                    entry("physicalLocation", dataFactory.getItem(asList(dataFactory.getCity()), 25))
            );
        return result;
    }

    public Map<String, Object> generateEditorSearchResult() {
        Map<String, Object> result = generateSearchResult();
        result.putAll(newMap(
                entry("status", dataFactory.getItem(asList("Active", "Active", "Active", "Deleted", "Draft"))),
                entry("fileNames", join(generateItems(0, 3, () -> dataFactory.getRandomWord(15) + ".jpg"), ", ")),
                entry("dateAdded", dataFactory.getDate(
                        new DateTime(1920, 1, 1, 1, 0).toDate(), 0, 365 * 90
                )),
                entry("dateRegistered", dataFactory.getDate(
                        new DateTime(2011, 1, 1, 1, 0).toDate(), 0, 365 * 2
                )),
                entry("dateUpdated", dataFactory.getDate(
                        new DateTime(2013, 1, 1, 1, 0).toDate(), 0, 365 * 2
                )),
                entry("updatedBy", dataFactory.getName())
        ));
        return result;
    }
}
