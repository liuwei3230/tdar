package org.tdar.core.serialize.resource;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.resource.ResourceType;

/**
 * $Id$
 * <p>
 * Represnts an image object in TDAR
 * </p>
 * 
 * @author Adam Brin
 * @version $Revision: 543$
 */
@XmlRootElement(name = "Pimage")
public class PImage extends PInformationResource {


    public PImage() {
        setResourceType(ResourceType.IMAGE);
    }

    @Override
    @Transient
    public boolean isSupportsThumbnails() {
        return true;
    }

    @Override
    @Transient
    public boolean isHasBrowsableImages() {
        return true;
    }
}
