package org.tdar.core.bean.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.tdar.core.bean.DisplayOrientation;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.HasName;
import org.tdar.core.bean.SortOption;
import org.tdar.core.bean.entity.TdarUser;
import org.tdar.core.bean.resource.Resource;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.TitleSortComparator;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

@DiscriminatorValue(value = "LIST")
@Entity
@XmlRootElement(name = "listCollection")
public class ListCollection extends VisibleCollection implements Comparable<ListCollection>, HasName {

    private static final long serialVersionUID = 1225586588061994193L;

//    @ManyToOne
//    @JoinColumn(name = "parent_id")
//    private ListCollection parent;
//
//    @ManyToOne
//    @JoinColumn(name = "alternate_parent_id")
//    private ListCollection alternateParent;
//
//
//    public void setParent(ListCollection parent) {
//        this.parent = parent;
//    }

    @XmlTransient
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "unmanagedResourceCollections", targetEntity = Resource.class)
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL, region = "org.tdar.core.bean.collection.ResourceCollection.unmanagedResources")
    private Set<Resource> unmanagedResources = new LinkedHashSet<Resource>();


    public ListCollection() {
//        setProperties(new CollectionDisplayProperties(false,false,false,false,false,false));
        setType(CollectionType.LIST);
    }
    
    public ListCollection(Long id, String title, String description, SortOption sortBy, boolean hidden) {
        setId(id);
        setName(title);
        setDescription(description);
        setSortBy(sortBy);
        setHidden(hidden);
        this.setType(CollectionType.LIST);
//        setProperties(new CollectionDisplayProperties(false,false,false,false,false,false));
    }

    public ListCollection(String name, String description, SortOption sort, boolean hidden, TdarUser basicUser) {
        setName(name);
        setDescription(description);
        setSortBy(sort);
        setHidden(hidden);
        this.setType(CollectionType.LIST);
        setOwner(basicUser);
//        setProperties(new CollectionDisplayProperties(false,false,false,false,false,false));
    }
//
//
//    @ElementCollection()
//    @CollectionTable(name = "collection_parents", joinColumns = @JoinColumn(name = "collection_id"))
//    @Column(name = "parent_id")
//    private Set<Long> parentIds = new HashSet<>();
//    
//    @ElementCollection()
//    @CollectionTable(name = "collection_alternate_parents", joinColumns = @JoinColumn(name = "collection_id") )
//    @Column(name = "parent_id")
//    private Set<Long> alternateParentIds = new HashSet<>();
//
//
//    /**
//     * Get ordered list of parents (ids) of this resources ... great grandfather, grandfather, father.
//     * 
//     * Note: in earlier implementations this contained the currentId as well, I've removed this, but am unsure
//     * whether it should be there
//     */
//    @Transient
//    @ElementCollection
//    @XmlTransient
//    public Set<Long> getParentIds() {
//        return parentIds;
//    }
//
//    public void setParentIds(Set<Long> parentIds) {
//        this.parentIds = parentIds;
//    }

    private transient TreeSet<ListCollection> transientChildren = new TreeSet<>(new TitleSortComparator());

//
//    @XmlAttribute(name = "parentIdRef")
//    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
//    public ListCollection getParent() {
//        return parent;
//    }
//
//

    @XmlTransient
    @Transient
    public TreeSet<ListCollection> getTransientChildren() {
        return transientChildren;
    }

    public void setTransientChildren(TreeSet<ListCollection> transientChildren) {
        this.transientChildren = transientChildren;
    }
//
//    @XmlTransient
//    @Transient
//    public boolean isTopCollection() {
//        return getParent() == null;
//    }
//
//    @XmlTransient
//    @Transient
//    public boolean isSubCollection() {
//        return getParent() != null;
//    }
//
//    @SuppressWarnings({ "unchecked", "hiding" })
//    public <C extends HierarchicalCollection> List<ListCollection> getHierarchicalResourceCollections(Class<ListCollection> class1, ListCollection collection_) {
//        ArrayList<ListCollection> parentTree = new ArrayList<>();
//        parentTree.add(collection_);
//        ListCollection collection = collection_;
//        while (collection.getParent() != null) {
//            collection = (ListCollection) collection.getParent();
//            parentTree.add(0, collection);
//        }
//        return parentTree;
//    }
//
//    @XmlTransient
//    @Transient
//    public List<String> getParentNameList() {
//        ArrayList<String> parentNameTree = new ArrayList<String>();
//        for (ListCollection collection : getHierarchicalResourceCollections()) {
//            parentNameTree.add(collection.getName());
//        }
//        return parentNameTree;
//    }
//
//
//    public List<ListCollection> getVisibleParents(Class<ListCollection> type) {
//        List<ListCollection> hierarchicalResourceCollections = getHierarchicalResourceCollections();
//        Iterator<ListCollection> iterator = hierarchicalResourceCollections.iterator();
//        while (iterator.hasNext()) {
//            ListCollection collection = iterator.next();
//            if (!(type.isAssignableFrom(collection.getClass())) || !collection.isHidden()) {
//                iterator.remove();
//            }
//        }
//        return hierarchicalResourceCollections;
//    }
//
//    @XmlTransient
//    @Transient
//    public boolean isTopLevel() {
//        if ((getParent() == null) || (getParent().isHidden() == true)) {
//            return true;
//        }
//        return false;
//    }
//
//    @XmlTransient
//    @Transient
//    public Long getParentId() {
//        if (getParent() == null) {
//            return null;
//        }
//        return getParent().getId();
//    }
//
//    @Transient
//    public Long getAlternateParentId() {
//        if (getAlternateParent() == null) {
//            return null;
//        }
//        return getAlternateParent().getId();
//    }
//
//
//    /*
//     * Default to sorting by name, but grouping by parentId, used for sorting int he tree
//     */
//    public  int compareTo(ListCollection self, ListCollection o) {
//        List<String> tree = self.getParentNameList();
//        List<String> tree_ = o.getParentNameList();
//        while (!tree.isEmpty() && !tree_.isEmpty() && (tree.get(0) == tree_.get(0))) {
//            tree.remove(0);
//            tree_.remove(0);
//        }
//        if (tree.isEmpty()) {
//            return -1;
//        } else if (tree_.isEmpty()) {
//            return 1;
//        } else {
//            return tree.get(0).compareTo(tree_.get(0));
//        }
//    }
//    
//    @Transient
//    @ElementCollection
//    public Set<Long> getAlternateParentIds() {
//        return alternateParentIds;
//    }
//
//    public void setAlternateParentIds(Set<Long> alternateParentIds) {
//        this.alternateParentIds = alternateParentIds;
//    }
//
//    public Collection<String> getAlternateParentNameList() {
//        HashSet<String> names = new HashSet<>();
//        if (PersistableUtils.isNotNullOrTransient(getAlternateParent())) {
//            if (getAlternateParent() instanceof ListCollection) {
//                ListCollection hierarchicalCollection = (ListCollection) getAlternateParent();
//                if (PersistableUtils.isNotNullOrTransient(hierarchicalCollection.getParent())) {
//                    names.addAll(hierarchicalCollection.getParentNameList());
//                }
//                if (PersistableUtils.isNotNullOrTransient(hierarchicalCollection.getAlternateParent())) {
//                    names.addAll(hierarchicalCollection.getAlternateParentNameList());
//                }
//            }
//        }
//        return names;
//    }
    @XmlTransient
    public Set<Resource> getUnmanagedResources() {
        return unmanagedResources;
    }

    public void setUnmanagedResources(Set<Resource> publicResources) {
        this.unmanagedResources = publicResources;
    }
//
//
//    /*
//     * Get all of the resource collections via a tree (actually list of lists)
//     */
//    @Transient
//    @XmlTransient
//    // infinite loop because parentTree[0]==self
//    public List<ListCollection> getHierarchicalResourceCollections() {
//        return getHierarchicalResourceCollections(ListCollection.class, this);
//    }
//
//    @Transient
//    @XmlTransient
//    public List<ListCollection> getVisibleParents() {
//        return getVisibleParents(ListCollection.class);
//    }


    @Enumerated(EnumType.STRING)
    @Column(name = "sort_order", length = FieldLength.FIELD_LENGTH_25)
    private SortOption sortBy = DEFAULT_SORT_OPTION;

    @Enumerated(EnumType.STRING)
    @Column(name = "secondary_sort_order", length = FieldLength.FIELD_LENGTH_25)
    private SortOption secondarySortBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "orientation", length = FieldLength.FIELD_LENGTH_50)
    private DisplayOrientation orientation = DisplayOrientation.LIST;
//
//    @Embedded
//    @AttributeOverrides({
//            @AttributeOverride(name = "whitelabel", column = @Column(table = "whitelabel_collection")),
//            @AttributeOverride(name = "custom_header_enabled", column = @Column(table = "whitelabel_collection", columnDefinition="boolean default false")),
//            @AttributeOverride(name = "custom_doc_logo_enabled", column = @Column(table = "whitelabel_collection", columnDefinition="boolean default false")),
//            @AttributeOverride(name = "featured_resources_enabled", column = @Column(table = "whitelabel_collection", columnDefinition="boolean default false")),
//            @AttributeOverride(name = "search_enabled", column = @Column(table = "whitelabel_collection", columnDefinition="boolean default false")),
//            @AttributeOverride(name = "sub_collections_enabled", column = @Column(table = "whitelabel_collection", columnDefinition="boolean default false")),
//            @AttributeOverride(name = "subtitle", column = @Column(table = "whitelabel_collection")),
//            @AttributeOverride(name = "css", column = @Column(table = "whitelabel_collection"))
//    })
//    @Access(AccessType.FIELD)
//    private CollectionDisplayProperties properties;
//
//    public CollectionDisplayProperties getProperties() {
//        return properties;
//    }
//
//    public void setProperties(CollectionDisplayProperties properties) {
//        this.properties = properties;
//    }

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

    
    @Override
    public boolean isValid() {
        if (isValidForController()) {
            if ((getType() == CollectionType.LIST) && (getSortBy() == null)) {
                return false;
            }
            return super.isValid();
        }
        return false;
    }
//
//    @XmlAttribute(name = "altParentIdRef")
//    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
//    public ListCollection getAlternateParent() {
//        return alternateParent;
//    }
//
//    public void setAlternateParent(ListCollection alternateParent) {
//        this.alternateParent = alternateParent;
//    }
//
//    @Override
//    public int compareTo(ListCollection o) {
//        return compareTo(this, o);
//    }

    @Override
    public int compareTo(ListCollection o) {
        // TODO Auto-generated method stub
        return 0;
    }


}
