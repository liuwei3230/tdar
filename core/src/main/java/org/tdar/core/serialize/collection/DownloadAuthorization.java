package org.tdar.core.serialize.collection;

import java.beans.Transient;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

import org.tdar.core.bean.AbstractPersistable;

/**
 * A downloadauth designates a collection of resources that allow file downloads from unauthenticated users. Instead, the download request includes
 * an API key that effectively serves as an alternate form of authentication.
 */
@XmlRootElement(name = "DownloadAuthorization")
public class DownloadAuthorization extends AbstractPersistable {

    private static final int UUID_BEGIN_INDEX = 24;

    private String apiKey;
    private Set<String> refererHostnames = new LinkedHashSet<>();
    private PResourceCollection sharedCollection;

    private static String generateSimpleKey() {
        return "d" + UUID.randomUUID().toString().substring(UUID_BEGIN_INDEX);
    };

    // zero-arg constructor for hibernate (does not generate key)
    public DownloadAuthorization() {
    }

    /**
     * Create new downloadAuthorization w/ default api key for the specified resourceCollection.
     * 
     * @param resourceCollection
     */
    public DownloadAuthorization(PResourceCollection resourceCollection) {
        this(resourceCollection, generateSimpleKey());
    }

    /**
     * Create new downloadAuth with specified resourceCollection & key
     * 
     * @param resourceCollection
     * @param apiKey
     */
    public DownloadAuthorization(PResourceCollection resourceCollection, String apiKey) {
        this.sharedCollection = resourceCollection;
        this.apiKey = apiKey;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public PResourceCollection getSharedCollection() {
        return sharedCollection;
    }

    public void setSharedCollection(PResourceCollection resourceCollection) {
        this.sharedCollection = resourceCollection;
    }

    public Set<String> getRefererHostnames() {
        return refererHostnames;
    }

    public void setRefererHostnames(Set<String> refererHostnames) {
        this.refererHostnames = refererHostnames;
    }

    /**
     * Returns true if downloads are supported for requests from any referrer. The convention for this policy is for a
     * DownloadAuthorization object to have a single referrerHostname with a value of '*' (the asterisk symbol).
     * 
     * @return
     */
    public boolean isAnyReferrerAllowed() {
        // FIXME: needs implementation
        return false;
    }

    public boolean isReferrerAllowed(String referrerHostname) {
        return isAnyReferrerAllowed() || refererHostnames.contains(referrerHostname);
    }

}
