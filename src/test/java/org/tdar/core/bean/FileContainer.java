package org.tdar.core.bean;

import org.tdar.core.bean.resource.InformationResource;
import org.tdar.core.bean.resource.InformationResourceFile;
import org.tdar.core.bean.resource.InformationResourceFileVersion;

public class FileContainer {

    InformationResource informationResource;
    InformationResourceFile informationResourceFile;
    InformationResourceFileVersion informationResourceFileVersion;
    
    public FileContainer(InformationResource ir, InformationResourceFile irf, InformationResourceFileVersion irfv) {
        this.informationResource = ir;
        this.informationResourceFile = irf;
        this.informationResourceFileVersion = irfv;
    }

    public InformationResource getInformationResource() {
        return informationResource;
    }

    public void setInformationResource(InformationResource informationResource) {
        this.informationResource = informationResource;
    }

    public InformationResourceFile getInformationResourceFile() {
        return informationResourceFile;
    }

    public void setInformationResourceFile(InformationResourceFile informationResourceFile) {
        this.informationResourceFile = informationResourceFile;
    }

    public InformationResourceFileVersion getInformationResourceFileVersion() {
        return informationResourceFileVersion;
    }

    public void setInformationResourceFileVersion(InformationResourceFileVersion informationResourceFileVersion) {
        this.informationResourceFileVersion = informationResourceFileVersion;
    }
    
    
}
