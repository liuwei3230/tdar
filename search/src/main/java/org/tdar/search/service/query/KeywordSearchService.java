package org.tdar.search.service.query;

import java.io.IOException;

import org.tdar.core.serialize.keyword.PKeyword;
import org.tdar.search.exception.SearchException;
import org.tdar.search.query.LuceneSearchResultHandler;

import com.opensymphony.xwork2.TextProvider;

public interface KeywordSearchService<I extends PKeyword> {

    LuceneSearchResultHandler<I> findKeyword(String term, String keywordType, LuceneSearchResultHandler<I> result, TextProvider provider, int min)
            throws SearchException, IOException;

}