package org.tdar.core.serialize.entity;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractSequenced;
import org.tdar.core.bean.HasResource;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.exception.TdarRecoverableRuntimeException;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * This is the class to build the relationships between creators and resources. These relationships include a role, which may depend
 * on the resource type and creator type.
 * 
 * @author <a href='mailto:allen.lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
public class PResourceCreator extends AbstractSequenced<PResourceCreator> implements HasResource<Resource> {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    @JsonView(JsonLookupFilter.class)
    private PCreator creator;
    @JsonView(JsonLookupFilter.class)
    private ResourceCreatorRole role;

    public PResourceCreator(PCreator creator, ResourceCreatorRole role) {
        setCreator(creator);
        setRole(role);
    }

    public PResourceCreator() {
    }

    @XmlElementRef
    public PCreator getCreator() {
        return creator;
    }

    public void setCreator(PCreator creator) {
        this.creator = creator;
    }

    @XmlAttribute
    public ResourceCreatorRole getRole() {
        return role;
    }

    public void setRole(ResourceCreatorRole role) {
        this.role = role;
    }

    @Transient
    public CreatorType getCreatorType() {
        if (getCreator() != null) {
            return getCreator().getCreatorType();
        }
        return null;
    }

    @Override
    public String toString() {
        String properName = "";
        Long id = -1L;
        if (creator != null) {
            id = creator.getId();
            properName = creator.getProperName();
        }
        return String.format("%s[%s] (%s)", properName, id, role);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.core.bean.Validatable#isValid()
     */
    @Override
    public boolean isValid() {
        if ((role == null) || (creator == null)) {
            logger.trace(String.format("role:%s creator:%s ", role, creator));
            return false;
        }
        return true;
    }


    @Override
    public boolean isValidForController() {
        return true;
    }

    @Transient
    @JsonView(JsonLookupFilter.class)
    public final String getCreatorRoleIdentifier() {
        return getCreatorRoleIdentifier(this.getCreator(), this.getRole());
    }

    @Transient
    public static final String getCreatorRoleIdentifier(PCreator creatorToFormat, ResourceCreatorRole creatorRole) {
        String toReturn = "";
        if ((creatorToFormat != null) && (creatorToFormat.getCreatorType() != null)) {
            String code = creatorToFormat.getCreatorType().getCode();
            String role = "";
            if (creatorRole != null) {
                role = creatorRole.name();
            }
            if (PersistableUtils.isNullOrTransient(creatorToFormat)) {
                throw new TdarRecoverableRuntimeException("resourceCreator.undefined_creator_id");
            }
            toReturn = String.format("%s_%s_%s", code, creatorToFormat.getId(), role).toLowerCase();
        }
        return toReturn;
    }

}
