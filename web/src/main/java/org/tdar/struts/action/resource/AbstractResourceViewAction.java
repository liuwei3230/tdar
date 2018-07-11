package org.tdar.struts.action.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.TdarGroup;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.entity.permissions.Permissions;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.bean.resource.file.VersionType;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.core.dao.resource.stats.ResourceSpaceUsageStatistic;
import org.tdar.core.exception.StatusCode;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.entity.PUserInvite;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.serialize.resource.PResourceAnnotation;
import org.tdar.core.serialize.resource.PResourceAnnotationKey;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;
import org.tdar.core.service.Authorizable;
import org.tdar.core.service.PResourceCreatorProxy;
import org.tdar.core.service.UserRightsProxyService;
import org.tdar.core.service.collection.ResourceCollectionService;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.core.service.resource.ResourceService;
import org.tdar.filestore.FilestoreObjectType;
import org.tdar.struts.action.AbstractAuthenticatableAction;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts.action.SlugViewAction;
import org.tdar.struts.data.AuthWrapper;
import org.tdar.struts.interceptor.annotation.HttpsOnly;
import org.tdar.struts_base.action.TdarActionException;
import org.tdar.struts_base.action.TdarActionSupport;
import org.tdar.transform.OpenUrlFormatter;
import org.tdar.utils.ResourceCitationFormatter;
import org.tdar.web.service.ResourceViewControllerService;
import org.tdar.web.service.WebLoadingService;

import com.opensymphony.xwork2.Preparable;

/**
 * $Id$
 * 
 * Provides basic metadata support for controllers that manage subtypes of
 * Resource.
 * 
 * Don't extend this class unless you need this metadata to be set.
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
@Component
@Scope("prototype")
@ParentPackage("default")
@Namespace("/resource")
@Results(value = {
        @Result(name = TdarActionSupport.SUCCESS, location = "../resource/view-template.ftl"),
        @Result(name = TdarActionSupport.BAD_SLUG, type = TdarActionSupport.TDAR_REDIRECT,
                location = "${id}/${persistable.slug}${slugSuffix}", params = { "ignoreParams", "id,keywordPath,slug", "statusCode", "301" }),
        @Result(name = TdarActionSupport.INPUT, type = TdarActionSupport.HTTPHEADER, params = { "error", "404" }),
        @Result(name = TdarActionSupport.DRAFT, location = "/WEB-INF/content/errors/resource-in-draft.ftl")
})

public abstract class AbstractResourceViewAction<R extends PResource> extends AbstractAuthenticatableAction implements  Preparable, SlugViewAction , Authorizable<Resource> {

    private static final long serialVersionUID = 896347341133309643L;

    @Autowired
    private transient AuthorizationService authorizationService;

    @Autowired
    private transient UserRightsProxyService userRightsProxyService;

    @Autowired
    public ResourceCollectionService resourceCollectionService;

    @Autowired
    private ResourceService resourceService;

    private List<PResourceCreatorProxy> authorshipProxies = new ArrayList<>();
    private List<PResourceCreatorProxy> creditProxies = new ArrayList<>();
    private List<PResourceCreatorProxy> contactProxies = new ArrayList<>();
    private ResourceCitationFormatter resourceCitation;
    private boolean redirectBadSlug;
    private String slug;
    private String slugSuffix;
    private String schemaOrgJsonLD;
    private Map<PDataTableColumn, String> mappedData;
    private List<PUserInvite> invites;

    @Autowired
    ResourceViewControllerService viewService;


    public String getOpenUrl() {
        return OpenUrlFormatter.toOpenURL(getResource());
    }

    public String getGoogleScholarTags() throws Exception {
        return resourceService.getGoogleScholarTags(getResource());
    }
    

    @Autowired
    WebLoadingService webLoadingService;
    
    private R resource;
    private Long id;
    private boolean canViewConfidential;
    private ResourceSpaceUsageStatistic totalResourceAccessStatistic;
    private ResourceSpaceUsageStatistic uploadedResourceAccessStatistic;
    private Set<PResourceCollection> effectiveShares = new HashSet<>(); 
    
    @Override
    public void prepare() throws Exception {
        try {
        resource = (R) webLoadingService.load(Resource.class, getPersistableClass(), getId(), getAuthenticatedUser(), InternalTdarRights.VIEW_ANYTHING, RequestType.VIEW, this);
        for (PResourceCollection rc : resource.getManagedResourceCollections()) {
            PResourceCollection r = rc;
            while (r != null) {
                getEffectiveShares().add(r);
                r = r.getParent();
            }
        }
        handleSlug();
        } catch (Throwable t) {
            getLogger().error("{}",t,t);
            throw t;
        }
    }

    public String loadViewMetadata() throws TdarActionException {
        if (getResource() == null) {
            return ERROR;
        }
        AuthWrapper<PResource> authWrapper = new AuthWrapper<>(getResource(), isAuthenticated(), getAuthenticatedUser(), isEditor());
        
        viewService.updateResourceInfo(authWrapper, isBot());
        viewService.initializeResourceCreatorProxyLists(authWrapper, authorshipProxies, creditProxies, contactProxies);
        
        setResourceCitation(new ResourceCitationFormatter(getResource()));
        setSchemaOrgJsonLD(resourceService.getSchemaOrgJsonLD(getResource()));
        loadBasicViewMetadata();
        loadCustomViewMetadata();
        if (isEditor()) {
            if (getResource().getResourceType().isProject()) {
                setUploadedResourceAccessStatistic(resourceService.getResourceSpaceUsageStatisticsForProject(getId(), null));
            } else {
                setUploadedResourceAccessStatistic(resourceService.getResourceSpaceUsageStatistics(Arrays.asList(getId()), null));
            }
        }
        whiteLabelCollection = resourceCollectionService.getWhiteLabelCollectionForResource(getId());
        setInvites(userRightsProxyService.findUserInvitesForResource(getId(), getAuthenticatedUser()));

        return SUCCESS;
    }

    @HttpsOnly
    @Actions(value = {
            @Action(value = "{id}/{slug}"),
            @Action(value = "{id}/"),
            @Action(value = "{id}")
    })
    @SkipValidation
    public String view() throws TdarActionException {
        if (isRedirectBadSlug()) {
            return BAD_SLUG;
        }
        String resultName = SUCCESS;

        resultName = loadViewMetadata();
        loadExtraViewMetadata();
        return resultName;
    }

    protected void loadExtraViewMetadata() {
        // TODO Auto-generated method stub
        
    }

    protected void loadCustomViewMetadata() throws TdarActionException {
    }

    @Override
    public boolean authorize(Resource resource, TdarUser user) throws TdarActionException {
        boolean result = authorizationService.isResourceViewable(user, resource);
        if (result == false) {
            getLogger().debug("{}/{}", status, resource);
            if (getStatus() == Status.DELETED) {
                getLogger().debug("resource not viewable because it is deleted: {}", getResource());
                throw new TdarActionException(StatusCode.GONE, getText("abstractResourceController.resource_deleted"));
            }

            if (getStatus() == Status.DRAFT) {
                getLogger().trace("resource not viewable because it is draft: {}", getResource());
                throw new TdarActionException(StatusCode.OK, DRAFT,
                        getText("abstractResourceController.this_record_is_in_draft_and_is_only_available_to_authorized_users"));
            }
            if (getResource() == null) {
                abort(StatusCode.UNKNOWN_ERROR, getText("abstractPersistableController.not_found"));
            }
        }
        
        canViewConfidential = authorizationService.canViewConfidentialInformation(user, resource);
        editable = authorizationService.canEditResource(user, resource, Permissions.MODIFY_METADATA);
        return result;
    }
    
    public List<Permissions> getAvailablePermissions() {
        List<Permissions> permissions = Permissions.getAvailablePermissionsFor(Resource.class);
        return permissions;
    }


    public void loadBasicViewMetadata() {
    }

    public R getResource() {
        return resource;
    }

    public void setResource(R resource) {
        getLogger().debug("setResource: {}", resource);
    }

    public boolean isAbleToViewConfidentialFiles() {
        return canViewConfidential;
    }

    public List<ResourceCreatorRole> getAllResourceCreatorRoles() {
        return ResourceCreatorRole.getAll();
    }

    public Set<PResourceAnnotationKey> getAllResourceAnnotationKeys() {
        Set<PResourceAnnotationKey> keys = new HashSet<>();
        if ((getPersistable() != null) && CollectionUtils.isNotEmpty(getResource().getActiveResourceAnnotations())) {
            for (PResourceAnnotation ra : getResource().getActiveResourceAnnotations()) {
                keys.add(ra.getResourceAnnotationKey());
            }
        }
        return keys;
    }

    public List<PResourceCreatorProxy> getAuthorshipProxies() {
        if (CollectionUtils.isEmpty(authorshipProxies)) {
            authorshipProxies = new ArrayList<>();
        }
        return authorshipProxies;
    }

    public List<PResourceCreatorProxy> getContactProxies() {
        if (CollectionUtils.isEmpty(contactProxies)) {
            contactProxies = new ArrayList<>();
        }
        return contactProxies;
    }

    public void setAuthorshipProxies(List<PResourceCreatorProxy> authorshipProxies) {
        this.authorshipProxies = authorshipProxies;
    }

    public List<PResourceCreatorProxy> getCreditProxies() {
        if (CollectionUtils.isEmpty(creditProxies)) {
            creditProxies = new ArrayList<>();
        }
        return creditProxies;
    }


    public boolean isUserAbleToReTranslate() {
        return editable;
    }

    public boolean isUserAbleToViewDeletedFiles() {
        return isEditor();
    }

    public boolean isUserAbleToViewUnobfuscatedMap() {
        return isEditor() && authorizationService.isMember(getAuthenticatedUser(), TdarGroup.TDAR_RPA_MEMBER);
    }

    private Boolean editable = null;

    public boolean isEditable() {
        return editable;
    }

    public String getSchemaOrgJsonLD() {
        return schemaOrgJsonLD;
    }

    public void setSchemaOrgJsonLD(String schemaOrgJsonLD) {
        this.schemaOrgJsonLD = schemaOrgJsonLD;
    }

    private PResourceCollection whiteLabelCollection;

    private Status status;

    @XmlTransient
    /**
     * We assume for now that a resource will only belong to a single white-label collection.
     *
     * @return
     */
    public PResourceCollection getWhiteLabelCollection() {
        return whiteLabelCollection;
    }

    public boolean isWhiteLabelLogoAvailable() {
        PResourceCollection wlc = getWhiteLabelCollection();
        return wlc != null && checkLogoAvailable(FilestoreObjectType.COLLECTION, wlc.getId(), VersionType.WEB_LARGE);
    }

    public String getWhiteLabelLogoUrl() {
        return String.format("/files/collection/lg/%s/logo", getWhiteLabelCollection().getId());
    }

    public ResourceCitationFormatter getResourceCitation() {
        return resourceCitation;
    }

    public void setResourceCitation(ResourceCitationFormatter resourceCitation) {
        this.resourceCitation = resourceCitation;
    }

    public Map<PDataTableColumn, String> getMappedData() {
        return mappedData;
    }

    public void setMappedData(Map<PDataTableColumn, String> mappedData) {
        this.mappedData = mappedData;
    }

    @Override
    public boolean isRightSidebar() {
        return true;
    }

    public List<PUserInvite> getInvites() {
        return invites;
    }

    public void setInvites(List<PUserInvite> invites) {
        this.invites = invites;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceSpaceUsageStatistic getTotalResourceAccessStatistic() {
        return totalResourceAccessStatistic;
    }

    public void setTotalResourceAccessStatistic(ResourceSpaceUsageStatistic totalResourceAccessStatistic) {
        this.totalResourceAccessStatistic = totalResourceAccessStatistic;
    }

    public ResourceSpaceUsageStatistic getUploadedResourceAccessStatistic() {
        return uploadedResourceAccessStatistic;
    }

    public void setUploadedResourceAccessStatistic(ResourceSpaceUsageStatistic uploadedResourceAccessStatistic) {
        this.uploadedResourceAccessStatistic = uploadedResourceAccessStatistic;
    }

    public boolean isRedirectBadSlug() {
        return redirectBadSlug;
    }

    public void setRedirectBadSlug(boolean redirectBadSlug) {
        this.redirectBadSlug = redirectBadSlug;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSlugSuffix() {
        return slugSuffix;
    }

    public void setSlugSuffix(String slugSuffix) {
        this.slugSuffix = slugSuffix;
    }

    protected void handleSlug() {
        if (!handleSlugRedirect(resource, this)) {
            setRedirectBadSlug(true);
        }
    }

    public abstract Class<R> getPersistableClass();
    
    public R getPersistable() {
        return resource;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status  = status;
    }

    public Set<PResourceCollection> getEffectiveShares() {
        return effectiveShares;
    }

    public void setEffectiveShares(Set<PResourceCollection> effectiveShares) {
        this.effectiveShares = effectiveShares;
    }

}
