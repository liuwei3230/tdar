package org.tdar.core.service.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.tdar.core.bean.resource.Dataset;
import org.tdar.core.bean.resource.InformationResource;
import org.tdar.core.bean.resource.InformationResourceFile;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.datatable.DataTable;

public class ProcessingProxy implements Serializable {

    private static final long serialVersionUID = -3252148475206950111L;
    private ResourceType resourceType;
    private Long id;
    private Set<InformationResourceFile> activeInformationResourceFiles = new HashSet<>();
    private List<String> dataTableNames = new ArrayList<>();
    private InformationResource ir;

    @Override
    public String toString() {
        return String.format("ProcessingProxy[%s] %s (%s)", id, resourceType, activeInformationResourceFiles);
    };
    
    public ProcessingProxy(InformationResource ir) {
        this.id = ir.getId();
        this.resourceType = ir.getResourceType();
        this.activeInformationResourceFiles = ir.getActiveInformationResourceFiles();
        if (resourceType.isDataTableSupported()) {
            for (DataTable dt : ((Dataset) ir).getDataTables()) {
                this.dataTableNames.add(dt.getName());
            }
        }
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<InformationResourceFile> getActiveInformationResourceFiles() {
        return activeInformationResourceFiles;
    }

    public void setActiveInformationResourceFiles(Set<InformationResourceFile> activeInformationResourceFiles) {
        this.activeInformationResourceFiles = activeInformationResourceFiles;
    }

    public List<String> getDataTableNames() {
        return dataTableNames;
    }

    public void setDataTableNames(List<String> dataTableNames) {
        this.dataTableNames = dataTableNames;
    }

    public InformationResource getIr() {
        return ir;
    }

    public void setIr(InformationResource ir) {
        this.ir = ir;
    }
    
    
}
