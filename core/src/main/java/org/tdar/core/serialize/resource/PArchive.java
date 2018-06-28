package org.tdar.core.serialize.resource;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.resource.ResourceType;

/**
 * A compressed archive. From FAIMS, the hope is that it will be unpacked and its constituent parts imported as separate documents.
 * 
 * @author Martin Paulo
 */
@XmlRootElement(name = "archive")
public class PArchive extends PInformationResource {

    public PArchive() {
        setResourceType(ResourceType.ARCHIVE);
    }


}
