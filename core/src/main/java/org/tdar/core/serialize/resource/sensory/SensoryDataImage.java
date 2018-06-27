package org.tdar.core.serialize.resource.sensory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.tdar.core.bean.AbstractSequenced;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.HasResource;
import org.tdar.core.bean.resource.SensoryData;

/**
 * represents an image
 * 
 * @author abrin
 * 
 */
public class SensoryDataImage extends AbstractSequenced<SensoryDataImage> {

    private String filename;
    private String description;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
