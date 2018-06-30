package org.tdar.core.serialize.entity;

import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.Obfuscatable;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

/**
 * $Id$
 * 
 * Records the relevant information regarding an institution.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

@XmlRootElement(name = "Pinstitution")
public class PInstitution extends PCreator<PInstitution> implements Comparable<PInstitution> {


    private static final String ACRONYM_REGEX = "(?:.+)(?:[\\(\\[\\{])(.+)(?:[\\)\\]\\}])(?:.*)";

    private static final String[] IGNORE_PROPERTIES_FOR_UNIQUENESS = { "id", "dateCreated", "description", "dateUpdated", "url",
            "parentInstitution", "parentinstitution_id", "synonyms", "status", "occurrence", "browseOccurrence", "hidden" };
    private String name;

    @Override
    public int compareTo(PInstitution candidate) {
        return name.compareTo(candidate.name);
    }
    private PInstitution parentInstitution;
    private String email;

    public PInstitution() {
    }

    public PInstitution(String name) {
        this.name = name;
    }

    @Override
    @XmlElement
    public String getName() {
        return name;
    }

    @Override
    public String getProperName() {
        if (parentInstitution != null) {
            return parentInstitution.getName() + " : " + getName();
        }
        return getName();
    }

    public void setName(String name) {
        this.name = StringUtils.trimToEmpty(name);
    }

    public String getAcronym() {
        Pattern p = Pattern.compile(ACRONYM_REGEX);
        Matcher m = p.matcher(getName());
        if (m.matches()) {
            return m.group(1);
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @XmlElement(name = "parentRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PInstitution getParentInstitution() {
        return parentInstitution;
    }

    public void setParentInstitution(PInstitution parentInstitution) {
        this.parentInstitution = parentInstitution;
    }

    @Override
    public CreatorType getCreatorType() {
        return CreatorType.INSTITUTION;
    }

    public static String[] getIgnorePropertiesForUniqueness() {
        return IGNORE_PROPERTIES_FOR_UNIQUENESS;
    }

    @Override
    public Set<Obfuscatable> obfuscate() {
        return null;
    }

    @Override
    public boolean isValidForController() {
        return StringUtils.isNotBlank(name);
    }

    @Override
    public boolean isValid() {
        return isValidForController() && (getId() != null);
    }

    @Override
    public boolean hasNoPersistableValues() {
        if (StringUtils.isBlank(getName())) {
            return true;
        }
        return false;
    }

    @Override
    public Date getDateUpdated() {
        return super.getDateUpdated();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (StringUtils.isBlank(email)) {
            this.email = null;
        } else {
            this.email = email;
        }
    }

}
