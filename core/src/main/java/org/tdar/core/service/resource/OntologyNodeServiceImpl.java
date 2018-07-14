package org.tdar.core.service.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdar.core.bean.resource.Dataset;
import org.tdar.core.bean.resource.OntologyNode;
import org.tdar.core.dao.resource.OntologyNodeDao;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.POntologyNode;
import org.tdar.core.service.Context;
import org.tdar.core.service.ProxyConstructionService;
import org.tdar.core.service.ServiceInterface;
import org.tdar.utils.PersistableUtils;

/**
 * Transactional service providing persistence access to OntologyNodeS.
 * 
 * @author Allen Lee
 * @version $Revision$
 * @latest $Id$
 */
@Service
@Transactional
public class OntologyNodeServiceImpl extends ServiceInterface.TypedDaoBase<OntologyNode, OntologyNodeDao> implements OntologyNodeService {

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.core.service.resource.OntologyNodeService#getAllChildren(org.tdar.core.bean.resource.OntologyNode)
     */
    @Override
    public List<POntologyNode> getAllChildren(POntologyNode ontologyNode) {
        OntologyNode find = getDao().find(ontologyNode.getId());
        List<POntologyNode> toReturn = new ArrayList<>();
        for (OntologyNode node : getDao().getAllChildren(find)) {
            toReturn.add(proxyConstructionService.convertOntologyNode(node));
        }
        return toReturn;
    }

    public List<OntologyNode> getAllChildren(OntologyNode ontologyNode) {
        return getDao().getAllChildren(ontologyNode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.core.service.resource.OntologyNodeService#getHierarchyMap(java.util.List)
     */
    @Override
    public Map<OntologyNode, List<OntologyNode>> getHierarchyMap(List<OntologyNode> selectedOntologyNodes) {
        HashMap<OntologyNode, List<OntologyNode>> hierarchyMap = new HashMap<OntologyNode, List<OntologyNode>>();
        for (OntologyNode node : selectedOntologyNodes) {
            hierarchyMap.put(node, getAllChildren(node));
        }
        return hierarchyMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.core.service.resource.OntologyNodeService#getAllChildren(java.util.List)
     */
    // FIXME: may want to aggregate / batch for efficiency
    @Override
    public Set<OntologyNode> getAllChildren(List<OntologyNode> selectedOntologyNodes) {
        return getDao().getAllChildren(selectedOntologyNodes);
    }

    @Autowired
    ProxyConstructionService proxyConstructionService;

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.core.service.resource.OntologyNodeService#listDatasetsWithMappingsToNode(org.tdar.core.bean.resource.OntologyNode)
     */
    @Override
    public List<PDataset> listDatasetsWithMappingsToNode(POntologyNode node_) {
        List<PDataset> toReturn = new ArrayList<>();
        OntologyNode node = getDao().find(OntologyNode.class, node_.getId());
        if (node == null) {
            return toReturn;
        }
        for (Dataset dataset : getDao().findDatasetsUsingNode(node)) {
            toReturn.add((PDataset) proxyConstructionService.createShellResource(dataset, dataset.getResourceType().getProxyClass(),new Context(null)));
        }
        return toReturn;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.core.service.resource.OntologyNodeService#getParent(org.tdar.core.bean.resource.OntologyNode)
     */
    @Override
    public POntologyNode getParent(POntologyNode node) {
        if (PersistableUtils.isNullOrTransient(node)) {
            return null;
        }
        OntologyNode toReturn = getDao().getParentNode(node.getId());
        if (toReturn == null) {
            return null;
        }
        return proxyConstructionService.convertOntologyNode(toReturn);
    }
}
