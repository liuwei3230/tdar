package org.tdar.core.bean.projects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.resource.Resource;

@Entity
@Table(name="pm_task_resource")
public class ResourceStatusWrapper extends Persistable.Base {

    private static final long serialVersionUID = 3141330742501670989L;

    @ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH })
    private Resource resource;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = FieldLength.FIELD_LENGTH_50)
    private TaskStatus status;

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    
}
