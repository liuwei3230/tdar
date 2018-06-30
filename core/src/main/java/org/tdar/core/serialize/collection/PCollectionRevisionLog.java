package org.tdar.core.serialize.collection;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.tdar.core.bean.AbstractPersistable;
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

public class PCollectionRevisionLog extends AbstractPersistable {

    public PCollectionRevisionLog() {
    }

    public PCollectionRevisionLog(String message, PResourceCollection resource, TdarUser person, RevisionLogType type) {
        this.person = person;
        this.timestamp = new Date();
        this.resourceCollection = resource;
        this.logMessage = message;
        this.type = type;
    }

    private PResourceCollection resourceCollection;
    private Date timestamp;
    private RevisionLogType type;
    private String logMessage;
    private TdarUser person;
    private Long timeInSeconds;

    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @XmlAttribute(name = "resourceRef")
    public PResourceCollection getResourceCollection() {
        return resourceCollection;
    }

    public void setResourceCollection(PResourceCollection resource) {
        this.resourceCollection = resource;
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
