package org.tdar.core.serialize.keyword;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.HasStatus;
import org.tdar.core.bean.Slugable;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.bean.util.UrlUtils;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

@XmlType(name = "Pkwdbase")
@XmlTransient
public abstract class AbstractKeyword<T extends PKeyword> extends AbstractPersistable implements PKeyword, HasStatus, Comparable<T>, Slugable {

    @JsonView(JsonLookupFilter.class)
    private String label;
    @JsonView(JsonLookupFilter.class)
    private Set<PExternalKeywordMapping> assertions = new HashSet<>();
    private Set<T> synonyms = new HashSet<T>();
    private String definition;
    private Status status = Status.ACTIVE;

    @Override
    public String getKeywordType() {
        return getClass().getSimpleName();
    }

    private Long occurrence = 0L;

    @Override
    public String getSlug() {
        return UrlUtils.slugify(getLabel());
    }

    @Override
    public int compareTo(T o) {
        return this.getLabel().compareTo(o.getLabel());
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = StringUtils.trimToEmpty(label);
    }

    @XmlTransient
    @Override
    public String getDefinition() {
        return definition;
    }

    @Override
    public void setDefinition(String definition) {
        this.definition = StringUtils.trimToEmpty(definition);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean isDedupable() {
        return true;
    }

    @XmlAttribute
    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonView(JsonLookupFilter.class)
    public Long getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(Long occurrence) {
        this.occurrence = occurrence;
    }

    @JsonView(JsonLookupFilter.class)
    public String getDetailUrl() {
        return String.format("/%s/%s/%s", getUrlNamespace(), getId(), getSlug());
    }

    @Override
    public Set<T> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<T> synonyms) {
        this.synonyms = synonyms;
    }

    @XmlElementWrapper(name = "assertions")
    @XmlElement(name = "assertion")
    public Set<PExternalKeywordMapping> getAssertions() {
        return assertions;
    }

    public void setAssertions(Set<PExternalKeywordMapping> externalMappings) {
        this.assertions = externalMappings;
    }

    public String getSynonymFormattedName() {
        return getLabel();
    }

    @Override
    @XmlTransient
    public boolean isDeleted() {
        return status == Status.DELETED;
    }

    @Override
    @XmlTransient
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    @Override
    @XmlTransient
    public boolean isDraft() {
        return status == Status.DRAFT;
    }

    @Override
    public boolean isDuplicate() {
        return status == Status.DUPLICATE;
    }

    @Override
    @XmlTransient
    public boolean isFlagged() {
        return status == Status.FLAGGED;
    }

}
