package org.tdar.struts.action.collection;

import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.struts.action.AbstractStatisticsAction;
import org.tdar.struts.interceptor.annotation.HttpsOnly;
import org.tdar.web.service.WebLoadingService;

import com.opensymphony.xwork2.Preparable;

@Component
@Scope("prototype")
@ParentPackage("secured")
@Namespace("/collection/usage")
@HttpsOnly
public class CollectionStatisticsAction extends AbstractStatisticsAction implements Preparable {

    private static final long serialVersionUID = 1653124517249681107L;

    private PResourceCollection collection;
    @Autowired
    private WebLoadingService webLoadingService;

    @Override
    public void prepare() throws Exception {
        ResourceCollection collection_ = getGenericService().find(ResourceCollection.class, getId());
        collection = webLoadingService.proxy(collection_, getAuthenticatedUser());
        if (collection == null) {
            addActionError("collectionStatisticsAction.no_collection");
        }
        
        setStatsForAccount(statisticsService.getStatsForCollection(collection_, this, getGranularity()));
        setupJson();
    }

    public PResourceCollection getCollection() {
        return collection;
    }

    public void setCollection(PResourceCollection collection) {
        this.collection = collection;
    }
}
