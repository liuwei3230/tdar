/**
 * $Id$
 * 
 * @author $Author$
 * @version $Revision$
 */
package org.tdar.core.serialize.entity;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.entity.permissions.Permissions;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

public class PAuthorizedUser extends AbstractPersistable {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private Permissions generalPermission;
    private Integer effectiveGeneralPermission;
    private PTdarUser user;
    private Date dateCreated = new Date();
    private Date dateExpires;
    private PTdarUser createdBy;
    private Long collectionId;
    private Long accountId;
    private Long integrationId;
    private Long resourceId;

    private transient boolean enabled = false;

    /**
     * @param person
     * @param modifyRecord
     */
    public PAuthorizedUser() {
    }

    public PAuthorizedUser(PTdarUser createdBy, PTdarUser person, Permissions permission) {
        this.createdBy = createdBy;
        this.user = person;
        setGeneralPermission(permission);
    }

    public PAuthorizedUser(PTdarUser authenticatedUser, PTdarUser person, Permissions permission, Date date) {
        this(authenticatedUser, person, permission);
        if (date != null) {
            setDateExpires(date);
        }
    }

    @XmlElement(name = "personRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PTdarUser getUser() {
        return user;
    }

    public void setUser(PTdarUser user) {
        this.user = user;
    }

    /**
     * @param generalPermission
     *            the generalPermission to set
     */
    public void setGeneralPermission(Permissions generalPermission) {
        this.generalPermission = generalPermission;
        this.setEffectiveGeneralPermission(generalPermission.getEffectivePermissions());
    }

    /**
     * @return the generalPermission
     */
    public Permissions getGeneralPermission() {
        return generalPermission;
    }

    @Transient
    // is the authorizedUser valid not taking into account whether a collection is present
    public boolean isValid() {
        boolean registered = false;
        String name = "";
        if (user != null) {
            registered = user.isRegistered();
            name = user.toString();
        }
        logger.trace("calling validate collection for user/permission/registered: [{} / {} / {}]", name, generalPermission != null, registered);
        return (user != null) && (generalPermission != null) && user.isRegistered();
    }

    @Override
    public String toString() {
        Long userid = null;
        String properName = null;
        if (user != null) {
            userid = user.getId();
            properName = user.getProperName();
        }
        String ex = "";
        if (getDateExpires() != null) {
            ex = dateExpires.toString();
        }
        return String.format("%s[%s] (%s - %s %s)", properName, userid, generalPermission, getId(), ex);
    }

    /**
     * @param effectiveGeneralPermission
     *            the effectiveGeneralPermission to set
     */
    // I should only be called internally
    private void setEffectiveGeneralPermission(Integer effectiveGeneralPermission) {
        this.effectiveGeneralPermission = effectiveGeneralPermission;
    }

    /**
     * @return the effectiveGeneralPermission
     */
    public Integer getEffectiveGeneralPermission() {
        return effectiveGeneralPermission;
    }

    /**
     * 'Enabled' in this context refers to whether the system should allow modification of this object in the context UI edit operation. When enabled is false,
     * the system should not allow operations which would alter the fields in this object, and also should not allow operations that would add or remove the
     * object to/from an authorized user list.
     *
     * @return
     */
    public boolean isEnabled() {
        if (PersistableUtils.isNullOrTransient(this) && PersistableUtils.isNullOrTransient(user)) {
            return true;
        }
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(Date dateExpires) {
        this.dateExpires = dateExpires;
    }

    @XmlElement(name = "createdByRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PTdarUser getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(PTdarUser createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

}