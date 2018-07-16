package org.tdar.core.service;

import java.util.HashMap;
import java.util.Map;

import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.billing.BillingAccount;
import org.tdar.core.bean.collection.ResourceCollection;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.entity.permissions.Permissions;
import org.tdar.core.bean.resource.Resource;
import org.tdar.core.serialize.billing.PBillingAccount;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.entity.PCreator;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.external.AuthorizationService;
import org.tdar.utils.PersistableUtils;

public class Context {

    private TdarUser user;
    private Map<Long, Permissions> collectionPermissions = new HashMap<>();
    private Map<Long, Permissions> resourcePermissions = new HashMap<>();
    private Map<Long, Permissions> accountPermissions = new HashMap<>();
    private Map<Long, PResource> resourceCache = new HashMap<>();
    private Map<Long, PBillingAccount> accountCache = new HashMap<>();
    private Map<Long, PCreator> creatorCache = new HashMap<>();
    private Map<Long, PResourceCollection> collectionCache = new HashMap<>();
    private boolean admin = false;
    private boolean ableToSeeConfidentialFiles = false;
    private boolean institutionEmailObfuscated = false;
    private boolean personEmailObfuscated = false;
    private boolean mappedMetadataIncluded = false;
    private boolean viewUnobfuscatedLat = false;

    public boolean isAdmin() {
        return admin;
    }

    public void setAbleToSeeConfidentialFiles(boolean ableToSeeConfidentialFiles) {
        this.ableToSeeConfidentialFiles = ableToSeeConfidentialFiles;
    }

    public void setInstitutionEmailObfuscated(boolean institutionEmailObfuscated) {
        this.institutionEmailObfuscated = institutionEmailObfuscated;
    }

    public void setPersonEmailObfuscated(boolean personEmailObfuscated) {
        this.personEmailObfuscated = personEmailObfuscated;
    }

    public Context(TdarUser user) {
        this.setUser(user);
    }

    public Map<Long, Permissions> getResourcePermissions() {
        return resourcePermissions;
    }

    public void setResourcePermissions(Map<Long, Permissions> resourcePermissions) {
        this.resourcePermissions = resourcePermissions;
    }

    public Map<Long, Permissions> getCollectionPermissions() {
        return collectionPermissions;
    }

    public void setCollectionPermissions(Map<Long, Permissions> collectionPermissions) {
        this.collectionPermissions = collectionPermissions;
    }

    public TdarUser getUser() {
        return user;
    }

    public void setUser(TdarUser user) {
        this.user = user;
    }

    public boolean isPersonEmailObfuscated() {
        return personEmailObfuscated;
    }

    public boolean isInstitutionEmailObfuscated() {
        return institutionEmailObfuscated;
    }

    public boolean isAbleToSeeConfidentialFiles() {
        return ableToSeeConfidentialFiles;
    }

    public void setAdmin(boolean b) {
        this.admin = b;

    }

    public Long getUserId() {
        if (getUser() == null) {
            return null;
        }
        return getUser().getId();
    }

    public boolean isMappedMetadaataIncluded() {
        return mappedMetadataIncluded;
    }

    public void setViewUnobfuscatedLat(boolean viewU) {
        viewUnobfuscatedLat = viewU;
    }

    public boolean isViewUnobfuscatedLat() {
        return viewUnobfuscatedLat;
    }

    public boolean canView(AuthorizationService authorizationService, ResourceCollection rc_) {
        return checkPermissionsCache(authorizationService, Permissions.VIEW_ALL, collectionPermissions, rc_);
    }

    public boolean canModifyMetadata(AuthorizationService authorizationService, ResourceCollection rc_) {
        return checkPermissionsCache(authorizationService, Permissions.MODIFY_RECORD, collectionPermissions, rc_);
    }

    public boolean canView(AuthorizationService authorizationService, Resource rc_) {
        return checkPermissionsCache(authorizationService, Permissions.VIEW_ALL, resourcePermissions, rc_);
    }

    public boolean canModifyMetadata(AuthorizationService authorizationService, Resource rc_) {
        return checkPermissionsCache(authorizationService, Permissions.MODIFY_RECORD, resourcePermissions, rc_);
    }

    public boolean canView(AuthorizationService authorizationService, BillingAccount rc_) {
        return checkPermissionsCache(authorizationService, Permissions.USE_ACCOUNT, accountPermissions, rc_);
    }

    private boolean checkPermissionsCache(AuthorizationService authorizationService, Permissions p, Map<Long, Permissions> map, Persistable persistable) {
        Permissions per = map.get(persistable.getId());
        if (per == null) {
            if (p == Permissions.VIEW_ALL && authorizationService.canView(user, persistable)) {
                per = Permissions.VIEW_ALL;
                map.put(persistable.getId(), p);
            }
            if (p == Permissions.MODIFY_RECORD && authorizationService.canModifyPermissions(user, (Resource) persistable)) {
                per = Permissions.MODIFY_RECORD;
                map.put(persistable.getId(), p);
            }
            if (p == Permissions.MODIFY_METADATA && authorizationService.canEdit(user, persistable)) {
                per = Permissions.MODIFY_METADATA;
                map.put(persistable.getId(), p);
            }
        }

        if (per != null && per.getEffectivePermissions() >= p.getEffectivePermissions()) {
            return true;
        }
        return false;
    }

    public <R extends PResource> void addToResourceCache(R r) {
        resourceCache.put(r.getId(), r);
    }

    public void addToCollectionCache(PResourceCollection r) {
        collectionCache.put(r.getId(), r);
    }

    public <R extends PCreator> void addToCreatorCache(R r) {
        creatorCache.put(r.getId(), r);
    }

    public void addToAccountCache(PBillingAccount r) {
        accountCache.put(r.getId(), r);
    }

    public PCreator getFromCreatorCache(Long id) {
        return creatorCache.get(id);
    }

    public PResourceCollection getFromCollectionCache(Long id) {
        return getFronCache(collectionCache, id);

    }

    public PResource getFromResourceCache(Long id) {
        return getFronCache(resourceCache, id);
    }

    private <T> T getFronCache(Map<Long, T> cache, Long id) {
        if (PersistableUtils.isNullOrTransient(id)) {
            return null;
        }
        return cache.get(id);
    }

    public PBillingAccount getFromAccountCache(Long id) {
        return getFronCache(accountCache, id);
    }

}
