package org.tdar.search.excel;

import org.junit.Test;
import org.tdar.core.bean.resource.Resource;
import org.tdar.search.query.SearchResultHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

/**
 * Created by jimdevos on 5/31/16.
 */
public class SearchSpreadsheetGeneratorTest {

    SearchSpreadsheetGenerator generator = new SearchSpreadsheetGenerator();

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
        SearchResultHandler<Resource> mockResults = generateMockSearchResults(100);

        try( FileOutputStream fos = new FileOutputStream(outputFile)) {
            generator.generate(mockResults, fos);
        }

        assertThat(outputFile.exists(), is( true));
        assertThat("output file should have more than zero bytes", outputFile.length(), is ( greaterThan( 0L)));
    }



    private SearchResultHandler<Resource>  generateMockSearchResults(int recordCount) {
        SearchResultHandler<Resource> results  =  null;

        return results;
    }

}
