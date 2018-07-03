package org.tdar.core.serialize.integration;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.HasSubmitter;
import org.tdar.core.bean.Hideable;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.Updatable;
import org.tdar.core.bean.entity.AuthorizedUser;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Addressable;
import org.tdar.core.bean.resource.HasAuthorizedUsers;

public class PDataIntegrationWorkflow extends AbstractPersistable
        implements HasSubmitter, Addressable, HasAuthorizedUsers, Indexable, Hideable {

    private transient boolean viewable;

    public PDataIntegrationWorkflow() {
    }

    private String title;
    private String description;
    private boolean hidden = true;
    private String jsonData;
    private Date dateCreated = new Date();
    private Date dateUpdated = new Date();
    private int version = 1;
    private boolean editable;
    private TdarUser submitter;
    private Set<AuthorizedUser> authorizedUsers = new LinkedHashSet<AuthorizedUser>();

    public PDataIntegrationWorkflow(String string, boolean b, TdarUser adminUser) {
        this.title = string;
        this.hidden = b;
        this.submitter = adminUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public TdarUser getSubmitter() {
        return submitter;
    }

    public void setSubmitter(TdarUser user) {
        this.submitter = user;
    }

    public String toString() {
        return String.format("%s: %s (%s)\nJSON: \t%s", title, submitter, getDateCreated(), jsonData);
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String getUrlNamespace() {
        return "workspace/integrate";
    }

    // convenience for deletion
    public String getName() {
        return title;
    }

    @Override
    public String getDetailUrl() {
        return String.format("/%s/%s", getUrlNamespace(), getId());
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Set<AuthorizedUser> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(Set<AuthorizedUser> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isIntegration() {
        return true;
    }
}
