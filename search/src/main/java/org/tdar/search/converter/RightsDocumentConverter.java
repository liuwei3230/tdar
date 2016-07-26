package org.tdar.search.converter;

import org.apache.solr.common.SolrInputDocument;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.resource.Resource;
import org.tdar.search.query.QueryFieldNames;
import org.tdar.search.service.SearchUtils;

public class RightsDocumentConverter extends AbstractSolrDocumentConverter{

    public static SolrInputDocument convert(Resource resource) {
        ResourceRightsExtractor rightsExtractor = new ResourceRightsExtractor(resource);
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(QueryFieldNames.ID, resource.getId());
        Class<? extends Indexable> class1 = resource.getClass();
        doc.setField(QueryFieldNames.CLASS, class1.getName());
        doc.setField(QueryFieldNames._ID, SearchUtils.createKey(resource));
        doc.setField(QueryFieldNames.RESOURCE_USERS_WHO_CAN_MODIFY, rightsExtractor.getUsersWhoCanModify());
        doc.setField(QueryFieldNames.RESOURCE_USERS_WHO_CAN_VIEW, rightsExtractor.getUsersWhoCanView());

        doc.setField(QueryFieldNames.RESOURCE_COLLECTION_DIRECT_SHARED_IDS, rightsExtractor.getDirectCollectionIds());
        doc.setField(QueryFieldNames.RESOURCE_COLLECTION_SHARED_IDS, rightsExtractor.getCollectionIds());
        doc.setField(QueryFieldNames.RESOURCE_COLLECTION_IDS, rightsExtractor.getAllCollectionIds());
        doc.setField(QueryFieldNames.RESOURCE_COLLECTION_NAME, rightsExtractor.getCollectionNames());
        return doc;
    }

}
