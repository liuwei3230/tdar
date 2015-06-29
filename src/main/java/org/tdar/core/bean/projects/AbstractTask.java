package org.tdar.core.bean.projects;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.entity.TdarUser;

@Table(name="pm_task")
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public abstract class AbstractTask extends Persistable.Base {

    private static final long serialVersionUID = 3844713738069514669L;

    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = FieldLength.FIELD_LENGTH_50)
    private TaskStatus status;
    
    @ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH })
    private TdarUser creator;

    @OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
    @JoinColumn(nullable = false, updatable = false, name = "task_id")
    private List<Comment> comments;

    @NotNull
    @Column(name = "date_created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    @Column(name = "date_completed")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCompleted;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(nullable = false, updatable = false, name = "task_id")
    private List<ResourceStatusWrapper> resources;
    
    public List<ResourceStatusWrapper> getResources() {
        return resources;
    }

    public void setResources(List<ResourceStatusWrapper> resources) {
        this.resources = resources;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TdarUser getCreator() {
        return creator;
    }

    public void setCreator(TdarUser creator) {
        this.creator = creator;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(Date dateCompleted) {
        this.dateCompleted = dateCompleted;
    }
    
    
    
    
}
