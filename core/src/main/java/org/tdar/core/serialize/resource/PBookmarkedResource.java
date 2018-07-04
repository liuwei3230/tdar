package org.tdar.core.serialize.resource;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

/**
 * <p>
 * A persistable pointer to a resource that has been "bookmarked" by a user. Bookmarked resources serve two purposes:
 * <ul>
 * <li>Bookmarks facilitate a rudimentary, user-specific organizational tool for users.
 * <li>Bookmarked datasets serve as a the "pool" from which a user may choose to include in a dataset integration task.
 * </ul>
 * </p>
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

public class PBookmarkedResource extends AbstractPersistable {

    private TdarUser person;
    private PResource resource;
    private String name;
    private Date timestamp;

    public TdarUser getPerson() {
        return person;
    }

    public void setPerson(TdarUser person) {
        this.person = person;
    }

    @XmlAttribute(name = "resourceRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PResource getResource() {
        return resource;
    }

    public void setResource(PResource resource) {
        this.resource = resource;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        if (StringUtils.isEmpty(name)) {
            return String.format("(%d, %s, %s)", getId(), getPerson(), getResource());
        } else {
            return name;
        }
    }

    @Override
    public List<?> getEqualityFields() {
        // ab probably okay as not nullable fields
        return Arrays.asList(person, resource);
    }

}
