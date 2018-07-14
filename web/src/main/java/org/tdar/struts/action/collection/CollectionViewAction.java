package org.tdar.struts.action.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.tdar.core.bean.DisplayOrientation;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.SortOption;
import org.tdar.core.bean.Sortable;
import org.tdar.core.bean.collection.CollectionDisplayProperties;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.entity.UserInvite;
import org.tdar.core.bean.entity.permissions.Permissions;
import org.tdar.core.bean.keyword.CultureKeyword;
import org.tdar.core.bean.keyword.GeographicKeyword;
import org.tdar.core.bean.keyword.InvestigationType;
import org.tdar.core.bean.keyword.MaterialKeyword;
import org.tdar.core.bean.keyword.OtherKeyword;
import org.tdar.core.bean.keyword.SiteNameKeyword;
import org.tdar.core.bean.keyword.SiteTypeKeyword;
import org.tdar.core.bean.keyword.TemporalKeyword;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.bean.resource.file.VersionType;
import org.tdar.core.bean.statistics.ResourceCollectionViewStatistic;
import org.tdar.core.cache.ThreadPermissionsCache;
import org.tdar.core.dao.external.auth.InternalTdarRights;
import org.tdar.core.exception.StatusCode;
import org.tdar.core.serialize.collection.PCollectionDisplayProperties;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.Authorizable;
import org.tdar.core.service.BookmarkedResourceService;
import org.tdar.core.service.FileSystemResourceService;
import org.tdar.core.service.ProxyConstructionService;
import org.tdar.core.service.UserRightsProxyService;
import org.tdar.core.service.collection.ResourceCollectionService;
import org.tdar.core.service.collection.WhiteLabelFiles;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.filestore.FilestoreObjectType;
import org.tdar.filestore.PairtreeFilestore;
import org.tdar.search.exception.SearchPaginationException;
import org.tdar.search.query.ProjectionModel;
import org.tdar.search.query.QueryFieldNames;
import org.tdar.search.query.facet.Facet;
import org.tdar.search.query.facet.FacetWrapper;
import org.tdar.search.query.facet.FacetedResultHandler;
import org.tdar.search.service.query.ResourceSearchService;
import org.tdar.struts.action.AbstractAuthenticatableAction;
import org.tdar.struts.action.AbstractPersistableViewableAction;
import org.tdar.struts.action.ResourceFacetedAction;
import org.tdar.struts.action.SlugViewAction;
import org.tdar.struts.action.AbstractPersistableController.RequestType;
import org.tdar.struts.interceptor.annotation.HttpsOnly;
import org.tdar.struts_base.action.TdarActionException;
import org.tdar.struts_base.action.TdarActionSupport;
import org.tdar.utils.PaginationHelper;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.TitleSortComparator;
import org.tdar.web.service.HomepageDetails;
import org.tdar.web.service.HomepageService;
import org.tdar.web.service.WebLoadingService;

import com.opensymphony.xwork2.Preparable;

@Component
@Scope("prototype")
@ParentPackage("default")
@Namespace("/collection")
@Results(value = {
        @Result(name = TdarActionSupport.SUCCESS, location = "view.ftl"),
        @Result(name = CollectionViewAction.SUCCESS_WHITELABEL, location = "view-whitelabel.ftl"),
        @Result(name = TdarActionSupport.BAD_SLUG, type = TdarActionSupport.TDAR_REDIRECT,
                location = "${id}/${persistable.slug}${slugSuffix}", params = { "ignoreParams", "id,slug" }), // removed ,keywordPath
        @Result(name = TdarActionSupport.INPUT, type = TdarActionSupport.HTTPHEADER, params = { "error", "404" })
})
public class CollectionViewAction<C extends ResourceCollection> extends AbstractAuthenticatableAction implements  Preparable, SlugViewAction , Authorizable<ResourceCollection> , FacetedResultHandler<PResource>,
        ResourceFacetedAction {

    private static final long serialVersionUID = 5126290300997389535L;

    public static final String SUCCESS_WHITELABEL = "success_whitelabel";

    private ThreadPermissionsCache permissionsCache;
    private List<UserInvite> invites;
    /**
     * Threshold that defines a "big" collection (based on imperical evidence by highly-trained tDAR staff). This number
     * refers to the combined count of authorized users +the count of resources associated with a collection. Big
     * collections may adversely affect save/load times as well as cause rendering problems on the client, and so the
     * system may choose to mitigate these effects (somehow)
     */
    public static final int BIG_COLLECTION_CHILDREN_COUNT = 3_000;

    @Autowired
    private HomepageService homepageService;
    @Autowired
    private transient ResourceSearchService resourceSearchService;
    @Autowired
    private transient ResourceCollectionService resourceCollectionService;
    @Autowired
    private transient AuthorizationService authorizationService;
    @Autowired
    private transient BookmarkedResourceService bookmarkedResourceService;
    @Autowired
    private transient FileSystemResourceService fileSystemResourceService;
    @Autowired
    private transient UserRightsProxyService userRightsProxyService;
    @Autowired
    private transient WebLoadingService webLoadingService;
    @Autowired
    private transient ProxyConstructionService proxyConstructionService;
    private Long parentId;
    private Long id;
    private List<PResourceCollection> collections = new LinkedList<>();
    private Long viewCount = 0L;
    private int startRecord = DEFAULT_START;
    private int recordsPerPage = getDefaultRecordsPerPage();
    private int totalRecords;
    private List<PResource> results;
    private SortOption secondarySortField;
    private SortOption sortField;
    private String mode = "CollectionBrowse";
    private PaginationHelper paginationHelper;
    private String parentCollectionName;
    private ArrayList<ResourceType> selectedResourceTypes = new ArrayList<ResourceType>();
    private boolean showNavSearchBox = true;
    private FacetWrapper facetWrapper = new FacetWrapper();

    private ProjectionModel projectionModel = ProjectionModel.HIBERNATE_DEFAULT;

    private boolean keywordSectionVisible = true;

    private DisplayOrientation orientation;

    private HomepageDetails homepageGraphs;

    private String schemaOrgJsonLD;

    private List<PResourceCollection> parentCollections;

    private PResourceCollection collection;

    private Status status;

    private boolean visible;

    private boolean editable;

    private String slugSuffix;

    private boolean redirectBadSlug;

    private List<PResourceCollection> parents = new ArrayList<>();

    private String slug;

    /**
     * Returns a list of all resource collections that can act as candidate parents for the current resource collection.
     * 
     * @return
     */
    public List<PResourceCollection> getCandidateParentResourceCollections() {
        return parentCollections;
    }


    public boolean isVisible() {
        return visible;
    }

    public PResourceCollection getResourceCollection() {
        return collection;
    }

//    public void setResourceCollection(PResourceCollection rc) {
//        this.collection = rc;
//    }


    public List<SortOption> getSortOptions() {
        return SortOption.getOptionsForResourceCollectionPage();
    }

    public void loadExtraViewMetadata() {
        if (PersistableUtils.isNullOrTransient(getPersistable())) {
            return;
        }
        ResourceCollection rc = getGenericService().find(ResourceCollection.class, id);
        parents.addAll(webLoadingService.proxy(resourceCollectionService.findPotentialParentCollections(getAuthenticatedUser(), rc), getAuthenticatedUser()));
        setParentId(getPersistable().getParentId());
        if (!isEditor()) {
            ResourceCollectionViewStatistic rcvs = new ResourceCollectionViewStatistic(new Date(), rc, isBot());
            getGenericService().saveOrUpdate(rcvs);
        } else {
            // setViewCount(resourceCollectionService.getCollectionViewCount(getPersistable()));
        }

        reSortFacets(this, (Sortable) getPersistable());
        getLogger().trace("child collections: begin");
        TreeSet<PResourceCollection> findAllChildCollections = new TreeSet<>(new TitleSortComparator());

        if (isAuthenticated()) {
            resourceCollectionService.buildCollectionTreeForController(rc, getAuthenticatedUser());
            findAllChildCollections.addAll(getPersistable().getTransientChildren());
        } else {
            findAllChildCollections.addAll(webLoadingService.proxy(resourceCollectionService.findDirectChildCollections(getId(), false), getAuthenticatedUser()));
        }
        findAllChildCollections.addAll(webLoadingService.proxy(resourceCollectionService.findAlternateChildren(Arrays.asList(getId()), getAuthenticatedUser()), getAuthenticatedUser()));
        setCollections(new ArrayList<PResourceCollection>(findAllChildCollections));
        getLogger().trace("child collections: sort");
        // Collections.sort(collections);
        getLogger().trace("child collections: end");

        setInvites(userRightsProxyService.findUserInvites(getPersistable()));

        // if this collection is public, it will appear in a resource's public collection id list, otherwise it'll be in the shared collection id list
        // String collectionListFieldName = getPersistable().isVisible() ? QueryFieldNames.RESOURCE_COLLECTION_PUBLIC_IDS
        // : QueryFieldNames.RESOURCE_COLLECTION_SHARED_IDS;

        getLogger().trace("lucene: end");
    }

    @HttpsOnly
    @Actions(value = {
            @Action(value = "{id}/{slug}"),
            @Action(value = "{id}")
    })
    public String view() throws TdarActionException {
        if (collection== null) {
            return INPUT;
        }
        if (isWhiteLabelCollection()) {
            if (isSearchHeaderEnabled()) {
                showNavSearchBox = false;
            }
            return  CollectionViewAction.SUCCESS_WHITELABEL;
        }

        // if (SUCCESS.equals(result) && getPersistable().getType() == CollectionType.SHARED) {
        // result = SUCCESS_SHARE;
        // }
        return SUCCESS;
    }

    public boolean isWhiteLabelCollection() {
        if (collection == null) {
            return false;
        }
        if (collection.getProperties() != null && collection.getProperties().getWhitelabel()) {
            return true;
        }
        return false;
    }

    public FacetWrapper getFacetWrapper() {
        return facetWrapper;
    }

    public void setFacetWrapper(FacetWrapper facetWrapper) {
        this.facetWrapper = facetWrapper;
    }

    private void buildLuceneSearch() throws TdarActionException {
        // the visibilty fence should take care of visible vs. shared above
        facetWrapper.facetBy(QueryFieldNames.RESOURCE_TYPE, ResourceType.class, selectedResourceTypes);
        setSortField(((Sortable) getPersistable()).getSortBy());
        if (getSortField() != SortOption.RELEVANCE) {
            setSecondarySortField(SortOption.TITLE);
            if (getPersistable().getSecondarySortBy() != null) {
                setSecondarySortField(getPersistable().getSecondarySortBy());
            }
        }

        homepageService.setupResultForMapSearch(this);
        try {
            ResourceCollection rc = getGenericService().find(ResourceCollection.class, id);

            resourceSearchService.buildResourceContainedInSearch(rc, null, getAuthenticatedUser(), this, this);
            homepageGraphs = homepageService.generateDetails(this);
            bookmarkedResourceService.applyTransientBookmarked(getResults(), getAuthenticatedUser());
        } catch (SearchPaginationException spe) {
            throw new TdarActionException(StatusCode.BAD_REQUEST, spe);
        } catch (Exception e) {
            addActionErrorWithException(getText("collectionController.error_searching_contents"), e);
        }
    }

    @Override
    public SortOption getSortField() {
        return this.sortField;
    }

    @Override
    public SortOption getSecondarySortField() {
        return this.secondarySortField;
    }

    @Override
    public void setTotalRecords(int resultSize) {
        this.totalRecords = resultSize;
    }

    @Override
    public int getStartRecord() {
        return this.startRecord;
    }

    @Override
    public int getRecordsPerPage() {
        return this.recordsPerPage;
    }

    @Override
    public boolean isDebug() {
        return false;
    }

    @Override
    public void setStartRecord(int startRecord) {
        this.startRecord = startRecord;
    }

    @Override
    public void setRecordsPerPage(int recordsPerPage) {
        this.recordsPerPage = recordsPerPage;
    }

    public void setCollections(List<PResourceCollection> findAllChildCollections) {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace("child collections: {}", findAllChildCollections);
        }
        this.collections = findAllChildCollections;
    }

    public List<PResourceCollection> getCollections() {
        return this.collections;
    }

    @Override
    public boolean isRightSidebar() {
        return true;
    };

    @Override
    public int getTotalRecords() {
        return totalRecords;
    }

    @Override
    public void setResults(List<PResource> toReturn) {
        getLogger().trace("setResults: {}", toReturn);
        this.results = toReturn;
    }

    @Override
    public List<PResource> getResults() {
        return results;
    }

    public void setSecondarySortField(SortOption secondarySortField) {
        this.secondarySortField = secondarySortField;
    }

    @Override
    public void setSortField(SortOption sortField) {
        this.sortField = sortField;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.struts.search.query.SearchResultHandler#setMode(java.lang.String)
     */
    @Override
    public void setMode(String mode) {
        this.mode = mode;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.struts.search.query.SearchResultHandler#getMode()
     */
    @Override
    public String getMode() {
        return mode;
    }

    @Override
    public int getNextPageStartRecord() {
        return startRecord + recordsPerPage;
    }

    @Override
    public int getPrevPageStartRecord() {
        return startRecord - recordsPerPage;
    }

    @Override
    public String getSearchTitle() {
        return getText("collectionViewAction.search_title", Arrays.asList(getPersistable().getTitle()));
    }

    @Override
    public String getSearchDescription() {
        return getSearchTitle();
    }

    public PaginationHelper getPaginationHelper() {
        if (paginationHelper == null) {
            paginationHelper = PaginationHelper.withSearchResults(this);
        }
        return paginationHelper;
    }

    public String getParentCollectionName() {
        return parentCollectionName;

    }

    public List<Facet> getResourceTypeFacets() {
        return getFacetWrapper().getFacetResults().get(QueryFieldNames.RESOURCE_TYPE);
    }

    public ArrayList<ResourceType> getSelectedResourceTypes() {
        return selectedResourceTypes;
    }

    public void setSelectedResourceTypes(ArrayList<ResourceType> selectedResourceTypes) {
        this.selectedResourceTypes = selectedResourceTypes;
    }

    @Override
    public ProjectionModel getProjectionModel() {
        return projectionModel;
    }

    public void setProjectionModel(ProjectionModel model) {
        this.projectionModel = model;
    }

    /**
     * A hint to the view-layer that this resource collection is "big". The view-layer may choose to gracefully degrade the presentation to save on bandwidth
     * and/or
     * client resources.
     * 
     * @return
     */
    public boolean isBigCollection() {
        return (((PResourceCollection) getPersistable()).getManagedResources().size() ) > BIG_COLLECTION_CHILDREN_COUNT;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isEditable() {
        return editable;
    }

    @Override
    public void prepare() throws Exception {
        collection = webLoadingService.load(ResourceCollection.class, getId(), getAuthenticatedUser(), InternalTdarRights.VIEW_ANYTHING, RequestType.VIEW, this);
        getLogger().debug("{}", collection);
        setPermissionsCache(new ThreadPermissionsCache(isEditor()));
        if (!isRedirectBadSlug() && PersistableUtils.isNotTransient(getPersistable())) {

            try {
                setSchemaOrgJsonLD(resourceCollectionService.getSchemaOrgJsonLD(getPersistable()));
            } catch (Exception ioe) {
                getLogger().warn("issues creating json", ioe);
            }
            if (isKeywordSectionVisible()) {
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_CULTURE_KEYWORDS, CultureKeyword.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_INVESTIGATION_TYPES, InvestigationType.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_MATERIAL_KEYWORDS, MaterialKeyword.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_TEMPORAL_KEYWORDS, TemporalKeyword.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_GEOGRAPHIC_KEYWORDS, GeographicKeyword.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_OTHER_KEYWORDS, OtherKeyword.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_SITE_TYPE_KEYWORDS, SiteTypeKeyword.class);
                getFacetWrapper().facetBy(QueryFieldNames.ACTIVE_SITE_NAME_KEYWORDS, SiteNameKeyword.class);
            }
            try {
                buildLuceneSearch();
            } catch (Exception e) {
                if (e.getCause() instanceof SearchPaginationException) {
                    getLogger().warn("search pagination issue", e);
                } else {
                    throw e;
                }
            }
            
            // if we have no results, hide the keyword section entirely
            if (getTotalRecords() < 1) {
                setKeywordSectionVisible(false);
            }
         loadExtraViewMetadata();   
        }

    }

    public boolean isManaged(PResource r) {
        for (PResourceCollection rc : r.getManagedResourceCollections()) {
            if (rc.equals(getResourceCollection())) {
                return true;
            }
            if (rc.getParentIds().contains(getId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getDefaultRecordsPerPage() {
        return 100;
    }

    public boolean isLogoAvailable() {
        return checkLogoAvailable(FilestoreObjectType.COLLECTION, getId(), VersionType.WEB_LARGE);
    }

    
    public List<Permissions> getAvailablePermissions() {
        List<Permissions> permissions = Permissions.getAvailablePermissionsFor(ResourceCollection.class);
        return permissions;
    }


    public boolean isSearchHeaderLogoAvailable() {
        // for now, we just look in hosted + collection ID
        return checkHostedFileAvailable(WhiteLabelFiles.SEARCH_HEADER_FILENAME, getId());
    }
    
    protected boolean checkHostedFileAvailable(String filename, Long id) {
        return fileSystemResourceService.checkHostedFileAvailable(filename, FilestoreObjectType.COLLECTION, id);
    }


    /**
     * Indicate to view layer that we should display a search header.
     * 
     * @return
     */
    public boolean isSearchHeaderEnabled() {
        PCollectionDisplayProperties properties = getResourceCollection().getProperties();
        if (properties != null && properties.getSearchEnabled()) {
            return true;
        }
        return false;
    }

    /**
     * Indicates whether the view layer should show sub-navigation elements. We turn this off when the 'search header' is enabled.
     *
     */
    @Override
    public boolean isSubnavEnabled() {
        return !isSearchHeaderEnabled();
    }

    /**
     * Return the default/suggested base url for static content (trailing slash removed, if present)
     * 
     * @return
     */
    @Override
    public String getHostedContentBaseUrl() {
        String baseUrl = super.getHostedContentBaseUrl() + "/";
        if (PersistableUtils.isNotNullOrTransient(getResourceCollection())) {
            baseUrl += PairtreeFilestore.toPairTree(getResourceCollection().getId());
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = StringUtils.chop(baseUrl);
        }
        return baseUrl;
    }

    @Override
    public boolean isNavSearchBoxVisible() {
        return showNavSearchBox;
    }

    @Override
    public void setSearchTitle(String description) {
        // TODO Auto-generated method stub

    }

    @Override
    public DisplayOrientation getOrientation() {
        if (orientation == null) {
            return getPersistable().getOrientation();
        }
        return orientation;
    }

    public void setOrientation(DisplayOrientation orientation) {
        this.orientation = orientation;
    }

    public boolean isKeywordSectionVisible() {
        return keywordSectionVisible;
    }

    public void setKeywordSectionVisible(boolean keywordSectionVisible) {
        this.keywordSectionVisible = keywordSectionVisible;
    }

    public HomepageDetails getHomepageGraphs() {
        return homepageGraphs;
    }

    public void setHomepageGraphs(HomepageDetails homepageGraphs) {
        this.homepageGraphs = homepageGraphs;
    }

    public List<DisplayOrientation> getAvailableOrientations() {
        return Arrays.asList(DisplayOrientation.values());
    }

    public String getSchemaOrgJsonLD() {
        return schemaOrgJsonLD;
    }

    public void setSchemaOrgJsonLD(String schemaOrgJsonLD) {
        this.schemaOrgJsonLD = schemaOrgJsonLD;
    }

    public List<UserInvite> getInvites() {
        return invites;
    }

    public void setInvites(List<UserInvite> invites) {
        this.invites = invites;
    }

    public ThreadPermissionsCache getPermissionsCache() {
        return permissionsCache;
    }

    public void setPermissionsCache(ThreadPermissionsCache permissionsCache) {
        this.permissionsCache = permissionsCache;
    }

    @Override
    public PResourceCollection getPersistable() {
        return collection;
    }

    @Override
    public boolean authorize(ResourceCollection t, TdarUser user) throws Exception {
        if (t == null) {
            throw new TdarActionException(StatusCode.NOT_FOUND, "not found");
        }

        visible = authorizationService.canViewCollection(user,t);

        if (!t.isHidden() &&  visible) {
            visible = true;
        }

        if (t.isSystemManaged() && !isEditor()) {
            editable = false;
        } else if (authorizationService.canEditCollection(user, t)) {    
            editable = true;
        }
        return visible;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
        
    }

    @Override
    public void setSlugSuffix(String slugSuffix) {
        this.slugSuffix = slugSuffix;
        
    }

    @Override
    public String getSlugSuffix() {
        return slugSuffix;
    }

    @Override
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }

    @Override
    public boolean isRedirectBadSlug() {
        return redirectBadSlug;
    }
    
    public void setRedirectBadSlug(boolean slug) {
        this.redirectBadSlug = slug;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
