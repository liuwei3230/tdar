package org.tdar.core.serialize.citation;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * Mapped superclass to reduce redundancy of RelatedComparativeCollection and SourceCollection metadata
 * (which are all just special cases of String text associated with a Resource).
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlRootElement
@XmlType(name = "citation")
@XmlTransient
public abstract class Citation extends AbstractPersistable {
    private final static String[] JSON_PROPERTIES = { "id", "text" };
    @JsonView(JsonLookupFilter.class)
    private String text;

    @Override
    public java.util.List<?> getEqualityFields() {
        return Arrays.asList(text);
    };

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
