package org.tdar.search.excel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.WrapDynaBean;
import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.Sequenceable;
import org.tdar.core.bean.resource.InformationResource;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.configuration.TdarConfiguration;
import org.tdar.core.service.UrlService;
import org.tdar.core.service.excel.ExcelTemplateProcessor;
import org.tdar.search.query.SearchResultHandler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Tranform tDAR search results into an excel spreadsheet.
 */
public class SearchSpreadsheetGenerator {



    public static final Path SEARCH_RESULTS_TEMPLATE = Paths.get("/jxls-templates", "search_results_template.xls");
    public static final Path SEARCH_RESULTS_EDITOR_TEMPLATE = Paths.get("/jxls-templates", "search_results_editor_template.xls");

    public SearchSpreadsheetGenerator() {

    }

     void generate(SearchResultHandler<Resource> results, InputStream template, OutputStream outputStream, String searchUrl) throws IOException {
        ExcelTemplateProcessor templateProcessor = new ExcelTemplateProcessor();
        Map<String, Object> dataSource = getDataSource(results, searchUrl);
        templateProcessor.process(template, dataSource, outputStream);
    }

    public InputStream getSearchResultsTemplate() {
        return getClass().getResourceAsStream(SEARCH_RESULTS_TEMPLATE.toString());
    }

    public InputStream getSearchResultsEditorTemplate() {
        return getClass().getResourceAsStream(SEARCH_RESULTS_EDITOR_TEMPLATE.toString());

    }

    //transform the search results into the datasource that we will feed into the template processor
    private Map<String, Object> getDataSource(SearchResultHandler<Resource> handler, String searchUrl)  {

        // Create the datasource for the template and populate w/ top-level info
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> rows = new ArrayList<>();
        data.put("searchUrl", searchUrl);
        data.put("generatedFor", handler.getAuthenticatedUser().getProperName());
        data.put("currentDate", new Date());
        data.put("siteAcronym", TdarConfiguration.getInstance().getSiteAcronym());
        data.put("results", rows);

        // Process the actual search results and put then in the 'results' list.
        for(Resource result : handler.getResults()) {
            Map<String, Object> row = new HashMap<>();

            //basic fields that go on either xls report
            row.put("id", result.getId());
            row.put("resourceType", result.getResourceType());
            row.put("title", result.getTitle());
            row.put("description", result.getShortenedDescription());
            row.put("numberOfFiles", 0);
            row.put("url", UrlService.absoluteUrl(result));
            row.put("physicalLocation", "");
            row.put("dateCreated", null);
            row.put("authors", getResourceAuthors(result));

            //extended fields only seen by editors/admins
            row.put("status", result.getStatus().getLabel());
            row.put("fileNames", "");
            row.put("dateRegistered", null);
            row.put("dateAdded", result.getDateCreated());
            row.put("dateUpdated", result.getDateUpdated());

            //some fields only apply to information resources
            //fixme: consider moving consider making "blank" getters to parent class (read-only)
            if(result instanceof InformationResource) {
                InformationResource ir = (InformationResource)result;
                row.put("dateRegistered", ir.getDate());
                row.put("numberOfFiles", ir.getTotalNumberOfFiles());
                row.put("fileNames", getResourceFiles(ir));
            }
            rows.add(row);
        }

        return data;
    }

    // Return list of authors as single string.
    private String getResourceAuthors(Resource r) {
        return r.getPrimaryCreators().stream()
                .sorted(Comparator.comparing(Sequenceable::getSequenceNumber))
                .map((resourceCreator) -> resourceCreator.getCreator().getProperName())
                .collect(Collectors.joining(", "));
    }

    // Return list of files as single string (truncates list if too many files)
    private String getResourceFiles(InformationResource ir) {
        int maxFileListSize = 10;
        String files =ir.getLatestUploadedVersions().stream()
                .map( (irfv) -> irfv.getFilename())
                .sorted(Comparator.comparing(String::toLowerCase))
                .limit(maxFileListSize)
                .collect(Collectors.joining(", "));
        if(ir.getTotalNumberOfFiles() > maxFileListSize) {
            files = files + " ( and " + (ir.getTotalNumberOfFiles() - maxFileListSize) + " more)";
        }
        return files;
    }

    private int getMaxDownloadRecords() {
        return TdarConfiguration.getInstance().getSearchExcelExportRecordMax();
    }

}

