package org.tdar.core.serialize.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.resource.HasStatic;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;

/**
 * Represents an entry in a CodingSheet consisting of a String code (key),
 * the String term (value) that the code is mapped to, and an optional description.
 * 
 * CodingRules can also be mapped to an OntologyNode in an Ontology, which is essential for the way we
 * currently perform data integration, where different datasets can be compared through their mappings
 * to nodes in a common ontology.
 */
public class PCodingRule extends AbstractPersistable implements Comparable<PCodingRule>, HasStatic {

    @Override
    @XmlTransient
    @Transient
    public boolean isStatic() {
        return false;
    }
    private String code;
    private String term;
    private String description;
    private POntologyNode ontologyNode;

    private transient long count = -1L;

    private transient List<Long> mappedToData = new ArrayList<>();

    private transient List<POntologyNode> suggestions = new ArrayList<>();

    public PCodingRule() {
    }

    public PCodingRule(PCodingSheet codingSheet, String value) {
        this(codingSheet, value, value, "", null);
    }

    public PCodingRule(PCodingSheet codingSheet, String code, String term, String description) {
        this(codingSheet, code, term, description, null);
    }

    public PCodingRule(PCodingSheet codingSheet, String code, String term, String description, POntologyNode node) {
        setCode(code);
        setTerm(term);
        setDescription(description);
        setOntologyNode(node);
        // FIXME: must be careful when adding "this" to collections inside a constructor to avoid NPEs from uninitialized instance variables.
        codingSheet.getCodingRules().add(this);
    }

    public PCodingRule(String unmappedValue, Long count) {
        setTerm(term);
        setCount(count);
        setOntologyNode(null);
    }

    public String getCode() {
        return code;
    }

    @Override
    public List<?> getEqualityFields() {
        // ab probably okay as not nullable fields
        return Arrays.asList(getCode());
    }

    public void setCode(String code) {
        this.code = sanitize(code);
    }

    // strips leading zeros and trims whitespace from string.
    private static String sanitize(String string_) {
        String string = string_;
        if (StringUtils.isEmpty(string)) {
            return null;
        }
        if (string != null) {
            string = string.trim();
        }
        return string;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimToNull(description);
    }

    @Override
    public String toString() {
        return String.format("{%s, %s, %s, %s}", code, term, description, getOntologyNode());
    }

    /**
     * Default implementation of compareTo using the code.
     */
    @Override
    public int compareTo(PCodingRule other) {
        try {
            // first try integer comparison instead of String lexicographic comparison
            return Integer.valueOf(code).compareTo(Integer.valueOf(other.code));
        } catch (NumberFormatException exception) {
            return code.compareTo(other.code);
        }
    }

    public POntologyNode getOntologyNode() {
        return ontologyNode;
    }

    public void setOntologyNode(POntologyNode ontologyNode) {
        this.ontologyNode = ontologyNode;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setSuggestions(List<POntologyNode> suggestions) {
        this.suggestions = suggestions;
    }

    @XmlTransient
    public List<POntologyNode> getSuggestions() {
        return suggestions;
    }

    public boolean isMappedToData(PDataTableColumn col) {
        return mappedToData.contains(col.getId());
    }

    public void setMappedToData(PDataTableColumn col) {
        mappedToData.add(col.getId());
    }

    @XmlTransient
    public String getFormattedTerm() {
        if (StringUtils.equalsIgnoreCase(getCode(), getTerm())) {
            return getTerm();
        }

        return String.format("%s (%s)", getTerm(), getCode());
    }
}
