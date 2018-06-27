package org.tdar.core.serialize.collection;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
