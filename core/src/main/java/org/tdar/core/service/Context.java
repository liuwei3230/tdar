package org.tdar.core.service;

import java.util.HashMap;
import java.util.Map;

import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.entity.permissions.Permissions;

public class Context {

    private TdarUser user;
    private Map<Long, Permissions> collectionPermissions = new HashMap<>();
    private Map<Long, Permissions> resourcePermissions = new HashMap<>();
    private boolean admin = false;
    private boolean ableToSeeConfidentialFiles = false;
    private boolean institutionEmailObfuscated = false;
    private boolean personEmailObfuscated = false;
    private boolean latLongObfuscated = false;
    private boolean mappedMetadataIncluded;

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

    public void setLatLongObfuscated(boolean latLongObfuscated) {
        this.latLongObfuscated = latLongObfuscated;
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

    public boolean isLatLongObfuscated() {
        return latLongObfuscated;
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
}
