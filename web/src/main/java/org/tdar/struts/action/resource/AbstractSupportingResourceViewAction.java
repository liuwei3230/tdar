package org.tdar.struts.action.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.tdar.core.bean.resource.datatable.DataTable;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.serialize.resource.datatable.PDataTable;
import org.tdar.core.service.ProxyConstructionService;
import org.tdar.core.service.resource.DataTableService;
import org.tdar.struts_base.action.TdarActionException;
import org.tdar.web.service.ResourceViewControllerService;

public abstract class AbstractSupportingResourceViewAction<R extends PInformationResource> extends AbstractResourceViewAction<R> {

    private static final long serialVersionUID = -1581233578894577541L;
    private List<PResource> relatedResources;

    private List<PDataTable> tablesUsingResource;
    @Autowired
    ProxyConstructionService proxyConstructionService;
    @Autowired
    private ResourceViewControllerService rvcs;

    
    @Override
    protected void loadCustomViewMetadata() throws TdarActionException {
        super.loadCustomViewMetadata();
        if (relatedResources == null) {
            relatedResources = rvcs.loadRelatedResourcesForTable(getTablesUsingResource());
        }
    }

    public List<PDataTable> getTablesUsingResource() {
        if (tablesUsingResource == null) {
            tablesUsingResource = rvcs.findDataTablesUsingResource(getId());
        }
        return tablesUsingResource;
    }

    public void setRelatedResources(ArrayList<PResource> relatedResources) {
        this.relatedResources = relatedResources;
    }

    public List<PResource> getRelatedResources() {
        return relatedResources;
    }

}
