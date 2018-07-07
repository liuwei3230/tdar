package org.tdar.web.service;

import java.util.List;

import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.entity.AuthorizedUser;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.InformationResource;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.PResourceCreatorProxy;
import org.tdar.struts.data.AuthWrapper;

public interface ResourceViewControllerService {

    void initializeResourceCreatorProxyLists(AuthWrapper<PResource> auth, List<PResourceCreatorProxy> authorshipProxies,
            List<PResourceCreatorProxy> creditProxies, List<PResourceCreatorProxy> contactProxies);

    void updateResourceInfo(AuthWrapper<PResource> auth, boolean isBot);

    void updateInfoReadOnly(AuthWrapper<PResource> auth);

    /*
     * Creating a simple transient boolean to handle visibility here instead of freemarker
     */
    void setTransientViewableStatus(PInformationResource ir);

    void loadSharesCollectionsAuthUsers(AuthWrapper<Resource> auth, List<ResourceCollection> effectiveShares,
            List<ResourceCollection> effectiveResourceCollections,
            List<AuthorizedUser> authorizedUsers);

    List<ResourceCollection> getVisibleUnmanagedCollections(AuthWrapper<Resource> auth);

    List<ResourceCollection> getVisibleManagedCollections(AuthWrapper<Resource> auth);

}