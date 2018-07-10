package org.tdar.web.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.entity.AuthorizedUser;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.bean.resource.file.InformationResourceFile;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.serialize.resource.file.PInformationResourceFile;
import org.tdar.core.service.BookmarkedResourceService;
import org.tdar.core.service.PResourceCreatorProxy;
import org.tdar.core.service.ProxyConstructionService;
import org.tdar.core.service.collection.ResourceCollectionService;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.core.service.resource.InformationResourceFileService;
import org.tdar.core.service.resource.ResourceService;
import org.tdar.struts.data.AuthWrapper;

@Service
public class ResourceViewControllerServiceImpl implements ResourceViewControllerService {


    @Autowired
    private BookmarkedResourceService bookmarkedResourceService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private ResourceCollectionService resourceCollectionService;

    @Autowired
    private transient AuthorizationService authorizationService;
    @Autowired
    private transient ProxyConstructionService proxyConstructionService;

    @Autowired
    private transient InformationResourceFileService informationResourceFileService;

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#initializeResourceCreatorProxyLists(org.tdar.struts.data.AuthWrapper, java.util.List,
     * java.util.List, java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public void initializeResourceCreatorProxyLists(AuthWrapper<PResource> auth, List<PResourceCreatorProxy> authorshipProxies,
            List<PResourceCreatorProxy> creditProxies, List<PResourceCreatorProxy> contactProxies) {

        Set<PResourceCreator> resourceCreators = auth.getItem().getResourceCreators();
        resourceCreators = auth.getItem().getActiveResourceCreators();
        if (resourceCreators == null) {
            return;
        }

        // this may be duplicative... check
        for (PResourceCreator rc : resourceCreators) {
            PResourceCreatorProxy proxy = new PResourceCreatorProxy(rc);
            if (ResourceCreatorRole.getAuthorshipRoles().contains(rc.getRole())) {
                authorshipProxies.add(proxy);
            } else {
                creditProxies.add(proxy);
            }

            if (proxy.isValidEmailContact()) {
                contactProxies.add(proxy);
            }
        }
        Collections.sort(authorshipProxies);
        Collections.sort(creditProxies);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#updateResourceInfo(org.tdar.struts.data.AuthWrapper, boolean)
     */
    @Override
    @Transactional(readOnly = false)
    public void updateResourceInfo(AuthWrapper<PResource> auth, boolean isBot) {
        // don't count if we're an admin
        PResource item = auth.getItem();
        if (item == null || auth.getAuthenticatedUser() == null) {
            return;
        }
        if ((item.getSubmitter().getId() != auth.getAuthenticatedUser().getId()) && !auth.isEditor()) {
            Resource r = resourceService.find(item.getId());
            resourceService.incrementAccessCounter(r, isBot);
        }
        updateInfoReadOnly(auth);
        if (item instanceof PInformationResource && auth.getAuthenticatedUser() != null) {
            setTransientViewableStatus((PInformationResource)item);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#updateInfoReadOnly(org.tdar.struts.data.AuthWrapper)
     */
    @Override
    @Transactional(readOnly = true)
    public void updateInfoReadOnly(AuthWrapper<PResource> auth) {
        // only showing access count when logged in (speeds up page loads)
        if (auth.isAuthenticated()) {
            resourceService.updateTransientAccessCount(auth.getItem());
            proxyConstructionService.updateAccount(auth.getItem());
        }
        bookmarkedResourceService.applyTransientBookmarked(Arrays.asList(auth.getItem()), auth.getAuthenticatedUser());
    }

    /*
     * Creating a simple transient boolean to handle visibility here instead of freemarker
     */
    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#setTransientViewableStatus(org.tdar.core.bean.resource.InformationResource,
     * org.tdar.core.bean.entity.TdarUser)
     */
    @Override
    @Transactional(readOnly = true)
    public void setTransientViewableStatus(PInformationResource ir) {
            for (PInformationResourceFile irf : ir.getInformationResourceFiles()) {
                InformationResourceFile irf_ = informationResourceFileService.find(irf.getId());
                irf.setTransientDownloadCount(informationResourceFileService.updateTransientDownloadCount(irf_));
            }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#loadSharesCollectionsAuthUsers(org.tdar.struts.data.AuthWrapper, java.util.List, java.util.List,
     * java.util.List)
     */
    @Override
    @Transactional(readOnly = true)
    public void loadSharesCollectionsAuthUsers(AuthWrapper<Resource> auth, List<ResourceCollection> effectiveShares,
            List<ResourceCollection> effectiveResourceCollections,
            List<AuthorizedUser> authorizedUsers) {
        authorizedUsers.addAll(resourceCollectionService.getAuthorizedUsersForResource(auth.getItem(), auth.getAuthenticatedUser()));
        effectiveShares.addAll(resourceCollectionService.getEffectiveSharesForResource(auth.getItem()));
        effectiveResourceCollections.addAll(resourceCollectionService.getEffectiveResourceCollectionsForResource(auth.getItem()));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#getVisibleCollections(org.tdar.struts.data.AuthWrapper)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResourceCollection> getVisibleManagedCollections(AuthWrapper<Resource> auth) {
        List<ResourceCollection> visibleCollections = new ArrayList<>();
//        visibleCollections.addAll(getViewableSharedResourceCollections(auth));
        return visibleCollections;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.web.service.ResourceViewControllerServ#getVisibleCollections(org.tdar.struts.data.AuthWrapper)
     */
    @Override
    @Transactional(readOnly = true)
    public List<ResourceCollection> getVisibleUnmanagedCollections(AuthWrapper<Resource> auth) {
        List<ResourceCollection> visibleCollections = new ArrayList<>();
//        visibleCollections.addAll(getViewableListResourceCollections(auth));
        return visibleCollections;
    }


    private <C extends ResourceCollection> void addViewableCollections(Set<C> list, Collection<C> incomming, AuthWrapper<Resource> auth) {
        if (auth.isAuthenticated()) {
            for (C resourceCollection : incomming) {
                if (authorizationService.canViewCollection(auth.getAuthenticatedUser(), resourceCollection) && !resourceCollection.isSystemManaged()) {
                    list.add(resourceCollection);
                }
            }
        }
    }

}
