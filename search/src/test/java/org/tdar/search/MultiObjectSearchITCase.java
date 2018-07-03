package org.tdar.search;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.tdar.AbstractWithIndexIntegrationTestCase;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.integration.DataIntegrationWorkflow;
import org.tdar.core.bean.resource.Dataset;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.integration.PDataIntegrationWorkflow;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.search.bean.AdvancedSearchQueryObject;
import org.tdar.search.exception.SearchException;
import org.tdar.search.exception.SearchIndexException;
import org.tdar.search.query.SearchResult;
import org.tdar.search.service.query.ResourceSearchService;
import org.tdar.utils.MessageHelper;
import org.tdar.utils.PersistableUtils;

public class MultiObjectSearchITCase extends AbstractWithIndexIntegrationTestCase {

    @Autowired
    ResourceSearchService resourceSearchService;
    private DataIntegrationWorkflow workflow;
    private Dataset dataset;
    private ResourceCollection collection;

    @Before
    public void setup() {
        dataset = createAndSaveNewDataset();
        collection = createAndSaveNewResourceCollection("test collection");
        workflow = new DataIntegrationWorkflow("test integration", false, getAdminUser());
        workflow.markUpdated(getAdminUser());
        genericService.saveOrUpdate(workflow);
    }

    @Test
    @Rollback
    public void testAllActive() throws SearchException, SearchIndexException, IOException {
        dataset.setStatus(Status.ACTIVE);
        collection.setStatus(Status.ACTIVE);
        collection.setHidden(false);
        genericService.saveOrUpdate(workflow);
        genericService.saveOrUpdate(collection);
        genericService.saveOrUpdate(dataset);
        searchIndexService.index(workflow, collection, dataset);
        SearchResult<PResource> result = performSearch("", null, 100);
        for (Indexable r : result.getResults()) {
            logger.debug(" {} " ,r);
        }
        assertTrue("should see collection", PersistableUtils.extractIds(result.getResults()).contains(collection.getId()));
        assertTrue("should see dataset", PersistableUtils.extractIds(result.getResults()).contains(dataset.getId()));
        assertTrue("should see integration", PersistableUtils.extractIds(result.getResults()).contains(workflow.getId()));
    }
    
    @Test
    @Rollback
    public void testAllHidden() throws SearchException, SearchIndexException, IOException {

        /// change statuses to hidden
        dataset.setStatus(Status.DRAFT);
        workflow.setHidden(true);
        collection.setHidden(true);
        genericService.saveOrUpdate(workflow);
        genericService.saveOrUpdate(collection);
        genericService.saveOrUpdate(dataset);
        searchIndexService.index(workflow, collection, dataset);
        SearchResult<PResource> result = performSearch("", null, 100);
//        logger.debug("results:{}", result.getResults());
        logger.debug("datasetId: {}", dataset.getId());
        logger.debug("workflowId: {}", workflow.getId());
        logger.debug("collectionId: {}", collection.getId());
        boolean seenDataset = false;
        boolean seenWorkflow = false;
        boolean seenCollection = false;
        for (Persistable p : result.getResults()) {
            logger.debug("{} -- {}", p.getId(), p);
            if (p instanceof PResourceCollection && p.getId() == collection.getId()) {
                logger.debug(">> seen collection");
                seenCollection = true;
            }
            if (p instanceof PDataIntegrationWorkflow && p.getId() == workflow.getId()) {
                logger.debug(">> seen workflow");
                seenWorkflow = true;
            }
            if (p instanceof PDataset && p.getId() == dataset.getId()) {
                logger.debug(">> seen dataset");
                seenDataset = true;
            }
        }
        List<Long> ids = PersistableUtils.extractIds(result.getResults());
        assertFalse("should see collection", seenCollection);
        assertFalse("should see dataset", seenDataset);
        assertFalse("should see integration", seenWorkflow);
    }

    @Test
    @Rollback
    public void testCollectionDraft() throws SearchException, SearchIndexException, IOException {
        /// change collection to draft, but visible
        collection.setHidden(false);
        collection.setStatus(Status.DRAFT);
        genericService.save(collection);
        searchIndexService.index(workflow, collection, dataset);
        SearchResult<PResource> result = performSearch("", null, 100);
        logger.debug("results:{}", result.getResults());
        assertFalse("should see collection", PersistableUtils.extractIds(result.getResults()).contains(collection.getId()));
    }

    @Test
    @Rollback
    public void testCollectionDraftAndHidden() throws SearchException, SearchIndexException, IOException {
        /// change collection to draft, but visible
        collection.setHidden(true);
        collection.setStatus(Status.DRAFT);
        genericService.save(collection);
        searchIndexService.index(workflow, collection, dataset);
        SearchResult<PResource> result = performSearch("", null, 100);
        logger.debug("results:{}", result.getResults());
        assertFalse("should see collection", PersistableUtils.extractIds(result.getResults()).contains(collection.getId()));
    }

    @Test
    @Rollback
    public void testCollectionDeleted() throws SearchException, SearchIndexException, IOException {
        /// change collection to draft, but visible
        collection.setHidden(false);
        collection.setStatus(Status.DELETED);
        genericService.save(collection);
        searchIndexService.index(workflow, collection, dataset);
        SearchResult<PResource> result = performSearch("", null, 100);
        logger.debug("results:{}", result.getResults());
        assertFalse("should see collection", PersistableUtils.extractIds(result.getResults()).contains(collection.getId()));
    }

    public SearchResult<PResource> performSearch(String term, TdarUser user, int max) throws IOException, SearchException, SearchIndexException {
        SearchResult<PResource> result = new SearchResult<>(max);
        result.setAuthenticatedUser(user);
        AdvancedSearchQueryObject asqo = new AdvancedSearchQueryObject();
        asqo.setMultiCore(true);
        asqo.setQuery(term);
        resourceSearchService.buildAdvancedSearch(asqo, user, result, MessageHelper.getInstance());
        // (TdarUser user, ResourceLookupObject look, LuceneSearchResultHandler<Resource> result,
        // TextProvider support) throws SearchException, IOE
        return result;
    }

}
