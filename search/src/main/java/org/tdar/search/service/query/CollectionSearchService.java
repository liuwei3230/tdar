package org.tdar.search.service.query;

import java.io.IOException;

import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.search.bean.CollectionSearchQueryObject;
import org.tdar.search.exception.SearchException;
import org.tdar.search.query.LuceneSearchResultHandler;

import com.opensymphony.xwork2.TextProvider;

public interface CollectionSearchService {

    LuceneSearchResultHandler<PResourceCollection> buildResourceCollectionQuery(TdarUser authenticatedUser, CollectionSearchQueryObject query,
            LuceneSearchResultHandler<PResourceCollection> result, TextProvider provider) throws SearchException, IOException;

    LuceneSearchResultHandler<PResourceCollection> lookupCollection(TdarUser authenticatedUser, CollectionSearchQueryObject csqo,
            LuceneSearchResultHandler<PResourceCollection> result, TextProvider provider) throws SearchException, IOException;

}