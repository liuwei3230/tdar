package org.tdar.core.serialize.resource.file;

import java.io.File;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.AbstractPersistable;
import org.tdar.core.bean.FieldLength;
import org.tdar.core.bean.Viewable;
import org.tdar.core.bean.resource.file.VersionType;
import org.tdar.filestore.FileStoreFileProxy;
import org.tdar.filestore.FilestoreObjectType;

public class PInformationResourceFileVersion extends AbstractPersistable implements Comparable<PInformationResourceFileVersion>, Viewable, HasExtension,
        FileStoreFileProxy {

    private final transient Logger logger = LoggerFactory.getLogger(getClass());
    private transient File transientFile;
    private PInformationResourceFile informationResourceFile;
    private String filename;
    private Integer version;
    private String mimeType;
    private String format;
    private Boolean primaryFile = Boolean.FALSE;
    private String extension;
    private String premisId;
    private String filestoreId;
    private String checksum;
    private String checksumType;
    private Date dateCreated;
    private String fileType;
    private VersionType fileVersionType;

    private Integer width;

    private Integer height;

    @Column(name = "total_time")
    private Long totalTime;

    @Column(name = "size")
    private Long fileLength;

    // uncompressed size of file on DISK
    @Column(name = "effective_size")
    private Long uncompressedSizeOnDisk;

    @Length(max = FieldLength.FIELD_LENGTH_255)
    private String path;

    @Transient
    private Long informationResourceId;
    @Transient
    private Long informationResourceFileId;

    private transient boolean viewable = false;

    /*
     * This constructor exists only for Hibernate ... another constructor should
     * be used outside of Hibernate
     */
    @Deprecated
    public PInformationResourceFileVersion() {
    }

    public PInformationResourceFileVersion(VersionType type, String filename, Integer version, Long infoResId, Long irFileId) {
        setFileVersionType(type);
        setFilename(filename);
        setVersion(version);
        setExtension(FilenameUtils.getExtension(filename));
        setInformationResourceId(infoResId);
        setInformationResourceFileId(irFileId);
    }

    public PInformationResourceFileVersion(VersionType type, String filename, PInformationResourceFile irFile) {
        setFileVersionType(type);
        setFilename(filename);
        setExtension(FilenameUtils.getExtension(filename));
        if (irFile != null) {
            setVersion(irFile.getLatestVersion());
            setInformationResourceFile(irFile);
            setInformationResourceFileId(irFile.getId());
            setInformationResourceId(irFile.getInformationResource().getId());
        }
        setDateCreated(new Date());
    }

    @XmlTransient
    public PInformationResourceFile getInformationResourceFile() {
        return informationResourceFile;
    }

    public void setInformationResourceFile(PInformationResourceFile informationResourceFile) {
        this.informationResourceFile = informationResourceFile;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @XmlAttribute(name = "version")
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPremisId() {
        return premisId;
    }

    public void setPremisId(String premisId) {
        this.premisId = premisId;
    }

    public String getFilestoreId() {
        return filestoreId;
    }

    public void setFilestoreId(String filestoreId) {
        this.filestoreId = filestoreId;
    }

    @Override
    public String getChecksum() {
        return checksum;
    }

    /*
     * Only set the checksum if it is not set
     */
    @Override
    public void setChecksum(String checksum) {
        if (StringUtils.isEmpty(this.checksum)) {
            this.checksum = checksum;
        } else {
            logger.info("not setting checksum to :" + checksum + " b/c set to :" + this.checksum);
        }
    }

    public void overrideChecksum(String checksum) {
        this.checksum = checksum;
    }

    @Override
    public String getChecksumType() {
        return checksumType;
    }

    @Override
    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public VersionType getFileVersionType() {
        return fileVersionType;
    }

    public void setFileVersionType(VersionType informationResourceFileVersionType) {
        this.fileVersionType = informationResourceFileVersionType;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Long getFileLength() {
        return fileLength;
    }

    public void setFileLength(Long size) {
        this.fileLength = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Transient
    public boolean isTranslated() {
        return (getFileVersionType() == VersionType.TRANSLATED);
    }

    @Transient
    public boolean isUploaded() {
        return getFileVersionType().isUploaded();
    }

    @Transient
    public boolean isUploadedOrArchival() {
        return (isArchival() || isUploaded());
    }

    @Transient
    public boolean isArchival() {
        return getFileVersionType().isArchival();
    }

    @Transient
    public boolean isThumbnail() {
        return (getFileVersionType() == VersionType.WEB_SMALL);
    }

    /**
     * @return
     */
    public boolean isDerivative() {
        return getFileVersionType().isDerivative();
    }

    /**
     * @return
     */
    public boolean isIndexable() {
        return (getFileVersionType() == VersionType.INDEXABLE_TEXT);
    }

    /**
     * @param informationResourceFileId
     *            the informationResourceFileId to set
     */
    public void setInformationResourceFileId(Long informationResourceFileId) {
        this.informationResourceFileId = informationResourceFileId;
    }

    /**
     * Convenience Method to get at parent information. This will allow the bean
     * to be passed information in two different ways but the filestore to
     * access the raw information from the same core method.
     * 
     * @return the informationResourceFileId
     */
    @XmlAttribute(name = "informationResourceFileId")
    public Long getInformationResourceFileId() {
        if (informationResourceFile != null) {
            return informationResourceFile.getId();
        }
        return informationResourceFileId;
    }

    /**
     * @param informationResourceId
     *            the informationResourceId to set
     */
    public void setInformationResourceId(Long informationResourceId) {
        this.informationResourceId = informationResourceId;
    }

    /**
     * Convenience Method to get at parent information. This will allow the bean
     * to be passed information in two different ways but the filestore to
     * access the raw information from the same core method.
     * 
     * @return the informationResourceId
     */
    @XmlAttribute(name = "informationResourceId")
    @Transient
    public Long getInformationResourceId() {
        if ((informationResourceFile != null) && (informationResourceFile.getInformationResource() != null)) {
            return informationResourceFile.getInformationResource().getId();
        }
        return informationResourceId;
    }

    @Override
    public String toString() {
        return String.format("%s (%s, #%d | %d)", filename, fileVersionType, version, getInformationResourceFileId());
    }

    @Override
    public int compareTo(PInformationResourceFileVersion other) {
        int comparison = -1;
        logger.trace("comparing: " + other + " to " + this);
        if (equals(other)) { // exactly equal
            comparison = 0;
        } else {
            comparison = getVersion().compareTo(other.getVersion());
            if (comparison == 0) {
                comparison = getFilename().compareTo(other.getFilename());
                if (comparison == 0) {
                    comparison = getFileVersionType().compareTo(other.getFileVersionType());
                }
            }
        }
        logger.trace("result: " + comparison);
        return comparison;
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

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Long getUncompressedSizeOnDisk() {
        return uncompressedSizeOnDisk;
    }

    public void setUncompressedSizeOnDisk(Long actualSizeOnDisk) {
        this.uncompressedSizeOnDisk = actualSizeOnDisk;
    }

    public boolean isPrimaryFile() {
        if (primaryFile == null) {
            return false;
        }
        return primaryFile;
    }

    public void setPrimaryFile(boolean primaryFile) {
        this.primaryFile = primaryFile;
    }

    @Override
    public File getTransientFile() {
        return transientFile;
    }

    @Override
    public void setTransientFile(File transientFile) {
        this.transientFile = transientFile;
    }

    @Override
    public Long getPersistableId() {
        return getInformationResourceId();
    }

    @Override
    public FilestoreObjectType getType() {
        return FilestoreObjectType.RESOURCE;
    }

    @Override
    public VersionType getVersionType() {
        return getFileVersionType();
    }

}
