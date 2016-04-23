package org.tdar.core.service.processes.daily;

import org.tdar.core.bean.resource.InformationResource;
import org.tdar.core.bean.resource.file.InformationResourceFile;

public class FileResourceContainer {

    private InformationResourceFile file;
    private InformationResource resource;

    public FileResourceContainer(InformationResource resource, InformationResourceFile file) {
        this.resource = resource;
        this.file = file;
    }

    public InformationResourceFile getFile() {
        return file;
    }

    public void setFile(InformationResourceFile file) {
        this.file = file;
    }

    public InformationResource getResource() {
        return resource;
    }

    public void setResource(InformationResource resource) {
        this.resource = resource;
    }
    
    
}
