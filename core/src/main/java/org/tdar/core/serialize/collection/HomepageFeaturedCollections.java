package org.tdar.core.serialize.collection;

import org.tdar.core.bean.AbstractPersistable;

public class HomepageFeaturedCollections extends AbstractPersistable {

    private PResourceCollection featured;

    public PResourceCollection getFeatured() {
        return featured;
    }

    public void setFeatured(PResourceCollection featured) {
        this.featured = featured;
    }

}
