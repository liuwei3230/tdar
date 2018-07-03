package org.tdar.search;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.tdar.core.bean.keyword.SiteNameKeyword;
import org.tdar.core.bean.resource.Document;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.search.bean.SearchParameters;
import org.tdar.search.exception.SearchException;
import org.tdar.search.exception.SearchIndexException;
import org.tdar.search.query.SearchResult;
import org.tdar.utils.PersistableUtils;

public class ResourceSpecialCharacterSearchITCase extends AbstractResourceSearchITCase {

    @Test
    public void testSearchPhraseWithQuote() throws ParseException, SearchException, SearchIndexException, IOException {
        doSearch("\"test");
    }

    @Test
    public void testSearchPhraseWithColon() throws ParseException, SearchException, SearchIndexException, IOException {
        doSearch("\"test : abc ");
    }

    @Test
    public void testSearchPhraseWithLuceneSyntax() throws ParseException, SearchException, SearchIndexException, IOException {
        doSearch("title:abc");
    }

    @Test
    public void testSearchPhraseWithUnbalancedParenthesis() throws ParseException, SearchException, SearchIndexException, IOException {
        doSearch("\"test ( abc ");
    }

    @Test
    @Rollback(true)
    public void testHyphenatedSearchBasic() throws InstantiationException, IllegalAccessException, SearchException, SearchIndexException, IOException,
            ParseException, SearchException, SearchIndexException {
        String resourceTitle = _33_CU_314;
        Document document = createAndSaveNewInformationResource(Document.class, getBasicUser(), resourceTitle);
        searchIndexService.index(document);

        setupTestDocuments();
        SearchResult<PResource> result = doSearch(resourceTitle);
        List<Long> results = PersistableUtils.extractIds(result.getResults());
        logger.info("results:{}", results);
        assertTrue(results.contains(document.getId()));
        assertTrue(results.get(0).equals(document.getId()) || results.get(1).equals(document.getId()));
    }

    @Test
    @Rollback(true)
    public void testHyphenatedTitleSearch() throws InstantiationException, IllegalAccessException, SearchException, SearchIndexException, IOException,
            ParseException, SearchException, SearchIndexException {
        String resourceTitle = _33_CU_314;
        Document document = createAndSaveNewInformationResource(Document.class, getBasicUser(), resourceTitle);
        searchIndexService.index(document);
        setupTestDocuments();
        SearchParameters params = new SearchParameters();
        params.getTitles().add(resourceTitle);
        SearchResult<PResource> result = doSearch("", null, params, null);
        List<Long> results = PersistableUtils.extractIds(result.getResults());
        logger.info("results:{}", results);
        assertTrue(results.contains(document.getId()));
        assertTrue(results.get(0).equals(document.getId()) || results.get(1).equals(document.getId()));
    }

    @Test
    @Rollback(true)
    public void testUnHyphenatedTitleSearch() throws InstantiationException, IllegalAccessException, SearchException, SearchIndexException, IOException,
            ParseException, SearchException, SearchIndexException {
        String resourceTitle = _33_CU_314;
        Document document = createAndSaveNewInformationResource(Document.class, getBasicUser(), resourceTitle);
        searchIndexService.index(document);
        setupTestDocuments();
        SearchParameters params = new SearchParameters();
        params.getTitles().add(resourceTitle.replaceAll("\\-", ""));
        SearchResult<PResource> result = doSearch("", null, params, null);
        List<Long> results = PersistableUtils.extractIds(result.getResults());
        logger.info("results:{}", result.getResults());
        assertTrue(results.contains(document.getId()));
        assertTrue(results.get(0).equals(document.getId()) || results.get(1).equals(document.getId()));
    }

    @Test
    @Rollback(true)
    public void testHyphenatedSiteNameSearch() throws InstantiationException, IllegalAccessException, SearchException, SearchIndexException, IOException,
            ParseException, SearchException, SearchIndexException {
        String resourceTitle = "what fun";
        SiteNameKeyword snk = new SiteNameKeyword();
        String label = _33_CU_314;
        snk.setLabel(label);
        Document document = createAndSaveNewInformationResource(Document.class, getBasicUser(), resourceTitle);
        genericService.save(snk);
        Long id = document.getId();
        document.getSiteNameKeywords().add(snk);
        searchIndexService.index(document);
        setupTestDocuments();
        SearchParameters params = new SearchParameters();
        params.getSiteNames().add(label);
        SearchResult<PResource> result = doSearch("", null, params, null);
        List<PResource> results = result.getResults();
        List<Long> ids = PersistableUtils.extractIds(results);
        logger.info("results:{}", results);
        assertTrue(ids.contains(id));
        assertTrue(ids.get(0).equals(id) || ids.get(1).equals(id));
    }

    @Test
    @Rollback(true)
    public void testHyphenatedSiteNameSearchCombined() throws InstantiationException, IllegalAccessException, SearchException, SearchIndexException,
            IOException, ParseException, SearchException, SearchIndexException {
        String resourceTitle = "what fun";
        SiteNameKeyword snk = new SiteNameKeyword();
        String label = _33_CU_314;
        snk.setLabel(label);
        Document document = createAndSaveNewInformationResource(Document.class, getBasicUser(), resourceTitle);
        genericService.save(snk);
        document.getSiteNameKeywords().add(snk);
        searchIndexService.index(document);
        setupTestDocuments();
        SearchResult<PResource> result = doSearch("what fun 33-Cu-314");
        logger.info("results:{}", result.getResults());
        List<Long> results = PersistableUtils.extractIds(result.getResults());
        assertTrue(results.contains(document.getId()));
        assertTrue(results.get(0).equals(document.getId()) || results.get(1).equals(document.getId()));
    }
}
