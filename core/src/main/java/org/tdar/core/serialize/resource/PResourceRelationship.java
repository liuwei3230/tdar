package org.tdar.core.serialize.resource;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.resource.ResourceRelationship.ResourceRelationshipType;

/**
 * $Id$
 * <p>
 * A persistable pointer to a resource, stored by a registered user of tDAR.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

public class PResourceRelationship extends AbstractPersistable {

    private PResource sourceResource;
    private PResource targetResource;
    private ResourceRelationshipType type;


    public ResourceRelationshipType getType() {
        return type;
    }

    public void setType(ResourceRelationshipType type) {
        this.type = type;
    }

    public PResource getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(PResource targetResource) {
        this.targetResource = targetResource;
    }

    public PResource getSourceResource() {
        return sourceResource;
    }

    public void setSourceResource(PResource sourceResource) {
        this.sourceResource = sourceResource;
    }
}
