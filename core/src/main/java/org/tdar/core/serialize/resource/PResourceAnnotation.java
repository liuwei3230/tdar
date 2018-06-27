package org.tdar.core.serialize.resource;

import javax.xml.bind.annotation.XmlTransient;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.resource.ResourceAnnotationDataType;
import org.tdar.utils.json.JsonLookupFilter;
import org.tdar.utils.json.JsonProjectLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * A ResourceAnnotation represents a semi-controlled organizational identifier consisting
 * of a semi-controlled key, an arbitrary annotation value, and the resource tagged with
 * the annotation.
 * 
 * @author <a href='mailto:allen.lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class PResourceAnnotation extends AbstractPersistable  {


    public PResourceAnnotation() {
    }

    public PResourceAnnotation(PResourceAnnotationKey key, String value) {
        setResourceAnnotationKey(key);
        setValue(value);
    }

    @JsonView(JsonProjectLookupFilter.class)
    private PResourceAnnotationKey resourceAnnotationKey;
    @JsonView(JsonLookupFilter.class)
    private String value;

    public String getPairedValue() {
        return getResourceAnnotationKey().getKey() + ":" + getValue();
    }

    @XmlTransient
    public String getFormattedValue() {
        ResourceAnnotationDataType annotationDataType = resourceAnnotationKey.getAnnotationDataType();
        if (annotationDataType.isFormatString()) {
            // do format string stuff.
            return resourceAnnotationKey.format(value);
        }
        return value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public PResourceAnnotationKey getResourceAnnotationKey() {
        return resourceAnnotationKey;
    }

    public void setResourceAnnotationKey(PResourceAnnotationKey resourceAnnotationKey) {
        this.resourceAnnotationKey = resourceAnnotationKey;
    }

    @Override
    public String toString() {
        return String.format("[%s :: %s (%s)]", resourceAnnotationKey, value, hashCode());
    }

}
