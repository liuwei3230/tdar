package org.tdar.struts.action.ontology;

import java.util.List;
import java.util.Objects;

import org.tdar.core.bean.resource.Ontology;
import org.tdar.core.bean.resource.OntologyNode;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.POntology;
import org.tdar.core.serialize.resource.POntologyNode;
import org.tdar.struts.action.resource.AbstractSupportingResourceViewAction;

public abstract class AbstractOntologyViewAction extends AbstractSupportingResourceViewAction<POntology> {

    private static final long serialVersionUID = -7901012726097964225L;
    private POntologyNode node;
    private String iri;
    private String redirectIri;
    private List<PDataset> datasetsWithMappingsToNode;

    protected POntologyNode getNodeByIri() {
        String iri_ = getIri();
        getLogger().trace("id: {} iri: {} slug: {}", getId(), iri_, getSlug());
        POntologyNode node_ = getOntology().getNodeByIri(OntologyNode.normalizeIri(iri_));
        if (node_ == null) {
            node_ = fallbackCheckForIri(iri_);
        }
        getLogger().trace("iri: {} node: {}", getIri(), node_);
        return node_;
    }

    protected POntologyNode getNodeBySlug() {
        String iri_ = getIri();
        getLogger().trace("id: {} iri: {} slug: {}", getId(), iri_, getSlug());
        POntologyNode node_ = getOntology().getNodeBySlug(iri_);
        return node_;
    }

    public POntologyNode getNode() {
        return node;
    }

    public void setNode(POntologyNode node) {
        this.node = node;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public POntology getOntology() {
        return getPersistable();
    }

    /**
     * Checks for IRI, also removes parenthesis which may be removed by struts
     * 
     * @param normalizeIri
     * @param node_
     * @return
     */
    protected POntologyNode fallbackCheckForIri(String normalizeIri) {
        getLogger().trace("normalizedIri:{}", normalizeIri);
        for (POntologyNode node_ : getOntology().getOntologyNodes()) {
            String iri_ = node_.getNormalizedIri().replaceAll("[\\(\\)\\\\.']", "");
            getLogger().trace("|{}|<--{}-->|{}|", iri_, Objects.equals(iri_, normalizeIri), normalizeIri);
            if (Objects.equals(normalizeIri, iri_)) {
                return node_;
            }
        }
        return null;
    }

    public List<PDataset> getDatasetsWithMappingsToNode() {
        return datasetsWithMappingsToNode;
    }

    public void setDatasetsWithMappingsToNode(List<PDataset> datasetsWithMappingsToNode) {
        this.datasetsWithMappingsToNode = datasetsWithMappingsToNode;
    }

    public String getRedirectIri() {
        return redirectIri;
    }

    public void setRedirectIri(String redirectIri) {
        this.redirectIri = redirectIri;
    }

}
