package org.tdar.search.excel;

import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.Test;
import org.tdar.core.bean.entity.Person;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.*;
import org.tdar.core.bean.resource.file.InformationResourceFile;
import org.tdar.search.query.SearchResult;
import org.tdar.search.query.SearchResultHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.tdar.utils.MapUtils.generateItems;

/**
 * Created by jimdevos on 5/31/16.
 */
public class SearchSpreadsheetGeneratorTest {

    private SearchSpreadsheetGenerator generator = new SearchSpreadsheetGenerator();
    private DataFactory dataFactory = new DataFactory();
    private long id = 0L;


    Supplier<TdarUser> userSupplier = () -> new TdarUser(
            dataFactory.getFirstName(),
            dataFactory.getLastName(),
            dataFactory.getEmailAddress(),
            dataFactory.getRandomText(8,24).toLowerCase());

    Supplier<? extends Person> personSupplier = userSupplier;

    Supplier<Resource> resourceSupplier =  () -> {
        InformationResource r = dataFactory.getItem(new InformationResource[]{
                new Image(),
                new Document(),
                new Dataset()
        });
        r.setTitle(dataFactory.getRandomText(25, 200));
        r.setDescription(dataFactory.getRandomText(100, 500));
        r.setDateCreated(dataFactory.getDate(new Date(), 1, 365));
        r.setDateUpdated(dataFactory.getDateBetween(r.getDateCreated(), new Date()));
        r.setInformationResourceFiles(new HashSet<InformationResourceFile>(generateItems(0, 10, () -> {
            InformationResourceFile irf = new InformationResourceFile();
            irf.setFilename(dataFactory.getRandomChars(8));
            return irf;}
        )));
        return r;
    };

    private long nextId() {
        return id++;
    }

    @Test
    public void testFilesPresent() {
        assertThat( generator.getSearchResultsTemplate(), is( not ( nullValue())));
        assertThat( generator.getSearchResultsEditorTemplate(), is( not ( nullValue())));
    }

    /**
     * Did the generator spit out any bytes?
     */
    @Test
    public void testOutputSanityCheck() throws IOException {
        File outputFile = File.createTempFile("foo", ".xls");
        Map<String, Object> data = new HashMap<>();
        SearchResultHandler<Resource> mockResults = generateMockSearchResults();

        try( FileOutputStream fos = new FileOutputStream(outputFile)) {
            generator.generate(mockResults, fos);
        }

        assertThat(outputFile.exists(), is( true));
        assertThat("output file should have more than zero bytes", outputFile.length(), is ( greaterThan( 0L)));
    }

    private SearchResultHandler<Resource>  generateMockSearchResults() {
        SearchResult<Resource> handler = new SearchResult<>();
        handler.setMode("SEARCH");
        handler.setAuthenticatedUser(userSupplier.get());
        handler.setSearchTitle(dataFactory.getRandomText(25, 50));
        handler.setSearchDescription(dataFactory.getRandomText(75,200));
        handler.setResults(generateItems(100,1000, resourceSupplier));
        return handler;
    }
}
