package org.tdar.core.serialize.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.DeHydratable;
import org.tdar.core.bean.Editable;
import org.tdar.core.bean.HasName;
import org.tdar.core.bean.HasStatus;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.OaiDcProvider;
import org.tdar.core.bean.Persistable;
import org.tdar.core.bean.Slugable;
import org.tdar.core.bean.XmlLoggable;
import org.tdar.core.bean.billing.BillingAccount;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.bean.entity.ResourceCreatorRoleType;
import org.tdar.core.bean.resource.Addressable;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.Status;
import org.tdar.core.bean.util.UrlUtils;
import org.tdar.core.serialize.citation.PRelatedComparativeCollection;
import org.tdar.core.serialize.citation.PSourceCollection;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.coverage.PCoverageDate;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.serialize.entity.PAuthorizedUser;
import org.tdar.core.serialize.entity.PCreator;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.entity.PTdarUser;
import org.tdar.core.serialize.keyword.PCultureKeyword;
import org.tdar.core.serialize.keyword.PGeographicKeyword;
import org.tdar.core.serialize.keyword.PInvestigationType;
import org.tdar.core.serialize.keyword.PKeyword;
import org.tdar.core.serialize.keyword.PMaterialKeyword;
import org.tdar.core.serialize.keyword.POtherKeyword;
import org.tdar.core.serialize.keyword.PSiteNameKeyword;
import org.tdar.core.serialize.keyword.PSiteTypeKeyword;
import org.tdar.core.serialize.keyword.PTemporalKeyword;
import org.tdar.core.serialize.keyword.SuggestedKeyword;
import org.tdar.utils.MathUtils;
import org.tdar.utils.MessageHelper;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.jaxb.converters.JAXBPersistableRef;
import org.tdar.utils.jaxb.converters.JaxbPResourceCollectionRefConverter;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;
import org.tdar.utils.json.JsonIdNameFilter;
import org.tdar.utils.json.JsonIntegrationFilter;
import org.tdar.utils.json.JsonIntegrationSearchResultFilter;
import org.tdar.utils.json.JsonLookupFilter;
import org.tdar.utils.json.JsonProjectLookupFilter;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * $Id$
 * 
 * Contains metadata common to all Resources.
 * 
 * Projects, Datasets, Documents, CodingSheets, Ontologies, SensoryData
 * 
 * @author <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
@XmlRootElement
@XmlSeeAlso({ PDocument.class, PInformationResource.class, PProject.class, PCodingSheet.class, PDataset.class, POntology.class,
        PImage.class, PSensoryData.class, PVideo.class, PGeospatial.class, PArchive.class, PAudio.class })
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "Presource", propOrder = {})
@XmlTransient
@JsonAutoDetect(getterVisibility=Visibility.PUBLIC_ONLY)
public class PResource implements Persistable,
        Comparable<PResource>, HasName, Indexable, 
        HasStatus, OaiDcProvider, ConfidentialViewable, Addressable,
        DeHydratable, XmlLoggable, Slugable, Editable {

    public static final String RESOURCE_COLLECTIONS = "resourceCollections";
    public static final String MANAGED_RESOURCE_COLLECTIONS = "managedResourceCollections";


    private transient boolean obfuscated = false;
    private transient boolean bookmarked = false;
    private transient Boolean obfuscatedObjectDifferent = false;
    private transient Boolean statusChanged = Boolean.FALSE;
    private transient boolean viewable;
    private transient boolean editable;
    private transient boolean viewConfidential;
    private transient Long transientAccessCount;
    protected final static transient Logger logger = LoggerFactory.getLogger(PResource.class);


    public PResource() {
    }

    private Long spaceInBytesUsed = 0L;
    private Long filesUsed = 0L;

    private transient Long previousSpaceInBytesUsed = 0L;
    private transient Long previousFilesUsed = 0L;
    private transient boolean countedInBillingEvaluation = true;

    @Deprecated
    public PResource(Long id, String title) {
        setId(id);
        setTitle(title);
    }

    @Deprecated
    public PResource(Long id, String title, ResourceType type) {
        this(id, title);
        setResourceType(type);
    }

    public PResource(Long id, String title, ResourceType resourceType, String description, Status status) {
        this(id, title, resourceType);
        setDescription(description);
        setStatus(status);
    }

    /**
     * Instantiate a "sparse" resource object instance that has a very limited number of populated fields. This is
     * useful in the context of displaying summary information about a collection of resources. You should not
     * attempt to persist objects created using this constructor.
     * 
     * @param id
     * @param title
     * @param resourceType
     * @param status
     * @param submitterId
     */
    public PResource(Long id, String title, ResourceType resourceType, Status status, Long submitterId) {
        this(id, title, resourceType);
        this.status = status;
        PTdarUser submitter = new PTdarUser();
        submitter.setId(submitterId);
        this.submitter = submitter;
    }

    @JsonView(JsonLookupFilter.class)
    private Long id = -1L;
    @JsonView(JsonIdNameFilter.class)
    private String title;
    @JsonView(JsonLookupFilter.class)
    private String description;
    private String formattedDescription;

    @JsonView({ JsonLookupFilter.class, JsonIntegrationSearchResultFilter.class })
    private Date dateCreated;

    @JsonView(JsonLookupFilter.class)
    private String url;

    @JsonView(JsonLookupFilter.class)
    private ResourceType resourceType;

    @JsonView(JsonLookupFilter.class)
    private Status status = Status.ACTIVE;

    private Status previousStatus = Status.ACTIVE;
    private PTdarUser submitter;
    private PTdarUser uploader;
    private PTdarUser updatedBy;
    private Date dateUpdated;
    private Set<PResourceCreator> resourceCreators = new LinkedHashSet<PResourceCreator>();
    private Set<PResourceNote> resourceNotes = new LinkedHashSet<PResourceNote>();
    private Set<PResourceAnnotation> resourceAnnotations = new LinkedHashSet<PResourceAnnotation>();
    private Set<PSourceCollection> sourceCollections = new LinkedHashSet<PSourceCollection>();
    private Set<PRelatedComparativeCollection> relatedComparativeCollections = new LinkedHashSet<PRelatedComparativeCollection>();
    private Set<PLatitudeLongitudeBox> latitudeLongitudeBoxes = new LinkedHashSet<PLatitudeLongitudeBox>();
    private Set<PGeographicKeyword> geographicKeywords = new LinkedHashSet<PGeographicKeyword>();
    private Set<PGeographicKeyword> managedGeographicKeywords = new LinkedHashSet<PGeographicKeyword>();
    private Set<PTemporalKeyword> temporalKeywords = new LinkedHashSet<PTemporalKeyword>();
    private Set<PCoverageDate> coverageDates = new LinkedHashSet<PCoverageDate>();
    private Set<PCultureKeyword> cultureKeywords = new LinkedHashSet<PCultureKeyword>();
    private Set<POtherKeyword> otherKeywords = new LinkedHashSet<POtherKeyword>();
    private Set<PSiteNameKeyword> siteNameKeywords = new LinkedHashSet<PSiteNameKeyword>();
    private Set<PMaterialKeyword> materialKeywords = new LinkedHashSet<PMaterialKeyword>();
    private Set<PInvestigationType> investigationTypes = new LinkedHashSet<PInvestigationType>();
    private Set<PSiteTypeKeyword> siteTypeKeywords = new LinkedHashSet<PSiteTypeKeyword>();
    private Set<PResourceRevisionLog> resourceRevisionLog = new HashSet<PResourceRevisionLog>();
    private Set<PResourceCollection> managedResourceCollections = new LinkedHashSet<>();
    private Set<PResourceCollection> unmanagedResourceCollections = new LinkedHashSet<>();
    private Set<PBookmarkedResource> bookmarkedResources = new LinkedHashSet<>();
    private Set<PAuthorizedUser> authorizedUsers = new LinkedHashSet<PAuthorizedUser>();

    private transient BillingAccount account;

    private transient boolean created = false;
    private transient boolean updated = false;

    private String externalId;

    @XmlElementWrapper(name = "cultureKeywords")
    @XmlElement(name = "cultureKeyword")
    public Set<PCultureKeyword> getCultureKeywords() {
        if (cultureKeywords == null) {
            this.cultureKeywords = new LinkedHashSet<PCultureKeyword>();
        }
        return cultureKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PCultureKeyword> getActiveCultureKeywords() {
        return getCultureKeywords();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PResourceCreator> getActiveResourceCreators() {
        return getResourceCreators();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PCultureKeyword> getUncontrolledCultureKeywords() {
        return getUncontrolledSuggestedKeyword(getCultureKeywords());
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PMaterialKeyword> getUncontrolledMaterialKeywords() {
        return getUncontrolledSuggestedKeyword(getMaterialKeywords());
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PCultureKeyword> getApprovedCultureKeywords() {
        return getApprovedSuggestedKeyword(getCultureKeywords());
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PMaterialKeyword> getApprovedMaterialKeywords() {
        return getApprovedSuggestedKeyword(getMaterialKeywords());
    }

    public void setCultureKeywords(Set<PCultureKeyword> cultureKeywords) {
        this.cultureKeywords = cultureKeywords;
    }

    @XmlElementWrapper(name = "siteTypeKeywords")
    @XmlElement(name = "siteTypeKeyword")
    public Set<PSiteTypeKeyword> getSiteTypeKeywords() {
        if (siteTypeKeywords == null) {
            this.siteTypeKeywords = new LinkedHashSet<PSiteTypeKeyword>();
        }
        return siteTypeKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PSiteTypeKeyword> getActiveSiteTypeKeywords() {
        return getSiteTypeKeywords();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PSiteTypeKeyword> getUncontrolledSiteTypeKeywords() {
        return getUncontrolledSuggestedKeyword(getSiteTypeKeywords());
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PSiteTypeKeyword> getApprovedSiteTypeKeywords() {
        return getApprovedSuggestedKeyword(getSiteTypeKeywords());
    }

    private <K extends SuggestedKeyword> Set<K> getUncontrolledSuggestedKeyword(
            Collection<K> keywords) {
        Set<K> uncontrolledKeys = new HashSet<K>();
        for (K key : keywords) {
            if (!key.isApproved()) {
                uncontrolledKeys.add(key);
            }
        }
        return uncontrolledKeys;
    }

    private <K extends SuggestedKeyword> Set<K> getApprovedSuggestedKeyword(
            Collection<K> keywords) {
        Set<K> approvedKeys = new HashSet<K>();
        for (K key : keywords) {
            if (key.isApproved()) {
                approvedKeys.add(key);
            }
        }
        return approvedKeys;
    }

    public void setSiteTypeKeywords(Set<PSiteTypeKeyword> siteTypeKeywords) {
        this.siteTypeKeywords = siteTypeKeywords;
    }

    @XmlElementWrapper(name = "otherKeywords")
    @XmlElement(name = "otherKeyword")
    public Set<POtherKeyword> getOtherKeywords() {
        if (otherKeywords == null) {
            otherKeywords = new LinkedHashSet<POtherKeyword>();
        }
        return otherKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<POtherKeyword> getActiveOtherKeywords() {
        return getOtherKeywords();
    }

    public void setOtherKeywords(Set<POtherKeyword> otherKeywords) {
        this.otherKeywords = otherKeywords;
    }

    @XmlElementWrapper(name = "siteNameKeywords")
    @XmlElement(name = "siteNameKeyword")
    public Set<PSiteNameKeyword> getSiteNameKeywords() {
        if (siteNameKeywords == null) {
            siteNameKeywords = new LinkedHashSet<PSiteNameKeyword>();
        }
        return siteNameKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PSiteNameKeyword> getActiveSiteNameKeywords() {
        return getSiteNameKeywords();
    }

    public void setSiteNameKeywords(Set<PSiteNameKeyword> siteNameKeywords) {
        this.siteNameKeywords = siteNameKeywords;
    }

    @XmlElementWrapper(name = "materialKeywords")
    @XmlElement(name = "materialKeyword")
    public Set<PMaterialKeyword> getMaterialKeywords() {
        if (materialKeywords == null) {
            materialKeywords = new LinkedHashSet<PMaterialKeyword>();
        }
        return materialKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PMaterialKeyword> getActiveMaterialKeywords() {
        return getMaterialKeywords();
    }

    public void setMaterialKeywords(Set<PMaterialKeyword> materialKeywords) {
        this.materialKeywords = materialKeywords;
    }

    @XmlElementWrapper(name = "investigationTypes")
    @XmlElement(name = "investigationType")
    public Set<PInvestigationType> getInvestigationTypes() {
        if (investigationTypes == null) {
            investigationTypes = new LinkedHashSet<PInvestigationType>();
        }
        return investigationTypes;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PInvestigationType> getActiveInvestigationTypes() {
        return getInvestigationTypes();
    }

    public String getJoinedInvestigationTypes() {
        return join(getInvestigationTypes());
    }

    public void setInvestigationTypes(Set<PInvestigationType> investigationTypes) {
        this.investigationTypes = investigationTypes;
    }

    @Override
    @XmlAttribute
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = StringUtils.trimToEmpty(title);
    }

    public void setDescription(String description) {
        this.description = StringUtils.trimToEmpty(description);
    }

    @Override
    @JsonView(JsonIntegrationFilter.class)
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateRegistered) {
        this.dateCreated = dateRegistered;
    }

    @XmlAttribute(name = "submitterRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @NotNull
    public PTdarUser getSubmitter() {
        return submitter;
    }

    public void setSubmitter(PTdarUser submitter) {
        this.submitter = submitter;
    }

    @Override
    public String getDescription() {
        return description;
    }

    private String join(Collection<?> keywords) {
        return StringUtils.join(keywords, ", ");
    }

    public String getJoinedCultureKeywords() {
        return join(getCultureKeywords());
    }

    public String getJoinedMaterialKeywords() {
        return join(getMaterialKeywords());
    }

    public String getJoinedOtherKeywords() {
        return join(getOtherKeywords());
    }

    public String getJoinedSiteNameKeywords() {
        return join(getSiteNameKeywords());
    }

    public String getJoinedSiteTypeKeywords() {
        return join(getSiteTypeKeywords());
    }

    @XmlElementWrapper(name = "latitudeLongitudeBoxes")
    @XmlElement(name = "latitudeLongitudeBox")
    public Set<PLatitudeLongitudeBox> getLatitudeLongitudeBoxes() {
        if (latitudeLongitudeBoxes == null) {
            latitudeLongitudeBoxes = new LinkedHashSet<PLatitudeLongitudeBox>();
        }
        return latitudeLongitudeBoxes;
    }

    @JsonView({ JsonProjectLookupFilter.class, JsonLookupFilter.class })
    public Set<PLatitudeLongitudeBox> getActiveLatitudeLongitudeBoxes() {
        return getLatitudeLongitudeBoxes();
    }

    @JsonView(JsonProjectLookupFilter.class)
    @XmlTransient
    @JsonIgnore
    public PLatitudeLongitudeBox getFirstActiveLatitudeLongitudeBox() {
        if (CollectionUtils.isEmpty(getActiveLatitudeLongitudeBoxes())) {
            return null;
        }
        return getActiveLatitudeLongitudeBoxes().iterator().next();
    }

    @XmlTransient
    @JsonIgnore
    public boolean isLatLongVisible() {
        PLatitudeLongitudeBox latLongBox = getFirstActiveLatitudeLongitudeBox();
        if (logger.isTraceEnabled()) {
            logger.trace("{} : hasConfidentialFiles:{}\t latLongBox:{}", getId(), hasConfidentialFiles(), latLongBox);
        }
        if (hasConfidentialFiles() || (latLongBox == null)) {
            logger.trace("latLong for {} is confidential or null", getId());
            return Boolean.FALSE;
        }
        return Boolean.FALSE;
    }

    public PLatitudeLongitudeBox getFirstLatitudeLongitudeBox() {
        if (CollectionUtils.isEmpty(latitudeLongitudeBoxes)) {
            return null;
        }
        return latitudeLongitudeBoxes.iterator().next();
    }

    public void setLatitudeLongitudeBoxes(
            Set<PLatitudeLongitudeBox> latitudeLongitudeBoxes) {
        this.latitudeLongitudeBoxes = latitudeLongitudeBoxes;
    }

    @XmlElementWrapper(name = "geographicKeywords")
    @XmlElement(name = "geographicKeyword")
    public Set<PGeographicKeyword> getGeographicKeywords() {
        if (geographicKeywords == null) {
            geographicKeywords = new LinkedHashSet<PGeographicKeyword>();
        }
        return geographicKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PGeographicKeyword> getActiveGeographicKeywords() {
        return getGeographicKeywords();
    }

    public Set<PGeographicKeyword> getActiveManagedGeographicKeywords() {
        return getManagedGeographicKeywords();
    }

    public Set<PGeographicKeyword> getIndexedGeographicKeywords() {
        Set<PGeographicKeyword> indexed = new HashSet<PGeographicKeyword>(getActiveGeographicKeywords());
        if (!CollectionUtils.isEmpty(getActiveManagedGeographicKeywords())) {
            indexed.addAll(getActiveManagedGeographicKeywords());
        }
        return indexed;
    }

    public void setGeographicKeywords(Set<PGeographicKeyword> geographicKeywords) {
        this.geographicKeywords = geographicKeywords;
    }

    public String getJoinedGeographicKeywords() {
        return join(geographicKeywords);
    }

    public String getJoinedManagedGeographicKeywords() {
        return join(managedGeographicKeywords);
    }

    @XmlElementWrapper(name = "temporalKeywords")
    @XmlElement(name = "temporalKeyword")
    public Set<PTemporalKeyword> getTemporalKeywords() {
        if (temporalKeywords == null) {
            temporalKeywords = new LinkedHashSet<PTemporalKeyword>();
        }
        return temporalKeywords;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PTemporalKeyword> getActiveTemporalKeywords() {
        return getTemporalKeywords();
    }

    public void setTemporalKeywords(Set<PTemporalKeyword> temporalKeywords) {
        this.temporalKeywords = temporalKeywords;
    }

    public String getJoinedTemporalKeywords() {
        return join(temporalKeywords);
    }

    @XmlTransient
    public Set<PResourceRevisionLog> getResourceRevisionLog() {
        return resourceRevisionLog;
    }

    public void setResourceRevisionLog(
            Set<PResourceRevisionLog> resourceRevisionLog) {
        this.resourceRevisionLog = resourceRevisionLog;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    @Deprecated()
    @JsonView(JsonLookupFilter.class)
    // removing for localization
    public String getResourceTypeLabel() {
        return MessageHelper.getMessage(resourceType.getLocaleKey());
    }

    // marked as final because this is called from constructors.
    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    @XmlElementWrapper(name = "sourceCollections")
    @XmlElement(name = "sourceCollection")
    public Set<PSourceCollection> getSourceCollections() {
        if (sourceCollections == null) {
            sourceCollections = new LinkedHashSet<PSourceCollection>();
        }
        return sourceCollections;
    }

    @XmlElementWrapper(name = "resourceNotes")
    @XmlElement(name = "resourceNote")
    public Set<PResourceNote> getResourceNotes() {
        if (resourceNotes == null) {
            resourceNotes = new LinkedHashSet<PResourceNote>();
        }
        return resourceNotes;
    }

    public void setSourceCollections(Set<PSourceCollection> sourceCollections) {
        this.sourceCollections = sourceCollections;
    }

    public void setResourceNotes(Set<PResourceNote> resourceNotes) {
        this.resourceNotes = resourceNotes;
    }

    @XmlElementWrapper(name = "relatedComparativeCollections")
    @XmlElement(name = "relatedComparativeCollection")
    public Set<PRelatedComparativeCollection> getRelatedComparativeCollections() {
        if (relatedComparativeCollections == null) {
            relatedComparativeCollections = new LinkedHashSet<PRelatedComparativeCollection>();
        }
        return relatedComparativeCollections;
    }

    public void setRelatedComparativeCollections(
            Set<PRelatedComparativeCollection> relatedComparativeCollections) {
        this.relatedComparativeCollections = relatedComparativeCollections;
    }

    @Override
    public String toString() {
        return String.format("%s (id: %d, %s)", title, getId(), resourceType);
    }

    /**
     * Returns the title field clamped to 200 characters.
     * 
     * @return
     */
    public String getShortenedTitle() {
        return StringUtils.abbreviate(title, 200);
    }

    /**
     * Returns the description field clamped to 500 characters.
     * 
     * @return
     */
    public String getShortenedDescription() {
        return StringUtils.abbreviate(description, 500);

    }

    /**
     * Returns the alphanumeric comparison of resource.title.
     */
    @Override
    public int compareTo(PResource resource) {
        int comparison = getTitle().compareTo(resource.getTitle());
        return (comparison == 0) ? getId().compareTo(resource.getId())
                : comparison;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isTransient() {
        return PersistableUtils.isTransient(this);
    }

    /**
     * Returns the appropriate url namespace where actions for this information
     * resource can be accessed (e.g., /<b>project</b>/add vs
     * /<b>document</b>/add vs /<b>dataset</b>/add).
     * 
     * @return
     */
    @Override
    @JsonView(JsonLookupFilter.class)
    public String getUrlNamespace() {
        return getResourceType().getUrlNamespace();
    }

    public String getAbsoluteUrl() {
        return getUrlNamespace() + "/" + getId();
    }

    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @XmlAttribute(name = "updaterRef")
    public PTdarUser getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(PTdarUser updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    @XmlTransient
    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Override
    @XmlAttribute
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        if (this.status != status) {
            setPreviousStatus(this.status);
            setStatusChanged(Boolean.TRUE);
        }
        this.status = status;
    }

    public void addResourceNote(PResourceNote note) {
        resourceNotes.add(note);
    }

    /**
     * @param resourceCreators
     *            the resourceCreators to set
     */
    public void setResourceCreators(Set<PResourceCreator> resourceCreators) {
        this.resourceCreators = resourceCreators;
    }

    /**
     * @return the set of all resourceCreators associated with this Resource
     */
    @XmlElementWrapper(name = "resourceCreators")
    @XmlElement(name = "resourceCreator")
    public Set<PResourceCreator> getResourceCreators() {
        if (resourceCreators == null) {
            resourceCreators = new LinkedHashSet<PResourceCreator>();
        }
        return resourceCreators;
    }

    /**
     * @return the resourceCreators with the given ResourceCreatorRole
     */
    public Set<PResourceCreator> getResourceCreators(ResourceCreatorRole role) {
        Set<PResourceCreator> creators = new LinkedHashSet<PResourceCreator>();
        for (PResourceCreator creator : this.getResourceCreators()) {
            if (creator.getRole() == role) {
                creators.add(creator);
            }
        }
        return creators;
    }

    /**
     * @param resourceAnnotations
     *            the resourceAnnotations to set
     */
    public void setResourceAnnotations(Set<PResourceAnnotation> resourceAnnotations) {
        this.resourceAnnotations = resourceAnnotations;
    }

    /**
     * @return the resourceAnnotations
     */
    @XmlElementWrapper(name = "resourceAnnotations")
    @XmlElement(name = "resourceAnnotation")
    public Set<PResourceAnnotation> getResourceAnnotations() {
        if (resourceAnnotations == null) {
            resourceAnnotations = new LinkedHashSet<PResourceAnnotation>();
        }
        return resourceAnnotations;
    }

    @XmlTransient
    public Collection<PResourceCreator> getContentOwners() {
        List<PResourceCreator> authors = new ArrayList<PResourceCreator>();

        // get the applicable resource roles for this resource type
        List<ResourceCreatorRole> primaryRoles = ResourceCreatorRole.getAuthorshipRoles();
        if (resourceCreators != null) {
            for (PResourceCreator creator : resourceCreators) {
                if (primaryRoles.contains(creator.getRole()) && !creator.getCreator().isDeleted()) {
                    authors.add(creator);
                }
            }

        }
        Collections.sort(authors);
        return authors;
    }

    public Collection<PResourceCreator> getPrimaryCreators() {
        List<PResourceCreator> authors = new ArrayList<PResourceCreator>();

        // get the applicable resource roles for this resource type
        Set<ResourceCreatorRole> primaryRoles = ResourceCreatorRole.getPrimaryCreatorRoles(getResourceType());
        if (resourceCreators != null) {
            for (PResourceCreator creator : resourceCreators) {
                if (primaryRoles.contains(creator.getRole()) && !creator.getCreator().isDeleted()) {
                    authors.add(creator);
                }
            }

        }
        Collections.sort(authors);
        return authors;
    }

    public Collection<PResourceCreator> getEditors() {
        List<PResourceCreator> editors = new ArrayList<PResourceCreator>(
                this.getResourceCreators(ResourceCreatorRole.EDITOR));
        Iterator<PResourceCreator> iterator = editors.iterator();
        while (iterator.hasNext()) {
            PResourceCreator rc = iterator.next();
            if (rc.getCreator().isDeleted()) {
                iterator.remove();
            }
        }
        Collections.sort(editors);
        return editors;
    }

    /**
     * @param managedGeographicKeywords
     *            the managedGeographicKeywords to set
     */
    public void setManagedGeographicKeywords(Set<PGeographicKeyword> managedGeographicKeywords) {
        this.managedGeographicKeywords = managedGeographicKeywords;
    }

    /**
     * @return the managedGeographicKeywords
     */
    @XmlElementWrapper(name = "managedGeographicKeywords")
    @XmlElement(name = "managedGeographicKeyword")
    public Set<PGeographicKeyword> getManagedGeographicKeywords() {
        return managedGeographicKeywords;
    }

    public void markUpdated(PTdarUser p) {
        setUpdatedBy(p);
        setUpdated(true);
        setDateUpdated(new Date());
        if (submitter == null) {
            setSubmitter(p);
        }
        if (uploader == null) {
            setUploader(p);
        }
        if (dateCreated == null) {
            setDateCreated(new Date());
        }
    }

    @Override
    public List<?> getEqualityFields() {
        return Collections.emptyList();
    }

    @Override
    public boolean equals(Object candidate) {
        return PersistableUtils.isEqual(this, (Persistable) candidate);
    }

    @Override
    public int hashCode() {
        return PersistableUtils.toHashCode(this);
    }

    public void setCoverageDates(Set<PCoverageDate> coverageDates) {
        this.coverageDates = coverageDates;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PCoverageDate> getActiveCoverageDates() {
        return getCoverageDates();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PResourceAnnotation> getActiveResourceAnnotations() {
        return getResourceAnnotations();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PSourceCollection> getActiveSourceCollections() {
        return getSourceCollections();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PRelatedComparativeCollection> getActiveRelatedComparativeCollections() {
        return getRelatedComparativeCollections();
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PResourceNote> getActiveResourceNotes() {
        return getResourceNotes();
    }

    @XmlElementWrapper(name = "coverageDates")
    @XmlElement(name = "coverageDate")
    public Set<PCoverageDate> getCoverageDates() {
        if (coverageDates == null) {
            coverageDates = new LinkedHashSet<PCoverageDate>();
        }
        return coverageDates;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PKeyword> getAllActiveKeywords() {
        Collection<PKeyword> kwds = new HashSet<>();
        kwds.addAll(getActiveCultureKeywords());
        kwds.addAll(getIndexedGeographicKeywords());
        kwds.addAll(getActiveSiteNameKeywords());
        kwds.addAll(getActiveInvestigationTypes());
        kwds.addAll(getActiveSiteTypeKeywords());
        kwds.addAll(getActiveMaterialKeywords());
        kwds.addAll(getActiveOtherKeywords());
        kwds.addAll(getActiveTemporalKeywords());
        return kwds;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(boolean created) {
        this.created = created;
    }

    /**
     * @return the created
     */
    @XmlTransient
    public boolean isCreated() {
        return created;
    }

    /**
     * Marking these as comments due to list collections not being currently implemented.
     * 
     * @return
     */
    @XmlElementWrapper(name = "unmanagedResourceCollections")
    @XmlElementRefs({
            @XmlElementRef(name = "resourceCollection", type = PResourceCollection.class, required = false),
            @XmlElementRef(name = "resourceCollectionRef", type = JAXBPersistableRef.class, required = false)
    })
    @XmlJavaTypeAdapter(JaxbPResourceCollectionRefConverter.class)
    public Set<PResourceCollection> getUnmanagedResourceCollections() {
        return unmanagedResourceCollections;
    }

    public void setUnmanagedResourceCollections(Set<PResourceCollection> publicResourceCollections) {
        this.unmanagedResourceCollections = publicResourceCollections;
    }

    @XmlElementWrapper(name = RESOURCE_COLLECTIONS)
    @XmlElementRefs({
            @XmlElementRef(name = "resourceCollection", type = PResourceCollection.class, required = false),
            @XmlElementRef(name = "resourceCollectionRef", type = JAXBPersistableRef.class, required = false)
    })
    @XmlJavaTypeAdapter(JaxbPResourceCollectionRefConverter.class)
    public Set<PResourceCollection> getManagedResourceCollections() {
        return managedResourceCollections;
    }
    
    public void setManagedResourceCollections(Set<PResourceCollection> mrc )  {
        this.managedResourceCollections = mrc;
    }

    // @Transient
    // public Set<ResourceCollection> getRightsBasedResourceCollections() {
    // Set<ResourceCollection> collections = new HashSet<>();
    // if (CollectionUtils.isNotEmpty(getManagedResourceCollections())) {
    // collections.addAll(getManagedResourceCollections());
    // }
    // Iterator<ResourceCollection> iterator = collections.iterator();
    // while (iterator.hasNext()) {
    // ResourceCollection next = iterator.next();
    // if (next == null || CollectionUtils.isEmpty(next.getManagedResources())) {
    // iterator.remove();
    // }
    // }
    // return collections;
    // }

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

    @Override
    @XmlTransient
    public String getName() {
        return getTitle();
    }

    public Set<PResourceCollection> getVisibleSharedResourceCollections() {
        Set<PResourceCollection> collections = new LinkedHashSet<>();
        for (PResourceCollection collection : managedResourceCollections) {
            if (collection.isVisibleAndActive()) {
                collections.add((PResourceCollection) collection);
            }
        }
        return collections;
    }

    /**
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId
     *            the externalId to set
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }


    @Override
    @XmlTransient
    public boolean isViewable() {
        return viewable;
    }

    @Override
    public void setViewable(boolean viewable) {
        this.viewable = viewable;
    }

    /*
     * This method is used to capture people who upload things without any
     * ResourceCreators, this makes the resource show up in the browse page for
     * that creator
     */
    @XmlTransient
    public Long getResourceOwner() {
        if (CollectionUtils.isEmpty(getResourceCreators())) {
            return getSubmitter().getId();
        }
        return null;
    }

    @XmlTransient
    public List<PCreator<?>> getRelatedCreators() {
        List<PCreator<?>> creators = new ArrayList<>();
        for (PResourceCreator creator : resourceCreators) {
            creators.add(creator.getCreator());
        }
        creators.add(getSubmitter());
        return creators;
    }

    @JsonIgnore
    @XmlTransient
    public Long getTransientAccessCount() {
        return transientAccessCount;
    }

    public void setTransientAccessCount(Long l) {
        this.transientAccessCount = l;
    }

    public boolean isSupportsThumbnails() {
        return false;
    }

    public boolean isCitationRecord() {
        return true;
    }

    public <R extends PResource> void copyImmutableFieldsFrom(R resource) {
        this.setDateCreated(resource.getDateCreated());
        this.setStatus(resource.getStatus());
        this.setSubmitter(resource.getSubmitter());
        // set previous, then set current
        this.setSpaceInBytesUsed(resource.getPreviousSpaceInBytesUsed());
        this.setFilesUsed(resource.getPreviousFilesUsed());
        this.setSpaceInBytesUsed(resource.getSpaceInBytesUsed());
        this.setFilesUsed(resource.getFilesUsed());
        this.getManagedResourceCollections().addAll(new ArrayList<>(resource.getManagedResourceCollections()));
        this.getAuthorizedUsers().addAll(resource.getAuthorizedUsers());

    }

    @XmlAttribute(name = "uploaderRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    @NotNull
    public PTdarUser getUploader() {
        return uploader;
    }

    public void setUploader(PTdarUser uploader) {
        this.uploader = uploader;
    }

    @XmlTransient
    public boolean isLessThanDayOld() {
        return Days.daysBetween(new DateTime(new Date()), new DateTime(getDateCreated())).getDays() < 1;
    }

    public boolean isContainsActiveKeywords() {

        if (CollectionUtils.isNotEmpty(getActiveSiteNameKeywords()) || CollectionUtils.isNotEmpty(getActiveCultureKeywords()) ||
                CollectionUtils.isNotEmpty(getActiveSiteTypeKeywords()) || CollectionUtils.isNotEmpty(getActiveMaterialKeywords()) ||
                CollectionUtils.isNotEmpty(getActiveInvestigationTypes()) || CollectionUtils.isNotEmpty(getActiveOtherKeywords()) ||
                CollectionUtils.isNotEmpty(getActiveTemporalKeywords()) || CollectionUtils.isNotEmpty(getActiveGeographicKeywords())) {
            return true;
        }
        return false;
    }

    public List<String> getKeywordProperties() {
        List<String> toReturn = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(getActiveCultureKeywords())) {
            toReturn.add("activeCultureKeywords");
        }
        if (CollectionUtils.isNotEmpty(getActiveMaterialKeywords())) {
            toReturn.add("activeMaterialKeywords");
        }
        if (CollectionUtils.isNotEmpty(getActiveSiteNameKeywords())) {
            toReturn.add("activeSiteNameKeywords");
        }
        if (CollectionUtils.isNotEmpty(getActiveSiteTypeKeywords())) {
            toReturn.add("activeSiteTypeKeywords");
        }
        if (CollectionUtils.isNotEmpty(getActiveInvestigationTypes())) {
            toReturn.add("activeInvestigationTypes");
        }
        if (CollectionUtils.isNotEmpty(getActiveOtherKeywords())) {
            toReturn.add("activeOtherKeywords");
        }
        if (CollectionUtils.isNotEmpty(getActiveGeographicKeywords())) {
            toReturn.add("activeGeographicKeywords");
        }
        if (CollectionUtils.isNotEmpty(getActiveTemporalKeywords())) {
            toReturn.add("activeTemporalKeywords");
        }
        return toReturn;
    }

    @JsonIgnore
    @XmlTransient
    public BillingAccount getAccount() {
        return account;
    }

    public void setAccount(BillingAccount account) {
        this.account = account;
    }

    public Status getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(Status previousStatus) {
        this.previousStatus = previousStatus;
    }

    public Long getSpaceInBytesUsed() {
        if (spaceInBytesUsed == null) {
            return 0L;
        }
        return spaceInBytesUsed;
    }

    public void setSpaceInBytesUsed(Long spaceInBytesUsed) {
        setPreviousSpaceInBytesUsed(this.spaceInBytesUsed);
        this.spaceInBytesUsed = spaceInBytesUsed;
    }

    public Long getFilesUsed() {
        if (filesUsed == null) {
            return 0L;
        }
        return filesUsed;
    }

    public void setFilesUsed(Long filesUsed) {
        setPreviousFilesUsed(this.filesUsed);
        this.filesUsed = filesUsed;
    }

    public Long getPreviousSpaceInBytesUsed() {
        if (previousSpaceInBytesUsed == null) {
            return 0L;
        }
        return previousSpaceInBytesUsed;
    }

    public void setPreviousSpaceInBytesUsed(Long previousSpaceInBytesUsed) {
        this.previousSpaceInBytesUsed = previousSpaceInBytesUsed;
    }

    public Long getPreviousFilesUsed() {
        if (previousFilesUsed == null) {
            return 0L;
        }
        return previousFilesUsed;
    }

    public void setPreviousFilesUsed(Long previousFilesUsed) {
        this.previousFilesUsed = previousFilesUsed;
    }

    @JsonIgnore
    @XmlTransient
    public Long getEffectiveSpaceUsed() {
        return getSpaceInBytesUsed() - getPreviousSpaceInBytesUsed();
    }

    @JsonIgnore
    @XmlTransient
    public Long getSpaceUsedInMb() {
        return MathUtils.divideByRoundUp(spaceInBytesUsed, MathUtils.ONE_MB);
    }

    @JsonIgnore
    @XmlTransient
    public Long getEffectiveFilesUsed() {
        return getFilesUsed() - getPreviousFilesUsed();
    }

    @JsonIgnore
    @XmlTransient
    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    @JsonIgnore
    @XmlTransient
    public boolean isCountedInBillingEvaluation() {
        return countedInBillingEvaluation;
    }

    public void setCountedInBillingEvaluation(boolean countedInBillingEvaluation) {
        this.countedInBillingEvaluation = countedInBillingEvaluation;
    }

    public boolean hasConfidentialFiles() {
        return false;
    }

    public boolean hasEmbargoedFiles() {
        return false;
    }

    public boolean isHasBrowsableImages() {
        return false;
    }


    public Set<PResourceCreator> getIndividualAndInstitutionalCredit() {
        Set<PResourceCreator> creators = new HashSet<>();
        for (PResourceCreator creator : this.getActiveResourceCreators()) {
            if (creator.getRole().getType() == ResourceCreatorRoleType.CREDIT) {
                creators.add(creator);
            }
        }
        return creators;
    }

    @JsonView(JsonProjectLookupFilter.class)
    public Set<PResourceCreator> getActiveIndividualAndInstitutionalCredit() {
        return getIndividualAndInstitutionalCredit();
    }

    @JsonIgnore
    @XmlTransient
    public boolean isBookmarked() {
        return bookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        this.bookmarked = bookmarked;
    }

    @JsonView(JsonLookupFilter.class)
    public String getDetailUrl() {
        return String.format("/%s/%s/%s", getUrlNamespace(), getId(), getSlug());
    }

    @Override
    public String getSlug() {
        return UrlUtils.slugify(getName());
    }

    @JsonIgnore
    @XmlTransient
    public Set<PBookmarkedResource> getBookmarkedResources() {
        return bookmarkedResources;
    }

    public void setBookmarkedResources(Set<PBookmarkedResource> bookmarkedResources) {
        this.bookmarkedResources = bookmarkedResources;
    }

    public Boolean getStatusChanged() {
        return statusChanged;
    }

    private void setStatusChanged(Boolean statusChanged) {
        this.statusChanged = statusChanged;
    }

    public String getFormattedDescription() {
        return formattedDescription;
    }

    public void setFormattedDescription(String formattedDescription) {
        this.formattedDescription = formattedDescription;
    }

    public Collection<PResourceCollection> getVisibleUnmanagedResourceCollections() {
        Set<PResourceCollection> collections = new LinkedHashSet<>();
        for (PResourceCollection collection : getUnmanagedResourceCollections()) {
            if (collection.isVisibleAndActive()) {
                collections.add(collection);
            }
        }
        return collections;
    }

    @XmlTransient
    @Override
    public boolean isConfidentialViewable() {
        return viewConfidential;
    }

    @Override
    public void setConfidentialViewable(boolean editable) {
        this.viewConfidential = editable;
    }

    @XmlElementWrapper(name = "authorizedUsers")
    @XmlElement(name = "authorizedUser")
    public Set<PAuthorizedUser> getAuthorizedUsers() {
        return authorizedUsers;
    }

    public void setAuthorizedUsers(Set<PAuthorizedUser> authorizedUsers) {
        this.authorizedUsers = authorizedUsers;
    }

    @Override
    @XmlTransient
    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
