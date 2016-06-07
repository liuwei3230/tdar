package org.tdar.search.excel;

import org.tdar.core.bean.resource.Resource;
import org.tdar.core.configuration.TdarConfiguration;
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

    //translate a resource into an object to be rendered by the templateProcessor
    private void translateRows(SearchResultHandler<Resource> handler) {
//        int rowNum = 0;
//        int maxRow = getMaxDownloadRecords();
//        if (maxRow > handler.getTotalRecords()) {
//            maxRow = handler.getTotalRecords();
//        }
//        if (getTotalRecords() > 0) {
//            ExcelWorkbookWriter excelWriter  = new ExcelWorkbookWriter();
//            Sheet sheet = excelWriter.createWorkbook("results",SpreadsheetVersion.EXCEL2007);
//
//            List<String> fieldNames = new ArrayList<String>(Arrays.asList(
//                    "id", RESOURCETYPE, TITLE, "date", "authors",
//                    PROJECT, DESCRIPTION, "number_of_files", "url",
//                    "physical_location"));
//
//            if (isEditor()) {
//                fieldNames.add("status");
//                fieldNames.add("filenames");
//                fieldNames.add("date_added");
//                fieldNames.add("submitted_by");
//                fieldNames.add("date_last_updated");
//                fieldNames.add("updated_by");
//            }
//
//            // ADD HEADER ROW THAT SHOWS URL and SEARCH PHRASE
//            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, fieldNames.size()));
//            excelWriter.addDocumentHeaderRow(sheet, rowNum, 0,
//                    Arrays.asList(getText("advancedSearchController.excel_search_results", TdarConfiguration.getInstance().getSiteAcronym(),
//                            getSearchPhrase())));
//            rowNum++;
//            List<String> headerValues = Arrays.asList(getText("advancedSearchController.search_url"), UrlService.getBaseUrl()
//                    + getServletRequest().getRequestURI()
//                    .replace("/download", "/results") + "?" + getServletRequest().getQueryString());
//            excelWriter.addPairedHeaderRow(sheet, rowNum, 0, headerValues);
//            rowNum++;
//            excelWriter.addPairedHeaderRow(sheet, rowNum, 0,
//                    Arrays.asList(getText("advancedSearchController.downloaded_by"),
//                            getText("advancedSearchController.downloaded_on", getAuthenticatedUser().getProperName(), new Date())));
//            rowNum++;
//            rowNum++;
//            for (int i = 0; i < fieldNames.size(); i++) {
//                fieldNames.set(i, getText("advancedSearchController." + fieldNames.get(i)));
//            }
//
//            excelWriter.addHeaderRow(sheet, rowNum, 0, fieldNames);
//            int startRecord = 0;
//            int currentRecord = 0;
//            while (currentRecord < maxRow) {
//                startRecord = getNextPageStartRecord();
//                setStartRecord(getNextPageStartRecord()); // resetting for
//                // next search
//                for (Resource result : getResults()) {
//                    rowNum++;
//                    if (currentRecord++ > maxRow) {
//                        break;
//                    }
//                    Resource r = result;
//                    Integer dateCreated = null;
//                    Integer numFiles = 0;
//                    List<String> filenames = new ArrayList<String>();
//                    String location = "";
//                    String projectName = "";
//                    if (result instanceof InformationResource) {
//                        InformationResource ir = (InformationResource) result;
//                        dateCreated = ir.getDate();
//                        numFiles = ir.getTotalNumberOfFiles();
//                        for (InformationResourceFileVersion file : ir.getLatestUploadedVersions()) {
//                            filenames.add(file.getFilename());
//                        }
//                        InformationResource ires = ((InformationResource) r);
//                        location = ires.getCopyLocation();
//                        projectName = ires.getProjectTitle();
//
//                    }
//                    List<Creator<?>> authors = new ArrayList<>();
//
//                    for (ResourceCreator creator : r.getPrimaryCreators()) {
//                        authors.add(creator.getCreator());
//                    }
//
//                    ArrayList<Object> data = new ArrayList<Object>(
//                            Arrays.asList(r.getId(), r.getResourceType(), r.getTitle(), dateCreated, authors,
//                                    projectName, r.getShortenedDescription(), numFiles,
//                                    UrlService.absoluteUrl(r), location));
//
//                    if (isEditor()) {
//                        data.add(r.getStatus());
//                        data.add(StringUtils.join(filenames, ","));
//                        data.add(r.getDateCreated());
//                        data.add(r.getSubmitter().getProperName());
//                        data.add(r.getDateUpdated());
//                        data.add(r.getUpdatedBy().getProperName());
//                    }
//
//                    excelWriter.addDataRow(sheet, rowNum, 0, data);
//                }
//                if (startRecord < getTotalRecords()) {
//                    performResourceSearch();
//                }
//            }
//
//            //                excelWriter.setColumnWidth(sheet, 0, 5000);
//            for (int i=0; i < fieldNames.size();i++) {
//                if (StringUtils.containsAny(fieldNames.get(i), TITLE,PROJECT,DESCRIPTION,RESOURCETYPE)) {
//                    sheet.setColumnWidth(i, 20);
//                } else {
//                    sheet.autoSizeColumn(i, false);
//                }
//            }
//            File tempFile = File.createTempFile("results", ".xls");
//            FileOutputStream fos = new FileOutputStream(tempFile);
//            sheet.getWorkbook().write(fos);
//            fos.close();
//            setInputStream(new FileInputStream(tempFile));
//            contentLength = tempFile.length();

    }

    private int getMaxDownloadRecords() {
        return TdarConfiguration.getInstance().getSearchExcelExportRecordMax();
    }

}

