package org.tdar.struts.action.ontology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.resource.CodingSheet;
import org.tdar.core.bean.resource.Ontology;
import org.tdar.core.bean.resource.OntologyNode;
import org.tdar.core.exception.StatusCode;
import org.tdar.core.serialize.resource.PCodingSheet;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.POntology;
import org.tdar.core.serialize.resource.POntologyNode;
import org.tdar.core.service.resource.CodingSheetService;
import org.tdar.core.service.resource.OntologyNodeService;
import org.tdar.core.service.resource.OntologyService;
import org.tdar.struts.interceptor.annotation.HttpsOnly;
import org.tdar.struts_base.action.TdarActionException;

@Component
@Scope("prototype")
@ParentPackage("default")
@Namespace("/ontology_node")
@Results(value = {
        @Result(name = "redirect_iri", location = "${redirectIri}")
})
public class OntologyNodeViewAction extends AbstractOntologyViewAction {

    private static final long serialVersionUID = -172190399789767787L;
    @Autowired
    private transient OntologyNodeService ontologyNodeService;
    @Autowired
    private transient OntologyService ontologyService;
    @Autowired
    private transient CodingSheetService codingSheetService;

    private List<PCodingSheet> codingSheetsWithMappings = new ArrayList<>();
    private POntologyNode parentNode;
    private List<POntologyNode> children;
    private String iri;
    private List<PDataset> datasetsWithMappingsToNode;

    @HttpsOnly
    @Action(value = "{id}/node/{iri}",
            interceptorRefs = { @InterceptorRef("unauthenticatedStack") },
            results = {
                    @Result(name = SUCCESS, location = "../ontology/view-node.ftl")
            })
    public String node() throws Exception {
        return SUCCESS;
    }

    @Override
    public void prepare() throws Exception {
        super.prepare();
        setNode(getNodeBySlug());
        // getLogger().debug("{}", getNode());
        if (getNode() == null) {
            setNode(getNodeByIri());
        }
        // getLogger().debug("{}", getNode());
        if (getNode() == null) {
            abort(StatusCode.NOT_FOUND, getText("ontologyController.node_not_found", Arrays.asList(getIri())));
        }
        try {
            getCodingSheetsWithMappings().addAll(codingSheetService.findAllUsingOntology(getId()));
            setParentNode(ontologyNodeService.getParent(getNode()));

            setDatasetsWithMappingsToNode(ontologyNodeService.listDatasetsWithMappingsToNode(getNode()));
            setChildren(getChildElements(getNode()));
        } catch (Exception e) {
            getLogger().warn("{}", e, e);
        }
    }

    public List<POntologyNode> getChildren() {
        return children;
    }

    public void setChildren(List<POntologyNode> children) {
        this.children = children;
    }

    public POntologyNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(POntologyNode parentNode) {
        this.parentNode = parentNode;
    }

    public List<POntologyNode> getChildElements(POntologyNode node) {
        getLogger().trace("get children:" + node);
        return ontologyService.getChildren(getOntology().getOntologyNodes(), node);
    }

    public List<POntologyNode> getChildElements(String index) {
        getLogger().trace("get children: {}", index);
        for (POntologyNode node : getOntology().getOntologyNodes()) {
            if (node.getIndex().equals(index)) {
                return ontologyService.getChildren(getOntology().getOntologyNodes(), node);
            }
        }
        return null;
    }

    public String getIri() {
        return iri;
    }

    public void setIri(String iri) {
        this.iri = iri;
    }

    public List<PDataset> getDatasetsWithMappingsToNode() {
        return datasetsWithMappingsToNode;
    }

    public void setDatasetsWithMappingsToNode(List<PDataset> datasetsWithMappingsToNode) {
        this.datasetsWithMappingsToNode = datasetsWithMappingsToNode;
    }

    public POntology getOntology() {
        return getPersistable();
    }

    @Override
    protected void handleSlug() {
        String normalizeIri = OntologyNode.normalizeIri(getIri());
        getLogger().trace("iri:{} --> {}", getIri(), normalizeIri);
        POntologyNode node_ = getOntology().getNodeByIri(normalizeIri);
        getLogger().trace("node:{}", node_);
        if (node_ == null) {
            node_ = fallbackCheckForIri(normalizeIri);
        }

        if (node_ != null) {
            setRedirectIri(String.format("/ontology/%s/node/%s", getId(), normalizeIri));
        }
    }

    public List<PCodingSheet> getCodingSheetsWithMappings() {
        return codingSheetsWithMappings;
    }

    public void setCodingSheetsWithMappings(List<PCodingSheet> codingSheetsWithMappings) {
        this.codingSheetsWithMappings = codingSheetsWithMappings;
    }

    @Override
    public Class<POntology> getPersistableClass() {
        return POntology.class;
    }

}
