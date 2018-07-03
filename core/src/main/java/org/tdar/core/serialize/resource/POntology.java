package org.tdar.core.serialize.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.utils.json.JsonIntegrationDetailsFilter;
import org.tdar.utils.json.JsonIntegrationFilter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * $Id$
 * 
 * OntologyS in tDAR are InformationResources with a collection of OntologyNodeS that can be categorized.
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision $
 */
@XmlRootElement(name = "Pontology")
public class POntology extends PInformationResource  {

    private PCategoryVariable categoryVariable;
    private List<POntologyNode> ontologyNodes = new ArrayList<>();

    private transient Map<Long, POntologyNode> idMap = new WeakHashMap<>();
    private transient Map<String, POntologyNode> iriMap = new WeakHashMap<>();
    private transient Map<String, POntologyNode> slugMap = new WeakHashMap<>();
    private transient Map<String, POntologyNode> nameMap = new WeakHashMap<>();

    public final static Comparator<POntologyNode> IMPORT_ORDER_COMPARATOR = new Comparator<POntologyNode>() {
        @Override
        public int compare(POntologyNode o1, POntologyNode o2) {
            int comparison = o1.getImportOrder().compareTo(o2.getImportOrder());
            if (comparison == 0) {
                // use default comparison by index
                return o1.compareTo(o2);
            }
            return comparison;
        }
    };

    public POntology() {
        setResourceType(ResourceType.ONTOLOGY);
    }

    @JsonView(JsonIntegrationFilter.class)
    public PCategoryVariable getCategoryVariable() {
        return categoryVariable;
    }

    public void setCategoryVariable(PCategoryVariable categoryVariable) {
        this.categoryVariable = categoryVariable;
    }

    public Map<Long, POntologyNode> getIdToNodeMap() {
        HashMap<Long, POntologyNode> idToNodeMap = new HashMap<>();
        for (POntologyNode node : ontologyNodes) {
            idToNodeMap.put(node.getId(), node);
        }
        return idToNodeMap;
    }

    /**
     * Returns a list of internal IDs (not database IDs) mapped to list of child ontology nodes.
     * 
     * @return
     */
    public SortedMap<Integer, List<POntologyNode>> toOntologyNodeMap() {
        List<POntologyNode> sortedOntologyNodes = getSortedOntologyNodes();
        TreeMap<Integer, List<POntologyNode>> map = new TreeMap<>();
        for (POntologyNode node : sortedOntologyNodes) {
            Integer intervalStart = node.getIntervalStart();
            String index = node.getIndex();
            for (String indexId : StringUtils.split(index, '.')) {
                Integer parentId = Integer.valueOf(indexId);
                // don't include this node if the parent id is the same as this node's interval start
                if (parentId.equals(intervalStart)) {
                    continue;
                }
                List<POntologyNode> children = map.get(parentId);
                if (children == null) {
                    children = new ArrayList<>();
                    map.put(parentId, children);
                }
                children.add(node);
            }
        }
        return map;
    }

    @Transient
    public POntologyNode getNodeByName(String name) {
        if (MapUtils.isEmpty(nameMap)) {
            initializeNameAndIriMaps();
        }
        return nameMap.get(name);
    }

    @Transient
    public POntologyNode getNodeByIri(String iri) {
        if (MapUtils.isEmpty(iriMap)) {
            initializeNameAndIriMaps();
        }
        return iriMap.get(iri);
    }

    @Transient
    public POntologyNode getNodeBySlug(String slug) {
        if (MapUtils.isEmpty(slugMap)) {
            initializeNameAndIriMaps();
        }
        return slugMap.get(slug);
    }

    private void initializeNameAndIriMaps() {
        for (POntologyNode node : getOntologyNodes()) {
            nameMap.put(node.getDisplayName(), node);
            iriMap.put(node.getNormalizedIri(), node);
            slugMap.put(node.getSlug(), node);
        }
    }

    public void clearTransientMaps() {
        nameMap.clear();
        iriMap.clear();
        idMap.clear();
        slugMap.clear();
    }

    @Transient
    public POntologyNode getNodeByNameIgnoreCase(String name) {
        for (POntologyNode node : getOntologyNodes()) {
            if (StringUtils.equalsIgnoreCase(node.getDisplayName(), name)) {
                return node;
            }
        }
        return null;
    }

    public List<POntologyNode> getSortedOntologyNodes() {
        // return ontology nodes by natural order.
        return getSortedOntologyNodes(null);
    }

    @JsonView(JsonIntegrationDetailsFilter.class)
    @JsonProperty(value = "nodes")
    public List<POntologyNode> getSortedOntologyNodesByImportOrder() {
        return getSortedOntologyNodes(IMPORT_ORDER_COMPARATOR);
    }

    public List<POntologyNode> getSortedOntologyNodes(Comparator<POntologyNode> comparator) {
        ArrayList<POntologyNode> sortedNodes = new ArrayList<>(getOntologyNodes());
        Collections.sort(sortedNodes, comparator);
        return sortedNodes;
    }

    @XmlElementWrapper(name = "ontologyNodes")
    @XmlElement(name = "ontologyNode")
    public List<POntologyNode> getOntologyNodes() {
        return ontologyNodes;
    }

    public void setOntologyNodes(List<POntologyNode> ontologyNodes) {
        this.ontologyNodes = ontologyNodes;
    }

    public POntologyNode getOntologyNodeById(Long id) {
        if (idMap.isEmpty()) {
            for (POntologyNode node : getOntologyNodes()) {
                idMap.put(node.getId(), node);
            }
        }
        return idMap.get(id);
    }

}
