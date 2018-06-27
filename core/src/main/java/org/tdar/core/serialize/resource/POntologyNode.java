package org.tdar.core.serialize.resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.Slugable;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;
import org.tdar.core.bean.util.UrlUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;
import org.tdar.utils.json.JsonIdNameFilter;
import org.tdar.utils.json.JsonIntegrationDetailsFilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

public class POntologyNode extends AbstractPersistable implements Comparable<POntologyNode>, Slugable {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private POntology ontology;

    private Integer intervalStart;
    private Integer intervalEnd;
    private String displayName;
    private String description;
    private Set<String> synonyms;

    private String index;

    private String iri;

    // @Column(unique=true)
    private String uri;
    private Long importOrder;

    // is this ontology node a synonym of another ontology node?
    private transient boolean synonym;
    // true if this ontology node or its children doesn't have any mapped data
    private transient boolean mappedDataValues;
    private transient boolean parent;
    private transient Map<PDataTableColumn, Boolean> columnHasValueMap = new HashMap<>();

    private transient POntologyNode parentNode;
    private transient Set<POntologyNode> synonymNodes = new HashSet<>();

    public POntologyNode() {
    }

    public POntologyNode(String iri, String label) {
        this.iri = iri;
        this.displayName = label;
    }

    public POntologyNode(Long id) {
        setId(id);
    }

    @XmlElement(name = "ontologyRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public POntology getOntology() {
        return ontology;
    }

    public void setOntology(POntology ontology) {
        this.ontology = ontology;
    }

    public Integer getIntervalStart() {
        return intervalStart;
    }

    public void setIntervalStart(Integer start) {
        this.intervalStart = start;
    }

    public Integer getIntervalEnd() {
        return intervalEnd;
    }

    public void setIntervalEnd(Integer end) {
        this.intervalEnd = end;
    }

    @JsonView({ JsonIntegrationDetailsFilter.class, JsonIdNameFilter.class })
    public String getIri() {
        return iri;
    }

    public void setIri(String label) {
        this.iri = label;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return String.format("id:%-8s label:%-25s url:%s ", getId(), getDisplayName(), iri);
    }

    @Override
    public List<?> getEqualityFields() {
        return Arrays.asList(iri);
    }

    @Transient
    public int getNumberOfParents() {
        if (StringUtils.isEmpty(index)) {
            return 0;
        }
        return StringUtils.split(index, '.').length;
    }

    @Override
    public int compareTo(POntologyNode other) {
        return ObjectUtils.compare(index, other.getIndex());
    }

    @Transient
    public String getIndentedLabel() {
        StringBuilder builder = new StringBuilder(index).append(' ').append(iri);
        return builder.toString();
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImportOrder(Long importOrder) {
        this.importOrder = importOrder;
    }

    public Long getImportOrder() {
        return importOrder;
    }

    public Set<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<String> synonyms) {
        this.synonyms = synonyms;
    }

    public Set<String> getEquivalenceSet() {
        HashSet<String> equivalenceSet = new HashSet<String>();
        for (String synonym : getSynonyms()) {
            equivalenceSet.add(synonym.toLowerCase());
        }
        equivalenceSet.add(displayName.toLowerCase());
        equivalenceSet.add(StringUtils.lowerCase(getNormalizedIri()));
        return equivalenceSet;
    }

    public String getNormalizedIri() {
        return POntologyNode.normalizeIri(iri);
    }

    public static String normalizeIri(String iriInput) {
        String iri_ = StringUtils.trim(iriInput);
        iri_ = StringUtils.replace(iri_, ".", "_");
        // backwards compatibility to help with mappings which start with digests
        if ((iri_ != null) && iri_.matches("^\\_\\d.*")) {
            return StringUtils.substring(iri_, 1);
        } else {
            return iri_;
        }
    }

    @Transient
    public boolean isEquivalentTo(POntologyNode existing) {
        // easy cases
        if (existing == null) {
            return false;
        }
        if (equals(existing)) {
            return true;
        }

        logger.trace("testing synonyms");
        for (String displayName_ : getEquivalenceSet()) {
            for (String existingDisplayName : existing.getEquivalenceSet()) {
                if (existingDisplayName.equalsIgnoreCase(displayName_)) {
                    logger.trace("\tcomparing {} <> {}", displayName, existingDisplayName);
                    return true;
                }
            }
        }
        return false;
    }

    @XmlTransient
    public POntologyNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(POntologyNode parentNode) {
        this.parentNode = parentNode;
    }

    @XmlTransient
    public Set<POntologyNode> getSynonymNodes() {
        return synonymNodes;
    }

    public void setSynonymNodes(Set<POntologyNode> synonymNodes) {
        this.synonymNodes = synonymNodes;
    }

    @Transient
    public boolean isChildOf(POntologyNode parentNode) {
        return (parentNode != null) && (parentNode.getIntervalStart() < getIntervalStart()) && (parentNode.getIntervalEnd() >= getIntervalEnd());
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    @XmlTransient
    public boolean isSynonym() {
        return synonym;
    }

    public void setSynonym(boolean synonym) {
        this.synonym = synonym;
    }

    public String getFormattedNameWithSynonyms() {
        if (CollectionUtils.isNotEmpty(getSynonyms())) {
            String txt = String.format("%s (%s)", getDisplayName(), StringUtils.join(getSynonyms(), ", "));
            return txt;
        } else {
            return getDisplayName();
        }
    }

    @Transient
    public boolean isDisabled() {
        return !mappedDataValues;
    }

    @Transient
    public boolean isMappedDataValues() {
        return mappedDataValues;
    }

    public void setMappedDataValues(boolean mappedDataValues) {
        this.mappedDataValues = mappedDataValues;
    }

    public Map<PDataTableColumn, Boolean> getColumnHasValueMap() {
        return columnHasValueMap;
    }

    public void setColumnHasValueMap(Map<PDataTableColumn, Boolean> columnHasValueMap) {
        this.columnHasValueMap = columnHasValueMap;
    }

    @Override
    @XmlTransient
    @Transient
    @JsonIgnore
    public String getSlug() {
        return UrlUtils.slugify(getIri());
    }

}
