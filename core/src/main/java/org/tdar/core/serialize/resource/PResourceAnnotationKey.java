package org.tdar.core.serialize.resource;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.HasLabel;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.resource.ResourceAnnotationDataType;
import org.tdar.core.bean.resource.ResourceAnnotationType;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * Semi-controlled list of possible resource identifier keys.
 * 
 * 
 * @author <a href='mailto:allen.lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class PResourceAnnotationKey extends AbstractPersistable implements Indexable, HasLabel {

    public PResourceAnnotationKey() {
    }

    public PResourceAnnotationKey(String key) {
        this.key = key;
        this.resourceAnnotationType = ResourceAnnotationType.IDENTIFIER;
    }

    private ResourceAnnotationType resourceAnnotationType;
    private ResourceAnnotationDataType annotationDataType;
    @JsonView(JsonLookupFilter.class)
    private String key;
    private String formatString;

    @XmlAttribute
    public ResourceAnnotationType getResourceAnnotationType() {
        return resourceAnnotationType;
    }

    public void setResourceAnnotationType(ResourceAnnotationType resourceAnnotationType) {
        this.resourceAnnotationType = resourceAnnotationType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @XmlAttribute
    public ResourceAnnotationDataType getAnnotationDataType() {
        return annotationDataType;
    }

    public void setAnnotationDataType(ResourceAnnotationDataType annotationDataType) {
        this.annotationDataType = annotationDataType;
    }

    public String format(String value) {
        // FIXME: not applying format strings yet.
        return value;
    }

    public String getFormatString() {
        return formatString;
    }

    public void setFormatString(String formatString) {
        this.formatString = formatString;
    }

    @Override
    public String toString() {
        return "[key:'" + key + "' id:" + getId() + "]";
    }

    @Override
    public List<?> getEqualityFields() {
        // ab probably okay as not nullable fields
        return Arrays.asList(key);
    }

    @Transient
    @Override
    @JsonView(JsonLookupFilter.class)
    public String getLabel() {
        return this.key;
    }
}
