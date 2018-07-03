/**
 * $Id$
 * 
 * @author $Author$
 * @version $Revision$
 */
package org.tdar.core.serialize.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.DeHydratable;
import org.tdar.core.bean.DisplayOrientation;
import org.tdar.core.bean.Editable;
import org.tdar.core.bean.HasName;
import org.tdar.core.bean.HasStatus;
import org.tdar.core.bean.HasSubmitter;
import org.tdar.core.bean.Hideable;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.OaiDcProvider;
import org.tdar.core.bean.Slugable;
import org.tdar.core.bean.SortOption;
import org.tdar.core.bean.Sortable;
import org.tdar.core.bean.Updatable;
import org.tdar.core.bean.Validatable;
import org.tdar.core.bean.XmlLoggable;
import org.tdar.core.bean.resource.Addressable;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.bean.util.UrlUtils;
import org.tdar.core.serialize.entity.PAuthorizedUser;
import org.tdar.core.serialize.entity.PTdarUser;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.TitleSortComparator;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;
import org.tdar.utils.json.JsonLookupFilter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author Adam Brin
 * 
 *         Resource Collections serve a number of purposes:
 *         - they manage rights
 *         - they organize resources
 *         The combination enables us to manage all access rights and permissions for resources through the user of these collections.
 * 
 *         <b>INTERNAL</b> collections enable access rights to a specific resource. Users never see these, they simply see the rights on the resource.
 *         <b>SHARED</b> collections are ones that users create and enable access. Shared collections can be public or private
 *         <b>PUBLIC</b> collections do not store rights and can be used for bookmarks and such things (not fully implemented).
 * 
 *         The Tree structure that is represented is a hybrid of a "materialized path" implementation -- see
 *         http://vadimtropashko.wordpress.com/2008/08/09/one-more-nested-intervals-vs-adjacency-list-comparison/.
 *         It's however, optimized so that the node's children are manifested in a supporting table to optimize rights queries, which will be the most common
 *         lookup.
 */
@XmlType(name = "Pcollection")
@JsonIgnoreProperties(ignoreUnknown = true, allowGetters = true)
@JsonInclude(value = Include.NON_NULL)
@XmlRootElement(name = "PresourceCollection")
public class PResourceCollection extends AbstractPersistable
        implements DeHydratable, XmlLoggable, HasStatus, Sortable,
        OaiDcProvider, HasName, Slugable, Addressable, Indexable, Editable, Hideable, Comparable<PResourceCollection> {

    public static final SortOption DEFAULT_SORT_OPTION = SortOption.TITLE;

    private transient boolean editable = false;

    public PResourceCollection(String title, String description, boolean hidden, SortOption sortOption, DisplayOrientation displayOrientation,
            PTdarUser creator) {
        setName(title);
        setDescription(description);
        setHidden(hidden);
        setSortBy(sortOption);
        setOrientation(displayOrientation);
        setOwner(creator);
    }

    public PResourceCollection(Long id, String title, String description, SortOption sortOption, boolean hidden) {
        setId(id);
        setName(title);
        setDescription(description);
        setHidden(hidden);
        setSortBy(sortOption);

    }

    public PResourceCollection(String title, String description, PTdarUser submitter) {
        setName(title);
        setDescription(description);
        setHidden(false);
        this.setOwner(submitter);
        setSortBy(SortOption.TITLE);
        setOrientation(DisplayOrientation.LIST);
    }

    public PResourceCollection(PResource document, PTdarUser tdarUser) {
        markUpdated(tdarUser);
        getManagedResources().add(document);
        setHidden(false);
        setSortBy(SortOption.TITLE);
        setOrientation(DisplayOrientation.LIST);
    }

    public PResourceCollection() {
        setSortBy(SortOption.TITLE);
        setOrientation(DisplayOrientation.LIST);
    }

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());
    private transient boolean changesNeedToBeLogged = false;

    private Boolean systemManaged = Boolean.FALSE;
    private Boolean verified = Boolean.FALSE;
    @JsonView(JsonLookupFilter.class)
    private Status status = Status.ACTIVE;
    private Set<PAuthorizedUser> authorizedUsers = new LinkedHashSet<PAuthorizedUser>();
    private PTdarUser owner;
    private PTdarUser updater;
    private Date dateCreated;

    private transient TreeSet<PResourceCollection> transientChildren = new TreeSet<>(new TitleSortComparator());
    private Date dateUpdated;
    @XmlTransient
    private Set<PCollectionRevisionLog> collectionRevisionLog = new HashSet<>();
    private Set<Long> resourceIds = new HashSet<>();
    private Set<Long> unmanagedResourceIds = new HashSet<>();

    private transient boolean created;

    private SortOption sortBy = DEFAULT_SORT_OPTION;
    private SortOption secondarySortBy;
    private DisplayOrientation orientation = DisplayOrientation.LIST;
    private PCollectionDisplayProperties properties;

    public PCollectionDisplayProperties getProperties() {
        return properties;
    }

    public void setProperties(PCollectionDisplayProperties properties) {
        this.properties = properties;
    }

    @JsonView(JsonLookupFilter.class)
    private String name;
    private Set<Long> parentIds = new HashSet<>();
    private Set<Long> alternateParentIds = new HashSet<>();
    private String description;
    private String formattedDescription;
    private boolean hidden = false;
    private Set<PResource> managedResources = new LinkedHashSet<PResource>();
    private Set<PResource> unmanagedResources = new LinkedHashSet<PResource>();

    private PResourceCollection parent;
    private PResourceCollection alternateParent;

    @XmlTransient
    public Set<PResource> getManagedResources() {
        return managedResources;
    }

    public void setManagedResources(Set<PResource> resources) {
        this.managedResources = resources;
    }

    /*
     * Get all of the resource collections via a tree (actually list of lists)
     */
@XmlTransient
    // infinite loop because parentTree[0]==self
    public List<PResourceCollection> getHierarchicalResourceCollections() {
        ArrayList<PResourceCollection> parentTree = new ArrayList<>();
        parentTree.add((PResourceCollection) this);
        PResourceCollection collection = (PResourceCollection) this;
        while (collection.getParent() != null) {
            collection = (PResourceCollection) collection.getParent();
            parentTree.add(0, collection);
        }
        return parentTree;
    }

    /*
     * Default to sorting by name, but grouping by parentId, used for sorting int he tree
     */
    public int compareTo(PResourceCollection o) {
        List<String> tree = getParentNameList();
        List<String> tree_ = o.getParentNameList();
        while (!tree.isEmpty() && !tree_.isEmpty() && (tree.get(0) == tree_.get(0))) {
            tree.remove(0);
            tree_.remove(0);
        }
        if (tree.isEmpty()) {
            return -1;
        } else if (tree_.isEmpty()) {
            return 1;
        } else {
            return tree.get(0).compareTo(tree_.get(0));
        }
    }

    @XmlTransient
    public List<String> getParentNameList() {
        ArrayList<String> parentNameTree = new ArrayList<String>();
        for (PResourceCollection collection : getHierarchicalResourceCollections()) {
            parentNameTree.add(collection.getName());
        }
        return parentNameTree;
    }

    @XmlTransient
    public List<PResourceCollection> getVisibleParents() {
        List<PResourceCollection> hierarchicalResourceCollections = getHierarchicalResourceCollections();
        Iterator<PResourceCollection> iterator = hierarchicalResourceCollections.iterator();
        while (iterator.hasNext()) {
            PResourceCollection collection = iterator.next();
            if (!(PResourceCollection.class.isAssignableFrom(collection.getClass())) || !collection.isHidden()) {
                iterator.remove();
            }
        }
        return hierarchicalResourceCollections;
    }

    @XmlAttribute(name = "altParentIdRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PResourceCollection getAlternateParent() {
        return alternateParent;
    }

    public void setAlternateParent(PResourceCollection alternateParent) {
        this.alternateParent = alternateParent;
    }

    public void copyImmutableFieldsFrom(PResourceCollection resource) {
        this.setDateCreated(resource.getDateCreated());
        this.setOwner(resource.getOwner());
        this.setAuthorizedUsers(new HashSet<>(resource.getAuthorizedUsers()));
        this.setSystemManaged(resource.isSystemManaged());
        ((PResourceCollection) this).getManagedResources().addAll(((PResourceCollection) resource).getManagedResources());
        this.setParent(resource.getParent());
    }

    @XmlAttribute(name = "parentIdRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PResourceCollection getParent() {
        return parent;
    }

    public void setParent(PResourceCollection parent) {
        this.parent = parent;
    }

    @XmlTransient
    public Set<PResource> getUnmanagedResources() {
        return unmanagedResources;
    }

    public void setUnmanagedResources(Set<PResource> unmanagedResources) {
        this.unmanagedResources = unmanagedResources;
    }

    @XmlAttribute
    @Override
    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean visible) {
        this.hidden = visible;
    }

    @Override
    @JsonView(JsonLookupFilter.class)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trimToEmpty(name);
    }

    @Override
    @JsonView(JsonLookupFilter.class)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimToEmpty(description);
    }

    @Override
    public String getTitle() {
        return getName();
    }

    public String getFormattedDescription() {
        return formattedDescription;
    }

    public void setFormattedDescription(String adminDescription) {
        this.formattedDescription = adminDescription;
    }

    @JsonView(JsonLookupFilter.class)
    public String getDetailUrl() {
        return String.format("/%s/%s/%s", getUrlNamespace(), getId(), getSlug());
    }

    @Override
    public String getSlug() {
        return UrlUtils.slugify(getName());
    }

    public boolean isSupportsThumbnails() {
        return false;
    }

    @XmlTransient
    public boolean isVisibleAndActive() {
        if (hidden) {
            return false;
        }
        if (getStatus() != Status.ACTIVE) {
            return false;
        }
        return true;
    }

    /**
     * @param sortBy
     *            the sortBy to set
     */
    public void setSortBy(SortOption sortBy) {
        this.sortBy = sortBy;
    }

    /**
     * @return the sortBy
     */
    @Override
    public SortOption getSortBy() {
        return sortBy;
    }

    public DisplayOrientation getOrientation() {
        return orientation;
    }

    public void setOrientation(DisplayOrientation orientation) {
        this.orientation = orientation;
    }

    public SortOption getSecondarySortBy() {
        return secondarySortBy;
    }

    public void setSecondarySortBy(SortOption secondarySortBy) {
        this.secondarySortBy = secondarySortBy;
    }

    // @XmlTransient
    @XmlElementWrapper(name = "authorizedUsers")
    @XmlElement(name = "authorizedUser")
    public Set<PAuthorizedUser> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(Set<PAuthorizedUser> users) {
        this.authorizedUsers = users;
    }

    @XmlAttribute(name = "ownerIdRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PTdarUser getOwner() {
        return owner;
    }

    public void setOwner(PTdarUser owner) {
        this.owner = owner;
    }

    public void markUpdated(PTdarUser p) {
        if (getOwner() == null) {
            setOwner(p);
        }
        if (getDateCreated() == null) {
            setDateCreated(new Date());
        }
        setUpdater(p);
        setDateUpdated(new Date());

    }

    /**
     * @param dateCreated
     *            the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * @return the dateCreated
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    @Override
    public String toString() {
        String own = "no owner -1";
        if (owner != null) {
            own = owner.getProperName() + " " + owner.getId();
        }
        return String.format("%s | collection %s  (creator: %s)", getName(), getId(), own);
    }

    public PTdarUser getSubmitter() {
        return owner;
    }

    @Override
    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @XmlAttribute(name = "updaterIdRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PTdarUser getUpdater() {
        return updater;
    }

    public void setUpdater(PTdarUser updater) {
        this.updater = updater;
    }

    @XmlTransient
    public boolean isChangesNeedToBeLogged() {
        return changesNeedToBeLogged;
    }

    public void setChangesNeedToBeLogged(boolean changesNeedToBeLogged) {
        this.changesNeedToBeLogged = changesNeedToBeLogged;
    }

    /**
     * Sort-of hack to support saving of massive resource collections -- the select that is generated for getResources() does a polymorphic deep dive for every
     * field when it only really needs to get at the Ids for proper logging.
     * 
     * @return
     */
    @XmlElementWrapper(name = "resources")
    @XmlElement(name = "resourceId")
    public Set<Long> getResourceIds() {
        return resourceIds;
    }

    public void setResourceIds(Set<Long> resourceIds) {
        this.resourceIds = resourceIds;
    }

    public String getUrlNamespace() {
        return "collection";
    }

    @XmlTransient
    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public Set<PCollectionRevisionLog> getCollectionRevisionLog() {
        return collectionRevisionLog;
    }

    public void setCollectionRevisionLog(Set<PCollectionRevisionLog> collectionRevisionLog) {
        this.collectionRevisionLog = collectionRevisionLog;

    }

    @XmlTransient
    @JsonIgnore
    public boolean isNew() {
        if (getDateCreated() == null) {
            return false;
        }

        if (DateTime.now().minusDays(7).isBefore(getDateCreated().getTime())) {
            return true;
        }
        return false;
    }

    @XmlAttribute(required = false)
    public Boolean isSystemManaged() {
        if (systemManaged == null) {
            systemManaged = false;
        }
        return systemManaged;
    }

    public void setSystemManaged(Boolean systemManaged) {
        this.systemManaged = systemManaged;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    @XmlTransient
    public boolean isDeleted() {
        return status == Status.DELETED;
    }

    @Override
    @XmlTransient
    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    @Override
    @XmlTransient
    public boolean isDraft() {
        return status == Status.DRAFT;
    }

    @Override
    public boolean isDuplicate() {
        return status == Status.DUPLICATE;
    }

    @Override
    @XmlTransient
    public boolean isFlagged() {
        return status == Status.FLAGGED;
    }

    public void setParentIds(Set<Long> parentIds) {
        this.parentIds = parentIds;
    }

    
    @XmlTransient
    public Set<Long> getParentIds() {
        return parentIds;
    }

    
    @XmlTransient
    public Set<Long> getAlternateParentIds() {
        return alternateParentIds;
    }

    public void setAlternateParentIds(Set<Long> alternateParentIds) {
        this.alternateParentIds = alternateParentIds;
    }

    @XmlTransient
    
    public Long getParentId() {
        if (getParent() == null) {
            return null;
        }
        return getParent().getId();
    }

    
    public Long getAlternateParentId() {
        if (getAlternateParent() == null) {
            return null;
        }
        return getAlternateParent().getId();
    }

    @XmlTransient
    
    public TreeSet<PResourceCollection> getTransientChildren() {
        return transientChildren;
    }

    public void setTransientChildren(TreeSet<PResourceCollection> transientChildren) {
        this.transientChildren = transientChildren;
    }

    public Collection<String> getAlternateParentNameList() {
        HashSet<String> names = new HashSet<>();
        if (PersistableUtils.isNotNullOrTransient(getAlternateParent())) {
            PResourceCollection hierarchicalCollection = getAlternateParent();
            if (PersistableUtils.isNotNullOrTransient(hierarchicalCollection.getParent())) {
                names.addAll(hierarchicalCollection.getParentNameList());
            }
            if (PersistableUtils.isNotNullOrTransient(hierarchicalCollection.getAlternateParent())) {
                names.addAll(hierarchicalCollection.getAlternateParentNameList());
            }
        }
        return names;
    }

    @XmlTransient
    
    public boolean isSubCollection() {
        return !isTopLevel();
    }

    @XmlTransient
    
    public boolean isTopLevel() {
        if ((getParent() == null) || (getParent().isHidden() == true)) {
            return true;
        }
        return false;
    }

    @XmlElementWrapper(name = "unmanagedResources")
    @XmlElement(name = "unmanagedResourceId")
    public Set<Long> getUnmanagedResourceIds() {
        return unmanagedResourceIds;
    }

    
    public void setUnmanagedResourceIds(Set<Long> unmanagedResourceIds) {
        this.unmanagedResourceIds = unmanagedResourceIds;
    }

    
    public int getSize() {
        return CollectionUtils.size(managedResources) + CollectionUtils.size(unmanagedResources);
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Override
    
    @XmlTransient
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    
    
    @XmlTransient
    public boolean isCollection() {
        return true;
    }
}
