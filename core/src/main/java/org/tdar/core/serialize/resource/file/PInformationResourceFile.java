package org.tdar.core.serialize.resource.file;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractSequenced;
import org.tdar.core.bean.Indexable;
import org.tdar.core.bean.Viewable;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.bean.resource.file.FileAccessRestriction;
import org.tdar.core.bean.resource.file.FileStatus;
import org.tdar.core.bean.resource.file.FileType;
import org.tdar.core.bean.resource.file.PreservationStatus;
import org.tdar.core.bean.resource.file.VersionType;
import org.tdar.filestore.WorkflowContext;
import org.tdar.utils.jaxb.converters.JaxbPersistableConverter;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PInformationResourceFile extends AbstractSequenced<PInformationResourceFile> implements Viewable, Indexable {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());

    private transient WorkflowContext workflowContext;
    private PInformationResource informationResource;
    private transient Long transientDownloadCount;
    private String description;
    private Date fileCreatedDate;
    private Boolean partOfComposite = Boolean.FALSE;
    private Boolean deleted = Boolean.FALSE;
    private FileType informationResourceFileType;
    private Integer latestVersion = 0;
    private Integer numberOfParts = 0;
    private PreservationStatus preservationStatus;
    private String preservationNote;
    private String filename;
    private SortedSet<PInformationResourceFileVersion> informationResourceFileVersions = new TreeSet<PInformationResourceFileVersion>();
    private FileAccessRestriction restriction = FileAccessRestriction.PUBLIC;
    private String errorMessage;
    private Date dateMadePublic;
    private FileStatus status;

    @XmlElement(name = "informationResourceRef")
    @XmlJavaTypeAdapter(JaxbPersistableConverter.class)
    public PInformationResource getInformationResource() {
        return informationResource;
    }

    private transient boolean viewable = false;

    public PInformationResourceFile() {
    }

    public PInformationResourceFile(FileStatus status, Collection<PInformationResourceFileVersion> versions) {
        setStatus(status);
        if (CollectionUtils.isNotEmpty(versions)) {
            getInformationResourceFileVersions().addAll(versions);
        }
    }

    public void setInformationResource(PInformationResource informationResource) {
        this.informationResource = informationResource;
    }

    public FileType getInformationResourceFileType() {
        return informationResourceFileType;
    }

    public void setInformationResourceFileType(FileType informationResourceFileType) {
        this.informationResourceFileType = informationResourceFileType;
    }

    @XmlAttribute(name = "latestVersion")
    public Integer getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(Integer latestVersion) {
        this.latestVersion = latestVersion;
    }

    @XmlElementWrapper(name = "informationResourceFileVersions")
    @XmlElement(name = "informationResourceFileVersion")
    public SortedSet<PInformationResourceFileVersion> getInformationResourceFileVersions() {
        return informationResourceFileVersions;
    }

    public void setInformationResourceFileVersions(
            SortedSet<PInformationResourceFileVersion> informationResourceFileVersions) {
        this.informationResourceFileVersions = informationResourceFileVersions;
    }

    @Transient
    @XmlTransient
    public PInformationResourceFileVersion getTranslatedFile() {
        return getVersion(getLatestVersion(), VersionType.TRANSLATED);
    }

    @Transient
    @XmlTransient
    public PInformationResourceFileVersion getIndexableVersion() {
        return getVersion(getLatestVersion(), VersionType.INDEXABLE_TEXT);
    }

    public void addFileVersion(PInformationResourceFileVersion version) {
        getInformationResourceFileVersions().add(version);
    }

    public void incrementVersionNumber() {
        if (latestVersion == null) {
            latestVersion = 0;
        }
        latestVersion++;
    }

    @Transient
    @XmlTransient
    public Collection<PInformationResourceFileVersion> getLatestVersions() {
        return getVersions(getLatestVersion());
    }

    public Collection<PInformationResourceFileVersion> getVersions(int version) {
        ArrayList<PInformationResourceFileVersion> files = new ArrayList<PInformationResourceFileVersion>();
        for (PInformationResourceFileVersion irfv : getInformationResourceFileVersions()) {
            if (irfv.getVersion().equals(version)) {
                files.add(irfv);
            }
        }
        return files;
    }

    @Transient
    public PInformationResourceFileVersion getLatestTranslatedVersion() {
        for (PInformationResourceFileVersion version : getInformationResourceFileVersions()) {
            if (version.getVersion().equals(getLatestVersion()) && version.isTranslated()) {
                logger.trace("version: {}", version);
                return version;
            }
        }
        return null;
    }

    @Transient
    @XmlTransient
    public PInformationResourceFileVersion getLatestPDF() {
        for (PInformationResourceFileVersion version : getInformationResourceFileVersions()) {
            if (version.getVersion().equals(getLatestVersion()) && version.getExtension().equalsIgnoreCase("pdf")) {
                ;
            }
            return version;
        }
        return null;
    }

    @Transient
    @XmlTransient
    public PInformationResourceFileVersion getLatestThumbnail() {
        for (PInformationResourceFileVersion version : getInformationResourceFileVersions()) {
            if (version.getVersion().equals(latestVersion) && version.isThumbnail()) {
                return version;
            }
        }
        return null;
    }

    @Transient
    @XmlTransient
    public PInformationResourceFileVersion getLatestArchival() {
        logger.debug("looking for latest archival version in {} with version number {}", getInformationResourceFileVersions(), latestVersion);
        for (PInformationResourceFileVersion version : getInformationResourceFileVersions()) {
            if (version.getVersion().equals(latestVersion) && version.isArchival()) {
                return version;
            }
        }
        return null;
    }

    @JsonIgnore
    public PInformationResourceFileVersion getLatestUploadedVersion() {
        return getUploadedVersion(getLatestVersion());
    }

    public PInformationResourceFileVersion getUploadedVersion(Integer versionNumber) {
        return getVersion(versionNumber, VersionType.UPLOADED_ARCHIVAL, VersionType.UPLOADED_TEXT, VersionType.UPLOADED);
    }

    /* Use for Ontology (or perhaps coding sheet); will need to verify that this does not break things when we have true archival version */
    @JsonIgnore
    public PInformationResourceFileVersion getLatestUploadedOrArchivalVersion() {
        return getVersion(getLatestVersion(), VersionType.UPLOADED, VersionType.UPLOADED_TEXT, VersionType.UPLOADED_ARCHIVAL, VersionType.ARCHIVAL);
    }

    // FIXME: improve efficiency
    public PInformationResourceFileVersion getVersion(Integer versionNumber_, VersionType... types) {
        Integer versionNumber = versionNumber_;
        int currentVersionNumber = -1;
        Set<PInformationResourceFileVersion> versions = getInformationResourceFileVersions();
        if ((versionNumber == null) || (versionNumber == -1)) {
            // FIXME: why not just set versionNumber = latestVersion?
            for (PInformationResourceFileVersion file : versions) {
                if (file.getVersion().intValue() > currentVersionNumber) {
                    currentVersionNumber = file.getVersion().intValue();
                }
            }
            versionNumber = Integer.valueOf(currentVersionNumber);
            logger.debug("assuming current version is: {}", currentVersionNumber);
        }

        for (PInformationResourceFileVersion file : versions) {
            if (file.getVersion().equals(versionNumber)) {
                for (VersionType type : types) {
                    if (file.getFileVersionType() == type) {
                        return file;
                    }
                }
            }
        }
        logger.trace("getVersion({}, {}) couldn't find an appropriate file version", versionNumber, Arrays.asList(types));
        return null;
    }

    /**
     * @param toDelete
     */
    public void removeAll(List<PInformationResourceFileVersion> toDelete) {
        this.informationResourceFileVersions.removeAll(toDelete);
    }

    /**
     * Get the latest version of the specified file version type. If the specified type is WEB_LARGE, return the best approximation if no WEB_LARGE version
     * is available.
     * 
     * @param type
     * @return latest version of the specified type, or null if no version of the specified type is available
     */
    public PInformationResourceFileVersion getCurrentVersion(VersionType type) {
        PInformationResourceFileVersion currentVersion = null;
        for (PInformationResourceFileVersion latestVersion : getLatestVersions()) {
            if (type == latestVersion.getFileVersionType()) {
                currentVersion = latestVersion;
                // if the type is exact, break and return out
                break;
            }
        }
        return currentVersion;
    }

    public PInformationResourceFileVersion getZoomableVersion() {
        PInformationResourceFileVersion currentVersion = null;
        for (PInformationResourceFileVersion latestVersion : getLatestVersions()) {
            logger.trace("latest version {}", latestVersion);
            switch (latestVersion.getFileVersionType()) {
                // FIXME: if no WEB_MEDIUM is available we probably want to return a WEB_SMALL if possible.
                case WEB_LARGE:
                    currentVersion = latestVersion;
                    break;
                case WEB_MEDIUM:
                    if (currentVersion == null) {
                        currentVersion = latestVersion;
                    }
                    break;
                case WEB_SMALL:
                    break;
                default:
                    break;
            }
        }
        return currentVersion;
    }

    @Transient
    @XmlTransient
    public boolean isConfidential() {
        return restriction == FileAccessRestriction.CONFIDENTIAL;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    }

    @Transient
    @XmlTransient
    public boolean isProcessed() {
        return status == FileStatus.PROCESSED;
    }

    public boolean isDeleted() {
        if (deleted == null) {
            return false;
        }
        return deleted;
    }

    @Transient
    @XmlTransient
    public boolean isErrored() {
        return status == FileStatus.PROCESSING_ERROR;
    }

    @Transient
    @XmlTransient
    public boolean isPublic() {
        // this had a "DELETED" check; but it really needs to just be "is public or not"
        if (restriction == FileAccessRestriction.PUBLIC) {
            return true;
        }
        return false;
    }

    public void clearStatus() {
        setStatus(null);
    }

    @Override
    public String toString() {
        return String.format("(%d, %s) v#:%s: %s (%s versions)", getId(), status, getLatestVersion(), restriction,
                CollectionUtils.size(informationResourceFileVersions));
    }

    public void clearQueuedStatus() {
        if (status == FileStatus.QUEUED) {
            status = null;
        }
    }

    public boolean isColumnarDataFileType() {
        return getInformationResourceFileType() == FileType.COLUMNAR_DATA;
    }

    @Override
    public boolean isViewable() {
        return viewable;
    }

    @Override
    public void setViewable(boolean accessible) {
        this.viewable = accessible;
    }

    public Long getTransientDownloadCount() {
        return transientDownloadCount;
    }

    public void setTransientDownloadCount(Long transientDownloadCount) {
        this.transientDownloadCount = transientDownloadCount;
    }

    public Integer getNumberOfParts() {
        return numberOfParts;
    }

    public void setNumberOfParts(Integer numberOfParts) {
        this.numberOfParts = numberOfParts;
    }

    @Transient
    @XmlTransient
    public WorkflowContext getWorkflowContext() {
        return workflowContext;
    }

    public void setWorkflowContext(WorkflowContext workflowContext) {
        this.workflowContext = workflowContext;
    }

    public Date getDateMadePublic() {
        return dateMadePublic;
    }

    public void setDateMadePublic(Date dateMadePublic) {
        this.dateMadePublic = dateMadePublic;
    }

    public FileAccessRestriction getRestriction() {
        return restriction;
    }

    public void setRestriction(FileAccessRestriction restriction) {
        this.restriction = restriction;
    }

    public boolean isEmbargoed() {
        return this.restriction != null && this.restriction.isEmbargoed();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isPartOfComposite() {
        if (partOfComposite == null) {
            return false;
        }
        return partOfComposite;
    }

    public void setPartOfComposite(boolean partOfComposite) {
        this.partOfComposite = partOfComposite;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getFileCreatedDate() {
        return fileCreatedDate;
    }

    public void setFileCreatedDate(Date fileCreatedDate) {
        this.fileCreatedDate = fileCreatedDate;
    }

    public boolean isHasTranslatedVersion() {
        try {
            if ((getLatestTranslatedVersion() != null) && getInformationResource().getResourceType().isDataTableSupported()) {
                return true;
            }
        } catch (Exception e) {
            logger.error("cannot tell if file has translated version {}", e);
        }
        return false;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getPreservationNote() {
        return preservationNote;
    }

    public void setPreservationNote(String preservationNote) {
        this.preservationNote = preservationNote;
    }

    public void setPreservationStatus(PreservationStatus preservationStatus) {
        this.preservationStatus = preservationStatus;
    }
}
