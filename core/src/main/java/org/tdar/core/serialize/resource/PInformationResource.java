package org.tdar.core.serialize.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections4.CollectionUtils;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.bean.resource.Language;
import org.tdar.core.bean.resource.LicenseType;
import org.tdar.core.bean.resource.ResourceAccessType;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.bean.resource.file.FileStatus;
import org.tdar.core.bean.resource.file.VersionType;
import org.tdar.core.serialize.citation.PRelatedComparativeCollection;
import org.tdar.core.serialize.citation.PSourceCollection;
import org.tdar.core.serialize.coverage.PCoverageDate;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.serialize.entity.PCreator;
import org.tdar.core.serialize.entity.PInstitution;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.keyword.PCultureKeyword;
import org.tdar.core.serialize.keyword.PGeographicKeyword;
import org.tdar.core.serialize.keyword.PInvestigationType;
import org.tdar.core.serialize.keyword.PMaterialKeyword;
import org.tdar.core.serialize.keyword.POtherKeyword;
import org.tdar.core.serialize.keyword.PSiteNameKeyword;
import org.tdar.core.serialize.keyword.PSiteTypeKeyword;
import org.tdar.core.serialize.keyword.PTemporalKeyword;
import org.tdar.core.serialize.resource.datatable.PDataTableColumn;
import org.tdar.core.serialize.resource.file.PInformationResourceFile;
import org.tdar.core.serialize.resource.file.PInformationResourceFileVersion;
import org.tdar.utils.PersistableUtils;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * $Id$
 * <p>
 * Represents a Resource with a file payload and additional metadata that can be one of the following:
 * </p>
 * <ol>
 * <li>Image
 * <li>Dataset file (Access, Excel)
 * <li>Document (PDF)
 * </ol>
 * 
 * @author <a href='Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Revision$
 */
public abstract class PInformationResource extends PResource {

    public static final String LICENSE_TEXT = "LICENSE_TEXT";
    public static final String LICENSE_TYPE = "LICENSE_TYPE";
    public static final String COPYRIGHT_HOLDER = "COPYRIGHT_HOLDER";


    public PInformationResource() {

    }

    @Deprecated
    public PInformationResource(Long id, String title) {
        setId(id);
        setTitle(title);
    }

    @Deprecated
    public PInformationResource(Long id, String title, ResourceType type) {
        setId(id);
        setTitle(title);
        setResourceType(type);
    }

    private PProject project;

    private Set<PInformationResourceFile> informationResourceFiles = new LinkedHashSet<>();

    private Language metadataLanguage;

    private Language resourceLanguage;

    private LicenseType licenseType;

    private String licenseText;

    private String doi;

    private boolean externalReference;

    private String copyLocation;

    private Date lastUploaded;

    private Integer date = -1;

    private Integer dateNormalized = -1;

    private PInstitution resourceProviderInstitution;

    private PInstitution publisher;

    private String publisherLocation;

    private PCreator<?> copyrightHolder;

    public static final String INVESTIGATION_TYPE_INHERITANCE_TOGGLE = "inheriting_investigation_information";
    public static final String SITE_NAME_INHERITANCE_TOGGLE = "inheriting_site_information";
    public static final String MATERIAL_TYPE_INHERITANCE_TOGGLE = "inheriting_material_information";
    public static final String OTHER_INHERITANCE_TOGGLE = "inheriting_other_information";
    public static final String GEOGRAPHIC_INHERITANCE_TOGGLE = "inheriting_spatial_information";
    public static final String CULTURE_INHERITANCE_TOGGLE = "inheriting_cultural_information";
    public static final String TEMPORAL_INHERITANCE_TOGGLE = "inheriting_temporal_information";

    private transient ResourceAccessType transientAccessType;

    private boolean inheritingInvestigationInformation = false;
    private boolean inheritingSiteInformation = false;
    private boolean inheritingMaterialInformation = false;
    private boolean inheritingOtherInformation = false;
    private boolean inheritingCulturalInformation = false;
    private boolean inheritingSpatialInformation = false;
    private boolean inheritingTemporalInformation = false;
    private boolean inheritingNoteInformation = false;
    private boolean inheritingIdentifierInformation = false;
    private boolean inheritingCollectionInformation = false;
    private boolean inheritingIndividualAndInstitutionalCredit = false;

    private PDataTableColumn mappedDataKeyColumn;

    private String mappedDataKeyValue;

    public Language getMetadataLanguage() {
        return metadataLanguage;
    }

    public void setMetadataLanguage(Language metadataLanguage) {
        this.metadataLanguage = metadataLanguage;
    }

    public Language getResourceLanguage() {
        return resourceLanguage;
    }

    public void setResourceLanguage(Language resourceLanguage) {
        this.resourceLanguage = resourceLanguage;
    }

    @XmlElement(name = "copyrightHolderRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PCreator<?> getCopyrightHolder() {
        return copyrightHolder;
    }

    public void setCopyrightHolder(PCreator<?> copyrightHolder) {
        this.copyrightHolder = copyrightHolder;
    }

    @XmlElement(name = "licenseType")
    public LicenseType getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(LicenseType licenseType) {
        this.licenseType = licenseType;
        if (licenseType != LicenseType.OTHER) {
            setLicenseText(null);
        }
    }

    @XmlElement(name = "licenseText")
    public String getLicenseText() {
        if (licenseType == LicenseType.OTHER) {
            return licenseText;
        } else {
            return null;
        }
    }

    public void setLicenseText(String licenseText) {
        this.licenseText = licenseText;
    }

    public Integer getDate() {
        return date;
    }

    public void setDate(Integer dateCreated) {
        this.date = dateCreated;
        if (dateCreated != null) {
            this.dateNormalized = (int) (Math.floor(dateCreated.floatValue() / 10f) * 10);
        } else {
            this.dateNormalized = null;
        }
    }

    public Integer getDateNormalized() {
        return dateNormalized;
    }

    @Deprecated
    public void setDateNormalized(Integer dateCreatedNormalized) {
        this.dateNormalized = dateCreatedNormalized;
    }

    public PInstitution getResourceProviderInstitution() {
        return resourceProviderInstitution;
    }

    public void setResourceProviderInstitution(PInstitution resourceProviderInstitution) {
        this.resourceProviderInstitution = resourceProviderInstitution;
    }

    @XmlElement(name = "projectRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PProject getProject() {
        if (project == null) {
            return PProject.NULL;
        }
        return project;
    }

    @XmlTransient
    public Long getProjectId() {
        if (PersistableUtils.isNotNullOrTransient(getProject())) {
            return getProject().getId();
        }
        return null;
    }

    @Transient
    public String getProjectTitle() {
        return getProject().getTitle();
    }

    public void setProject(PProject project) {
        if (project == PProject.NULL) {
            this.project = null;
        } else {
            this.project = project;
        }
    }

    /**
     * Returns true if this resource is an externally referenced resource,
     * signifying that there is no uploaded file. The URL should then
     * 
     * @return
     */
    public boolean isExternalReference() {
        return externalReference;
    }

    public void setExternalReference(boolean externalReference) {
        this.externalReference = externalReference;
    }

    public Date getLastUploaded() {
        return lastUploaded;
    }

    public void setLastUploaded(Date lastUploaded) {
        this.lastUploaded = lastUploaded;
    }

    public int getTotalNumberOfFiles() {
        return informationResourceFiles.size();
    }

    public int getTotalNumberOfActiveFiles() {
        int count = 0;
        for (PInformationResourceFile file : informationResourceFiles) {
            if (file.isDeleted()) {
                continue;
            }
            count++;
        }
        return count;
    }

    @XmlElementWrapper(name = "informationResourceFiles")
    @XmlElement(name = "informationResourceFile")
    public Set<PInformationResourceFile> getInformationResourceFiles() {
        return informationResourceFiles;
    }

    @XmlTransient
    @JsonIgnore
    public PInformationResourceFile getFirstInformationResourceFile() {
        if (getInformationResourceFiles().isEmpty()) {
            return null;
        }
        return informationResourceFiles.iterator().next();
    }


    public void setInformationResourceFiles(Set<PInformationResourceFile> informationResourceFiles) {
        this.informationResourceFiles = informationResourceFiles;
    }


    @XmlTransient
    public Collection<PInformationResourceFileVersion> getLatestVersions() {
        // FIXME: this method will become increasingly expensive as the number of files increases
        ArrayList<PInformationResourceFileVersion> latest = new ArrayList<PInformationResourceFileVersion>();
        for (PInformationResourceFile irfile : getInformationResourceFiles()) {
            latest.addAll(irfile.getLatestVersions());
        }
        return latest;
    }

    public Collection<PInformationResourceFileVersion> getLatestVersions(String type) {
        return getLatestVersions(VersionType.valueOf(type));
    }

    public Collection<PInformationResourceFileVersion> getLatestVersions(VersionType type) {
        ArrayList<PInformationResourceFileVersion> latest = new ArrayList<PInformationResourceFileVersion>();
        for (PInformationResourceFile irfile : getInformationResourceFiles()) {
            PInformationResourceFileVersion irfileVersion = irfile.getCurrentVersion(type);
            if (irfileVersion != null) {
                latest.add(irfileVersion);
            }
        }
        return latest;
    }

    @XmlTransient
    @JsonIgnore
    public PInformationResourceFileVersion getLatestUploadedVersion() {
        Collection<PInformationResourceFileVersion> latestUploadedVersions = getLatestUploadedVersions();
        if (CollectionUtils.isEmpty(latestUploadedVersions)) {
            logger.warn("No latest uploaded version for {}", this);
            return null;
        }
        return getLatestUploadedVersions().iterator().next();
    }

    @XmlTransient
    @JsonIgnore
    public Collection<PInformationResourceFileVersion> getLatestUploadedVersions() {
        return getLatestVersions(VersionType.UPLOADED);
    }

    @Transient
    @JsonIgnore
    @XmlTransient
    public List<PInformationResourceFileVersion> getContent() {
        logger.trace("getContent");
        List<PInformationResourceFile> files = getPublicFiles();
        if (CollectionUtils.isEmpty(files)) {
            return null;
        }
        // List<InputStream> streams = new ArrayList<InputStream>();
        List<PInformationResourceFileVersion> fileURIs = new ArrayList<PInformationResourceFileVersion>();
        for (PInformationResourceFile irFile : files) {
            try {
                if (irFile.getRestriction().isRestricted()) {
                    continue;
                }
                PInformationResourceFileVersion indexableVersion = irFile.getIndexableVersion();
                fileURIs.add(indexableVersion);
            } catch (Exception e) {
                logger.trace("an exception occurred while reading file: {} ", e);
            }
        }
        return fileURIs;
    }

    @Transient
    public ResourceAccessType getResourceAccessType() {
        if (transientAccessType != null) {
            return transientAccessType;
        }
        int totalFiles = getNonDeletedFiles().size();
        int publicFiles = getPublicFiles().size();
        if (totalFiles > 0) {
            if (publicFiles == 0) {
                return ResourceAccessType.RESTRICTED;
            }
            if (publicFiles == totalFiles) {
                return ResourceAccessType.PUBLICALLY_ACCESSIBLE;
            }
            return ResourceAccessType.PARTIALLY_RESTRICTED;
        }
        return ResourceAccessType.CITATION;
    }

    @Transient
    @XmlTransient
    public boolean isPublicallyAccessible() {
        return getResourceAccessType() == ResourceAccessType.PUBLICALLY_ACCESSIBLE;
    }

    @Transient
    public boolean getContainsFiles() {
        return hasFiles();
    }

    @Transient
    public boolean hasFiles() {
        return getInformationResourceFiles().size() > 0;
    }

    public boolean isInheritingInvestigationInformation() {
        return inheritingInvestigationInformation;
    }

    public void setInheritingInvestigationInformation(boolean inheritingInvestigationInformation) {
        this.inheritingInvestigationInformation = inheritingInvestigationInformation;
    }

    public boolean isInheritingSiteInformation() {
        return inheritingSiteInformation;
    }

    public void setInheritingSiteInformation(boolean inheritingSiteInformation) {
        this.inheritingSiteInformation = inheritingSiteInformation;
    }

    public boolean isInheritingMaterialInformation() {
        return inheritingMaterialInformation;
    }

    public void setInheritingMaterialInformation(boolean inheritingMaterialInformation) {
        this.inheritingMaterialInformation = inheritingMaterialInformation;
    }

    public boolean isInheritingOtherInformation() {
        return inheritingOtherInformation;
    }

    public void setInheritingOtherInformation(boolean inheritingOtherInformation) {
        this.inheritingOtherInformation = inheritingOtherInformation;
    }

    public boolean isInheritingCulturalInformation() {
        return inheritingCulturalInformation;
    }

    public void setInheritingCulturalInformation(boolean inheritingCulturalInformation) {
        this.inheritingCulturalInformation = inheritingCulturalInformation;
    }

    public boolean isInheritingSpatialInformation() {
        return inheritingSpatialInformation;
    }

    public void setInheritingSpatialInformation(boolean inheritingSpatialInformation) {
        this.inheritingSpatialInformation = inheritingSpatialInformation;
    }

    public boolean isInheritingTemporalInformation() {
        return inheritingTemporalInformation;
    }

    public void setInheritingTemporalInformation(boolean inheritingTemporalInformation) {
        this.inheritingTemporalInformation = inheritingTemporalInformation;
    }

    public String getCopyLocation() {
        return copyLocation;
    }

    public void setCopyLocation(String copyLocation) {
        this.copyLocation = copyLocation;
    }

    @Override
    public Set<PInvestigationType> getActiveInvestigationTypes() {
        return isProjectVisible() && isInheritingInvestigationInformation() ? project.getInvestigationTypes() : getInvestigationTypes();
    }

    @Override
    public Set<PResourceCreator> getActiveIndividualAndInstitutionalCredit() {
        return isProjectVisible() && isInheritingIndividualAndInstitutionalCredit() ? project.getIndividualAndInstitutionalCredit()
                : getIndividualAndInstitutionalCredit();
    }

    @Override
    public Set<PResourceCreator> getActiveResourceCreators() {
        Set<PResourceCreator> local = new HashSet<PResourceCreator>(super.getResourceCreators());
        if (isProjectVisible() && isInheritingIndividualAndInstitutionalCredit()) {
            local.addAll(project.getIndividualAndInstitutionalCredit());
        }
        return local;
    }

    @Transient
    @XmlTransient
    public boolean isProjectVisible() {
        // FIXME: indexing was dying when project below was replaced with getProject()
        return getProject().isActive() || getProject().isDraft();
    }

    @Override
    public Set<PSiteNameKeyword> getActiveSiteNameKeywords() {
        return isProjectVisible() && isInheritingSiteInformation() ? project.getSiteNameKeywords() : getSiteNameKeywords();
    }

    @Override
    public Set<PSourceCollection> getActiveSourceCollections() {
        return isProjectVisible() && isInheritingCollectionInformation() ? project.getSourceCollections() : getSourceCollections();
    }

    @Override
    public Set<PRelatedComparativeCollection> getActiveRelatedComparativeCollections() {
        return isProjectVisible() && isInheritingCollectionInformation() ? project.getRelatedComparativeCollections() : getRelatedComparativeCollections();
    }

    @Override
    public Set<PSiteTypeKeyword> getActiveSiteTypeKeywords() {
        return isProjectVisible() && isInheritingSiteInformation() ? project.getSiteTypeKeywords() : getSiteTypeKeywords();
    }

    public Set<PSiteTypeKeyword> getActiveApprovedSiteTypeKeywords() {
        return isProjectVisible() && isInheritingSiteInformation() ? project.getApprovedSiteTypeKeywords() : getApprovedSiteTypeKeywords();
    }

    public Set<PSiteTypeKeyword> getActiveUncontrolledSiteTypeKeywords() {
        return isProjectVisible() && isInheritingSiteInformation() ? project.getUncontrolledSiteTypeKeywords() : getUncontrolledSiteTypeKeywords();
    }

    @Override
    public Set<PMaterialKeyword> getActiveMaterialKeywords() {
        return isProjectVisible() && isInheritingMaterialInformation() ? project.getMaterialKeywords() : getMaterialKeywords();
    }

    @Override
    public Set<POtherKeyword> getActiveOtherKeywords() {
        return isProjectVisible() && isInheritingOtherInformation() ? project.getOtherKeywords() : getOtherKeywords();
    }

    @Override
    public Set<PCultureKeyword> getActiveCultureKeywords() {
        return isProjectVisible() && isInheritingCulturalInformation() ? project.getCultureKeywords() : getCultureKeywords();
    }

    public Set<PCultureKeyword> getActiveApprovedCultureKeywords() {
        return isProjectVisible() && isInheritingCulturalInformation() ? project.getApprovedCultureKeywords() : getApprovedCultureKeywords();
    }

    @Override
    public Set<PResourceNote> getActiveResourceNotes() {
        return isProjectVisible() && isInheritingNoteInformation() ? project.getResourceNotes() : getResourceNotes();
    }

    @Override
    public Set<PResourceAnnotation> getActiveResourceAnnotations() {
        return isProjectVisible() && isInheritingIdentifierInformation() ? project.getResourceAnnotations() : getResourceAnnotations();
    }

    public Set<PCultureKeyword> getActiveUncontrolledCultureKeywords() {
        return isProjectVisible() && isInheritingCulturalInformation() ? project.getUncontrolledCultureKeywords() : getUncontrolledCultureKeywords();
    }

    @Override
    public Set<PGeographicKeyword> getActiveGeographicKeywords() {
        return isProjectVisible() && isInheritingSpatialInformation() ? project.getGeographicKeywords() : getGeographicKeywords();
    }

    @Override
    public Set<PGeographicKeyword> getActiveManagedGeographicKeywords() {
        return isProjectVisible() && isInheritingSpatialInformation() ? project.getManagedGeographicKeywords() : getManagedGeographicKeywords();
    }

    @Override
    public Set<PLatitudeLongitudeBox> getActiveLatitudeLongitudeBoxes() {
        return isProjectVisible() && isInheritingSpatialInformation() ? project.getLatitudeLongitudeBoxes() : getLatitudeLongitudeBoxes();
    }

    @Override
    public Set<PTemporalKeyword> getActiveTemporalKeywords() {
        return isProjectVisible() && isInheritingTemporalInformation() ? project.getTemporalKeywords() : getTemporalKeywords();
    }

    @Override
    public Set<PCoverageDate> getActiveCoverageDates() {
        return isProjectVisible() && isInheritingTemporalInformation() ? project.getCoverageDates() : getCoverageDates();
    }

    @Transient
    @Override
    public boolean hasConfidentialFiles() {
        return !getConfidentialFiles().isEmpty();
    }

    @Transient
    @Override
    public boolean hasEmbargoedFiles() {
        for (PInformationResourceFile file : getConfidentialFiles()) {
            if (file.isEmbargoed()) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public List<PInformationResourceFile> getFilesWithRestrictions(boolean confidential) {
        List<PInformationResourceFile> confidentialFiles = new ArrayList<PInformationResourceFile>();
        List<PInformationResourceFile> publicFiles = new ArrayList<PInformationResourceFile>();
        for (PInformationResourceFile irFile : getNonDeletedFiles()) {
            if (irFile.isPublic()) {
                publicFiles.add(irFile);
            } else {
                confidentialFiles.add(irFile);
            }
        }
        if (confidential) {
            return confidentialFiles;
        } else {
            return publicFiles;
        }
    }

    @Transient
    public List<PInformationResourceFile> getConfidentialFiles() {
        return getFilesWithRestrictions(true);
    }

    @Transient
    @XmlTransient
    public List<PInformationResourceFile> getPublicFiles() {
        return getFilesWithRestrictions(false);
    }

    public PDataTableColumn getMappedDataKeyColumn() {
        return mappedDataKeyColumn;
    }

    public void setMappedDataKeyColumn(PDataTableColumn mappedDataKeyColumn) {
        this.mappedDataKeyColumn = mappedDataKeyColumn;
    }

    public String getMappedDataKeyValue() {
        return mappedDataKeyValue;
    }

    public void setMappedDataKeyValue(String mappedDataKeyValue) {
        this.mappedDataKeyValue = mappedDataKeyValue;
    }

    @Transient
    @XmlTransient
    public boolean isInheritingSomeMetadata() {
        return (inheritingCulturalInformation || inheritingInvestigationInformation || inheritingMaterialInformation || inheritingOtherInformation ||
                inheritingSiteInformation || inheritingSpatialInformation || inheritingTemporalInformation || inheritingIdentifierInformation
                || inheritingNoteInformation || inheritingIndividualAndInstitutionalCredit);
    }


    @Override
    @XmlTransient
    public List<PCreator<?>> getRelatedCreators() {
        List<PCreator<?>> creators = super.getRelatedCreators();
        creators.add(getResourceProviderInstitution());
        creators.add(getPublisher());
        return creators;
    }

    public boolean isInheritingNoteInformation() {
        return inheritingNoteInformation;
    }

    public void setInheritingNoteInformation(boolean inheritingNoteInformation) {
        this.inheritingNoteInformation = inheritingNoteInformation;
    }

    public boolean isInheritingIdentifierInformation() {
        return inheritingIdentifierInformation;
    }

    public void setInheritingIdentifierInformation(boolean inheritingIdentifierInformation) {
        this.inheritingIdentifierInformation = inheritingIdentifierInformation;
    }

    public boolean isInheritingCollectionInformation() {
        return inheritingCollectionInformation;
    }

    public void setInheritingCollectionInformation(boolean inheritingCollectionInformation) {
        this.inheritingCollectionInformation = inheritingCollectionInformation;
    }

    // shortcut for non-deleted, visible files
    @Transient
    @XmlTransient
    public List<PInformationResourceFile> getVisibleFilesWithThumbnails() {
        ArrayList<PInformationResourceFile> visibleFiles = new ArrayList<PInformationResourceFile>();
        for (PInformationResourceFile irfile : getVisibleFiles()) {
            if (logger.isTraceEnabled()) {
                logger.debug("{}", irfile.getLatestThumbnail());
            }
            if (irfile.getLatestThumbnail() != null) {
                visibleFiles.add(irfile);
            }
        }
        return visibleFiles;
    }

    // shortcut for non-deleted, visible files
    @Transient
    @XmlTransient
    public List<PInformationResourceFile> getVisibleFiles() {
        ArrayList<PInformationResourceFile> visibleFiles = new ArrayList<PInformationResourceFile>();
        for (PInformationResourceFile irfile : getInformationResourceFiles()) {
            if (logger.isTraceEnabled()) {
                logger.trace("{} ({} {} )", irfile, irfile.isViewable(), irfile.isDeleted());
            }
            if (irfile.isViewable() && !irfile.isDeleted()) {
                visibleFiles.add(irfile);
            }
        }
        Collections.sort(visibleFiles);
        return visibleFiles;
    }

    private transient PInformationResourceFileVersion primaryThumbnail = null;
    private transient Boolean hasPrimaryThumbnail = null;

    // get the latest version of the first non-deleted thumbnail (or null)
    @Transient
    @XmlTransient
    public PInformationResourceFileVersion getPrimaryThumbnail() {
        if (hasPrimaryThumbnail != null) {
            return primaryThumbnail;
        }
        hasPrimaryThumbnail = Boolean.FALSE;

        List<PInformationResourceFile> visibleFilesWithThumbnails = getVisibleFilesWithThumbnails();
        if (CollectionUtils.isNotEmpty(visibleFilesWithThumbnails)) {
            hasPrimaryThumbnail = Boolean.TRUE;
            primaryThumbnail = visibleFilesWithThumbnails.get(0).getLatestThumbnail();
            return primaryThumbnail;
        } else {
            return null;
        }
    }

    @Transient
    @XmlTransient
    public List<PInformationResourceFile> getNonDeletedFiles() {
        List<PInformationResourceFile> files = new ArrayList<PInformationResourceFile>();
        for (PInformationResourceFile irf : getInformationResourceFiles()) {
            if (!irf.isDeleted()) {
                files.add(irf);
            }
        }
        return files;
    }

    @Transient
    @Override
    // we consider a record to be citation record if it doesn't have any file attachments.
    public boolean isCitationRecord() {
        return getResourceAccessType() == ResourceAccessType.CITATION;
    }

    public PInstitution getPublisher() {
        return publisher;
    }

    public void setPublisher(PInstitution publisher) {
        this.publisher = publisher;
    }

    public String getPublisherLocation() {
        return publisherLocation;
    }

    public void setPublisherLocation(String publisherLocation) {
        this.publisherLocation = publisherLocation;
    }

    public String getPublisherName() {
        if (publisher != null) {
            return publisher.getName();
        }
        return null;
    }

    @Override
    public <R extends PResource> void copyImmutableFieldsFrom(R resource_) {
        super.copyImmutableFieldsFrom(resource_);
        PInformationResource resource = (PInformationResource) resource_;
        this.getInformationResourceFiles().addAll(new HashSet<PInformationResourceFile>(resource.getInformationResourceFiles()));
        this.setPublisher(resource.getPublisher());
        this.setResourceProviderInstitution(resource.getResourceProviderInstitution());

    };

    @XmlTransient
    public List<PInformationResourceFile> getFilesWithProcessingErrors() {
        List<PInformationResourceFile> files = new ArrayList<PInformationResourceFile>();
        for (PInformationResourceFile file : getInformationResourceFiles()) {
            if ((file.getStatus() == FileStatus.PROCESSING_ERROR) || (file.getStatus() == FileStatus.PROCESSING_WARNING)) {
                files.add(file);
            }
        }
        return files;
    }

    @XmlTransient
    public List<PInformationResourceFile> getFilesWithFatalProcessingErrors() {
        List<PInformationResourceFile> files = new ArrayList<PInformationResourceFile>();
        for (PInformationResourceFile file : getInformationResourceFiles()) {
            if (file.getStatus() == FileStatus.PROCESSING_ERROR) {
                files.add(file);
            }
        }
        return files;
    }

    @Transient
    @XmlTransient
    public Set<PResourceCreator> getContacts() {
        return getResourceCreators(ResourceCreatorRole.CONTACT);
    }

    /**
     * Override this if you need to pass resource specific information on to the work flow process.
     * Make a new instance of the resource, and then copy the fields across that will be needed by the work flow process
     * 
     * @see PArchive#getTransientCopyForWorkflow() for an implementation
     * @return <b>The default is null!</b> A copy of the information resource that will be serialised and sent to the work flow.
     */
    @SuppressWarnings("static-method")
    @Transient
    @XmlTransient
    public PInformationResource getTransientCopyForWorkflow() {
        return null;
    }

    /**
     * Override this method to write back the fields that may have been changed in the transient copy
     * 
     * @param transientCopy
     */
    public void updateFromTransientResource(PInformationResource transientCopy) {
        // Should we throw an exception if we are here ?
    }

    public boolean isInheritingIndividualAndInstitutionalCredit() {
        return inheritingIndividualAndInstitutionalCredit;
    }

    public void setInheritingIndividualAndInstitutionalCredit(
            boolean inheritingIndividualAndInstitutionalCredit) {
        this.inheritingIndividualAndInstitutionalCredit = inheritingIndividualAndInstitutionalCredit;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    @XmlTransient
    @JsonIgnore
    public ResourceAccessType getTransientAccessType() {
        return transientAccessType;
    }

    public void setTransientAccessType(ResourceAccessType transientAccessType) {
        this.transientAccessType = transientAccessType;
    }
}
