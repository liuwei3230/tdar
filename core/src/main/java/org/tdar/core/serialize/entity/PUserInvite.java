package org.tdar.core.serialize.entity;

import java.util.Date;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.entity.permissions.Permissions;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.resource.PResource;

/**
 * Bean for inviting a person to tDAR -- grants them implicit access to the collection(s)
 * 
 * @author abrin
 *
 */
public class PUserInvite extends AbstractPersistable {

    private Date dateCreated = new Date();
    private Date dateExpires;
    private Date dateRedeemed;
    private PResourceCollection resourceCollection;
    private PResource resource;
    private PTdarUser authorizer;

    private transient String note;

    private PPerson user;

    private Permissions permissions;

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public PResourceCollection getResourceCollection() {
        return resourceCollection;
    }

    public void setResourceCollection(PResourceCollection resourceCollection) {
        this.resourceCollection = resourceCollection;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public PTdarUser getAuthorizer() {
        return authorizer;
    }

    public void setAuthorizer(PTdarUser user) {
        this.authorizer = user;
    }

    public Date getDateRedeemed() {
        return dateRedeemed;
    }

    public void setDateRedeemed(Date dateRedeemed) {
        this.dateRedeemed = dateRedeemed;
    }

    public PPerson getUser() {
        return user;
    }

    public void setPerson(PPerson user) {
        this.user = user;
    }

    public Date getDateExpires() {
        return dateExpires;
    }

    public void setDateExpires(Date dateExpires) {
        this.dateExpires = dateExpires;
    }

    public PResource getResource() {
        return resource;
    }

    public void setResource(PResource resource) {
        this.resource = resource;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
