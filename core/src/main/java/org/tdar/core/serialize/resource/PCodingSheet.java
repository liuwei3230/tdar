package org.tdar.core.serialize.resource;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonView;

/**
 * <p>
 * Represents a categorized set of CodingRules and may be bound to a specific ontology. DataTableColumns associated with a CodingSheet are automatically
 * translated.
 * 
 * CodingRules themselves can be mapped to a specific node in this CodingSheet's bound ontology.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 */
@XmlRootElement(name = "PcodingSheet")
public class PCodingSheet extends PInformationResource  {

    private PCategoryVariable categoryVariable;
    private Set<PCodingRule> codingRules = new LinkedHashSet<>();
    private Set<PDataTableColumn> associatedDataTableColumns = new HashSet<>();
    @JsonView(JsonLookupFilter.class)
    private POntology defaultOntology;

    private boolean generated;

    private transient Map<Long, PCodingRule> idMap = new HashMap<>();

    public PCodingSheet() {
        setResourceType(ResourceType.CODING_SHEET);
    }

    public PCodingSheet(Long id, String title, String description, Status status) {
        this();
        setId(id);
        setTitle(title);
        setDescription(description);
        setStatus(status);
    }

    @XmlElementWrapper(name = "codingRules")
    @XmlElement(name = "codingRule")
    public Set<PCodingRule> getCodingRules() {
        return codingRules;
    }

    public void setCodingRules(Set<PCodingRule> codingRules) {
        this.codingRules = codingRules;
    }

    public PCategoryVariable getCategoryVariable() {
        return categoryVariable;
    }

    public void setCategoryVariable(PCategoryVariable categoryVariable) {
        this.categoryVariable = categoryVariable;
    }

    @Transient
    public SortedSet<PCodingRule> getSortedCodingRules() {
        return new TreeSet<PCodingRule>(getCodingRules());
    }

    @Transient
    public SortedSet<PCodingRule> getSortedCodingRules(Comparator<PCodingRule> comparator) {
        TreeSet<PCodingRule> sortedCodingRules = new TreeSet<>(comparator);
        sortedCodingRules.addAll(getCodingRules());
        return sortedCodingRules;
    }

    @Transient
    public Map<String, PCodingRule> getCodeToRuleMap() {
        HashMap<String, PCodingRule> map = new HashMap<>();
        for (PCodingRule codingRule : getCodingRules()) {
            map.put(codingRule.getCode(), codingRule);
        }
        return map;
    }

    @Transient
    public Map<String, List<PCodingRule>> getTermToCodingRuleMap() {
        Map<String, List<PCodingRule>> map = new HashMap<>();
        for (PCodingRule codingRule : codingRules) {
            String term = codingRule.getTerm();
            List<PCodingRule> rules = map.get(term);
            if (rules == null) {
                rules = new ArrayList<>();
                map.put(term, rules);
            }
            rules.add(codingRule);
        }
        return map;
    }

    @Transient
    public Map<String, POntologyNode> getTermToOntologyNodeMap() {
        HashMap<String, POntologyNode> map = new HashMap<>();
        for (PCodingRule codingRule : getCodingRules()) {
            map.put(codingRule.getTerm(), codingRule.getOntologyNode());
        }
        return map;
    }

    public List<PCodingRule> getCodingRuleByTerm(String term) {
        List<PCodingRule> rules = new ArrayList<>();
        if (StringUtils.isNotEmpty(term)) {
            for (PCodingRule rule : getCodingRules()) {
                if (StringUtils.equals(term, rule.getTerm())) {
                    rules.add(rule);
                }
            }
        }
        return rules;
    }

    public PCodingRule getCodingRuleByCode(String code) {
        if (StringUtils.isNotEmpty(code)) {
            for (PCodingRule rule : getCodingRules()) {
                if (StringUtils.equals(code, rule.getCode())) {
                    return rule;
                }
            }
        }
        return null;
    }

    @Transient
    public Map<String, List<Long>> getTermToOntologyNodeIdMap() {
        HashMap<String, List<Long>> map = new HashMap<>();
        for (PCodingRule codingRule : codingRules) {
            POntologyNode node = codingRule.getOntologyNode();
            if (node != null) {
                String term = codingRule.getTerm();
                List<Long> ids = map.get(term);
                if (ids == null) {
                    ids = new ArrayList<>();
                    map.put(term, ids);
                }
                ids.add(node.getId());
            }
        }
        return map;
    }

    public Set<PDataTableColumn> getAssociatedDataTableColumns() {
        return associatedDataTableColumns;
    }

    public void setAssociatedDataTableColumns(Set<PDataTableColumn> associatedDataTableColumns) {
        this.associatedDataTableColumns = associatedDataTableColumns;
    }

    /**
     * @return the defaultOntology
     */
    public POntology getDefaultOntology() {
        return defaultOntology;
    }

    /**
     * @param defaultOntology
     *            the defaultOntology to set
     */
    public void setDefaultOntology(POntology defaultOntology) {
        this.defaultOntology = defaultOntology;
    }

    public PCodingRule getCodingRuleById(Long id) {
        if (idMap.isEmpty()) {
            for (PCodingRule node : getCodingRules()) {
                idMap.put(node.getId(), node);
            }
        }
        return idMap.get(id);
    }

    @Transient
    @XmlTransient
    public Map<POntologyNode, List<PCodingRule>> getNodeToDataValueMap() {
        HashMap<POntologyNode, List<PCodingRule>> map = new HashMap<>();
        for (PCodingRule rule : getCodingRules()) {
            POntologyNode node = rule.getOntologyNode();
            if (node != null) {
                List<PCodingRule> list = map.get(node);
                if (list == null) {
                    list = new ArrayList<>();
                    map.put(node, list);
                }
                list.add(rule);
            }
        }
        return map;
    }

    public List<PCodingRule> getMappedValues() {
        List<PCodingRule> mappedValues = new ArrayList<>();
        for (PCodingRule rule : getCodingRules()) {
            if (rule.getOntologyNode() != null) {
                mappedValues.add(rule);
            }
        }
        return mappedValues;
    }

    public List<PCodingRule> findRulesMappedToOntologyNode(POntologyNode node) {
        if ((node == null) || CollectionUtils.isEmpty(getCodingRules())) {
            return new ArrayList<>();
        }
        Map<POntologyNode, List<PCodingRule>> nodeToDataValueMap = getNodeToDataValueMap();
        return nodeToDataValueMap.get(node);
    }

    @Transient
    public boolean isMappedImproperly() {
        if (PersistableUtils.isNullOrTransient(getDefaultOntology()) || CollectionUtils.isEmpty(codingRules)) {
            return false;
        }

        int count = 0;
        for (PCodingRule rule : getCodingRules()) {
            if (rule != null && rule.getOntologyNode() != null) {
                count++;
            }
        }

        if (count == 0) {
            return true;
        }
        int size = CollectionUtils.size(getCodingRules());
        // if less than 25% then warn
        if (count < size / 4) {
            return true;
        }

        return false;
    }

    /**
     * Returns true if this coding sheet was system generated as a result of associating an ontology but no coding sheet
     * with a data table column.
     */
    public boolean isGenerated() {
        return generated;
    }

    public void setGenerated(boolean generated) {
        this.generated = generated;
    }

}
