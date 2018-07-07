package org.tdar.core.service;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.serialize.entity.PCreator;
import org.tdar.core.serialize.entity.PInstitution;
import org.tdar.core.serialize.entity.PPerson;
import org.tdar.core.serialize.entity.PResourceCreator;

/**
 * $Id$
 * 
 * Utility class for easily passing resource creators between controller and
 * view
 * 
 * 
 * @author <a href='mailto:james.t.devos@asu.edu'>Jim deVos</a>
 * @version $Rev$
 */
public class PResourceCreatorProxy implements Comparable<PResourceCreatorProxy> {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    private Set<String> seenImportFieldNames = new HashSet<>();
    private PPerson person;
    private PInstitution institution;
    private PResourceCreator resourceCreator = new PResourceCreator();
    private ResourceCreatorRole role = ResourceCreatorRole.AUTHOR;

    public PResourceCreatorProxy() {
        // TODO: set any defaults here?
    }

    private CreatorType type = CreatorType.PERSON;
    private Long id;

    public PResourceCreatorProxy(PCreator<?> creator, ResourceCreatorRole role) {
        if (creator instanceof PPerson) {
            this.person = (PPerson) creator;
        } else {
            this.institution = (PInstitution) creator;
        }
        this.role = role;
    }

    public PResourceCreatorProxy(PResourceCreator rc) {
        this.resourceCreator = rc;
        if (rc.getCreator() instanceof PPerson) {
            this.person = (PPerson) rc.getCreator();
        } else {
            this.institution = (PInstitution) rc.getCreator();
        }
        this.role = rc.getRole();
        this.setId(rc.getId());
    }

    public PPerson getPerson() {
        return person;
    }

    public void setPerson(PPerson person) {
        this.person = person;
    }

    public PInstitution getInstitution() {
        return institution;
    }

    public void setInstitution(PInstitution institution) {
        this.institution = institution;
    }

    public PResourceCreator getResourceCreator() {
        return resourceCreator;
    }

    @Transient
    public CreatorType getActualCreatorType() {
        if (institution != null) {
            return CreatorType.INSTITUTION;
        }
        if (person == null) {
            return CreatorType.PERSON;
        }
        return null;
    }

    @Override
    public String toString() {
        String pstring = "null (-1)";
        if (person != null) {
            pstring = String.format("%s (%s)", person.getProperName(), person.getId());
        }
        String istring = "null (-1)";
        if (institution != null) {
            istring = String.format("%s (%s)", institution.getName(), institution.getId());
        }
        String rc = "null";
        if (resourceCreator != null) {
            rc = resourceCreator.toString();
        }
        return String.format("[RCP %s  role:%s rc:%s  p:%s  i:%s]",
                this.hashCode(), role, rc, pstring, istring);
    }

    @Override
    public int compareTo(PResourceCreatorProxy that) {
        return this.getResourceCreator().compareTo(that.getResourceCreator());
    }

    public ResourceCreatorRole getRole() {
        return role;
    }

    public void setRole(ResourceCreatorRole role) {
        this.role = role;
    }

    public CreatorType getType() {
        return type;
    }

    public void setType(CreatorType type) {
        this.type = type;
    }

    public Set<String> getSeenImportFieldNames() {
        return seenImportFieldNames;
    }

    public void setSeenImportFieldNames(Set<String> seenImportFieldNames) {
        this.seenImportFieldNames = seenImportFieldNames;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public boolean isValidEmailContact() {
        if (getRole() != ResourceCreatorRole.CONTACT) {
            return false;
        }

        if (getResourceCreator() != null && getResourceCreator().getCreator() != null) {
            return StringUtils.isNotBlank(getResourceCreator().getCreator().getEmail());
        }
        return false;

    }

}
