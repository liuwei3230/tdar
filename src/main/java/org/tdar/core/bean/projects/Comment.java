package org.tdar.core.bean.projects;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.entity.TdarUser;

@Table(name = "pm_task_comments")
@Entity
public class Comment extends Persistable.Base {

    private static final long serialVersionUID = 8310540413911204530L;

    @Lob
    @Type(type = "org.hibernate.type.StringClobType")
    private String comment;

    @Column(name = "date_created", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH })
    private TdarUser commentor;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TdarUser getCommentor() {
        return commentor;
    }

    public void setCommentor(TdarUser commentor) {
        this.commentor = commentor;
    }

}
