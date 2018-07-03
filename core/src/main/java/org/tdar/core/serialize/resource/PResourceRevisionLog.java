package org.tdar.core.serialize.resource;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.RevisionLogType;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

/**
 * Tracks administrative changes. When the UI changes a resource, a log entry should be added.
 * 
 * @author <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */

public class PResourceRevisionLog extends AbstractPersistable {

    public PResourceRevisionLog() {
    }

    public PResourceRevisionLog(String message, PResource resource, TdarUser person, RevisionLogType type) {
        this.person = person;
        this.timestamp = new Date();
        this.resource = resource;
        this.logMessage = message;
        this.type = type;
    }

    private PResource resource;
    private Date timestamp;
    private RevisionLogType type;
    private String logMessage;
    private TdarUser person;
    private String payload;
    private Long timeInSeconds;
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @XmlAttribute(name = "resourceRef")
    public PResource getResource() {
        return resource;
    }

    public void setResource(PResource resource) {
        this.resource = resource;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date dateRevised) {
        this.timestamp = dateRevised;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(String action) {
        this.logMessage = action;
    }

    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @XmlAttribute(name = "personRef")
    public TdarUser getPerson() {
        return person;
    }

    public void setPerson(TdarUser actor) {
        this.person = actor;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public RevisionLogType getType() {
        return type;
    }

    public void setType(RevisionLogType type) {
        this.type = type;
    }

    public Long getTimeInSeconds() {
        return timeInSeconds;
    }

    public void setTimeInSeconds(Long timeInSeconds) {
        this.timeInSeconds = timeInSeconds;
    }

    public void setTimeBasedOnStart(Long startTime) {
        if (PersistableUtils.isNotNullOrTransient(startTime)) {
            long milli = System.currentTimeMillis() - startTime.longValue();
            setTimeInSeconds(milli / 1000);
        }
    }

}
