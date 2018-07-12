package org.tdar.struts.action.resource;



import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.Authorizable;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.struts.action.AbstractAuthenticatableAction;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts_base.action.TdarActionException;
import org.tdar.struts_base.action.TdarActionSupport;
import org.tdar.transform.DcTransformer;
import org.tdar.transform.ModsTransformer;
import org.tdar.web.service.WebLoadingService;

import com.opensymphony.xwork2.Preparable;

import edu.asu.lib.dc.DublinCoreDocument;
import edu.asu.lib.mods.ModsDocument;

@Namespace("/unapi")
@Component
@Scope("prototype")
@ParentPackage("default")
@Result(name = TdarActionSupport.INPUT, type = TdarActionSupport.HTTPHEADER, params = { "status", "400" })
public class JAXBMetadataViewController extends AbstractAuthenticatableAction
        implements Preparable, Authorizable<Resource> {

    private static final long serialVersionUID = -7297306518597493712L;
    public static final String DC = "dc/{id}";
    public static final String MODS = "mods/{id}";
    private ModsDocument modsDocument;
    private DublinCoreDocument dcDocument;
    private Status status;

    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Autowired
    WebLoadingService webLoadingService;
    @Autowired
    AuthorizationService authorizationService;
    
    private Long id;
    private PResource resource;

    public ModsDocument getModsDocument() {
        if (modsDocument == null) {
            modsDocument = ModsTransformer.transformAny(getResource());
        }
        return modsDocument;
    }

    @SkipValidation
    @Action(value = MODS, results = {
            @Result(name = SUCCESS, type = JAXBRESULT, params = { "documentName", "modsDocument", "formatOutput", "true" })
    })
    public String viewMods() throws TdarActionException {
        return SUCCESS;
    }

    public DublinCoreDocument getDcDocument() {
        if (dcDocument == null) {
            dcDocument = DcTransformer.transformAny(getResource());
        }
        return dcDocument;
    }

    @SkipValidation
    @Action(value = DC, results = {
            @Result(name = SUCCESS, type = JAXBRESULT, params = { "documentName", "dcDocument", "formatOutput", "true" })
    })
    public String viewDc() throws TdarActionException {
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public void prepare() throws Exception {
        resource = webLoadingService.load(Resource.class, id, getAuthenticatedUser(), InternalTdarRights.VIEW_ANYTHING, RequestType.VIEW, this);
    }

    public PResource getResource() {
        return resource;
    }

    @Override
    public boolean authorize(Resource r, TdarUser user) {
        return authorizationService.isResourceViewable(user, r);
    }


}
