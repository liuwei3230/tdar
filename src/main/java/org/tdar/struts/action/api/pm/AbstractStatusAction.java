package org.tdar.struts.action.api.pm;

import org.tdar.core.bean.collection.ResourceCollection;

public abstract class AbstractStatusAction extends AbstractTaskAction {

    private static final long serialVersionUID = 8506051043321683379L;

    private Long collectionId;
    private ResourceCollection collection;

    @Override
    public void prepare() throws Exception {
        super.prepare();
        // can we remove this dependency
        this.collection = getGenericService().find(ResourceCollection.class, collectionId);
     }
    
    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public ResourceCollection getCollection() {
        return collection;
    }

    public void setCollection(ResourceCollection collection) {
        this.collection = collection;
    }

}
