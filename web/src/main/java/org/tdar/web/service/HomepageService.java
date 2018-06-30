package org.tdar.web.service;

import java.util.List;
import java.util.Set;

import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.cache.HomepageResourceCountCache;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.search.bean.AdvancedSearchQueryObject;
import org.tdar.search.query.facet.FacetedResultHandler;

import com.opensymphony.xwork2.TextProvider;

public interface HomepageService {

    HomepageDetails getSearchAndHomepageGraphs(TdarUser authenticatedUser, AdvancedSearchQueryObject advancedSearchQueryObject,
            FacetedResultHandler<PResource> result, TextProvider provider);

    HomepageDetails generateDetails(FacetedResultHandler<PResource> result);

    void setupResultForMapSearch(FacetedResultHandler<PResource> result);

    HomepageDetails getHomepageGraphs(TdarUser authenticatedUser, Long collectionId, boolean isBot, TextProvider provider);

    Set<Resource> featuredItems(TdarUser authenticatedUser);

    List<HomepageResourceCountCache> resourceStats();

    String getMapJson();

    String getResourceCountsJson();

}