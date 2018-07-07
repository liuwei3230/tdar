package org.tdar.core.serialize.billing;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.Updatable;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.serialize.entity.PTdarUser;

/**
 * Represents a group of Accounts. Each account may be associated with people who can charge. This "group of groups" allows for super-admins to manage lots of
 * accounts.
 * 
 * @author TDAR
 * @version $Rev$
 */
public class PBillingAccountGroup extends AbstractPersistable {

    private Set<PBillingAccount> accounts = new HashSet<>();
    private String name;
    private String description;
    private Status status = Status.ACTIVE;
    private Date dateCreated = new Date();
    private Date lastModified = new Date();
    private PTdarUser owner;
    private PTdarUser modifiedBy;
    private Set<PTdarUser> authorizedMembers = new HashSet<>();

    public Set<PBillingAccount> getAccounts() {
        return accounts;
    }

    public void setAccounts(Set<PBillingAccount> accounts) {
        this.accounts = accounts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public PTdarUser getOwner() {
        return owner;
    }

    public void setOwner(PTdarUser owner) {
        this.owner = owner;
    }

    public PTdarUser getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(PTdarUser modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Set<PTdarUser> getAuthorizedMembers() {
        return authorizedMembers;
    }

    public void setAuthorizedMembers(Set<PTdarUser> authorizedMembers) {
        this.authorizedMembers = authorizedMembers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDateUpdated() {
        return lastModified;
    }

}
