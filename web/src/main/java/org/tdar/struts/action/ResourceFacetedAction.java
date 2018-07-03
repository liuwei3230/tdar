package org.tdar.struts.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.Sortable;
import org.tdar.search.query.facet.Facet;
import org.tdar.utils.PersistableUtils;

public interface ResourceFacetedAction {

    List<Facet> getResourceTypeFacets();

    default void reSortFacets(ResourceFacetedAction handler, Sortable persistable) {
        // sort facets A-Z unless sortOption explicitly otherwise
        if (PersistableUtils.isNotNullOrTransient(getPersistable()) && CollectionUtils.isNotEmpty(handler.getResourceTypeFacets())) {
            Collections.sort(handler.getResourceTypeFacets(), new Comparator<Facet>() {
                @Override
                public int compare(Facet o1, Facet o2) {
                    return o1.getRaw().compareTo(o2.getRaw());
                }
            });
        }
    }

    Persistable getPersistable();

}
