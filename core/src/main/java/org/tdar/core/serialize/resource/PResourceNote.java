package org.tdar.core.serialize.resource;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;

import org.tdar.core.bean.AbstractSequenced;
import org.tdar.core.bean.resource.ResourceNoteType;
import org.tdar.utils.json.JsonProjectLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * <p>
 * ResourceNotes allow for free-text notes about a resource.
 * 
 * @author Adam Brin
 * @version $Revision$
 */

public class PResourceNote extends AbstractSequenced<PResourceNote>  {

    @JsonView(JsonProjectLookupFilter.class)
    private String note;
    @JsonView(JsonProjectLookupFilter.class)
    private ResourceNoteType type;

    @Override
    public java.util.List<?> getEqualityFields() {
        return Arrays.asList(type, note);
    };

    public PResourceNote() {
    }

    public PResourceNote(ResourceNoteType type, String note) {
        this.type = type;
        this.note = note;
    }

    public String getNote() {
        if (note == null) {
            return "";
        }
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @XmlAttribute
    public ResourceNoteType getType() {
        if (type == null) {
            return ResourceNoteType.GENERAL;
        }
        return type;
    }

    public void setType(ResourceNoteType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return getType().getLabel() + ":" + getNote();
    }


}
