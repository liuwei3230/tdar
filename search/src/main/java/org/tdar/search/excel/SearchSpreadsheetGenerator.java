package org.tdar.search.excel;

import org.tdar.search.query.SearchResultHandler;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tranform tDAR search results into an excel spreadsheet.
 */
public class SearchSpreadsheetGenerator {



    public static final Path SEARCH_RESULTS_TEMPLATE = Paths.get("/jxls-templates", "search_results_template.xls");
    public static final Path SEARCH_RESULTS_EDITOR_TEMPLATE = Paths.get("/jxls-templates", "search_results_editor_template.xls");

    public SearchSpreadsheetGenerator() {

    }

    public void generate(SearchResultHandler results, OutputStream outputStream) {

    }

    public InputStream getSearchResultsTemplate() {
        return getClass().getResourceAsStream(SEARCH_RESULTS_TEMPLATE.toString());
    }

    public InputStream getSearchResultsEditorTemplate() {
        return getClass().getResourceAsStream(SEARCH_RESULTS_EDITOR_TEMPLATE.toString());

    }




}
