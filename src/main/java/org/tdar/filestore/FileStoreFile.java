package org.tdar.filestore;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Transient;

import org.tdar.core.bean.resource.InformationResourceFileVersion;
import org.tdar.core.bean.resource.VersionType;

public class FileStoreFile implements Serializable, FileStoreFileProxy {

    private static final long serialVersionUID = -3168636719632062521L;

    private String filename;
    private String checksum;
    private String checksumType;
    private File transientFile;
    private Long persistableId;
    private DirectoryType type;
    private Long informationResourceFileId;
    private Long informationResourceFileVersionId;
    private Integer version;

    private VersionType versionType;

    public FileStoreFile() {

    }

    public enum DirectoryType {
        IMAGE,
        SUPPORT;
    }

    public FileStoreFile(DirectoryType type, Long id, String filename) {
        this.persistableId = id;
        this.filename = filename;
        this.type = type;
    }

    public FileStoreFile(Long id, InformationResourceFileVersion v) {
        this.filename = v.getFilename();
        this.checksum = v.getChecksum();
        this.checksumType = v.getChecksumType();
        this.persistableId = id;
        this.informationResourceFileId = v.getInformationResourceFileId();
        this.informationResourceFileVersionId = v.getId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#getFilename()
     */
    @Override
    public String getFilename() {
        return filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#setFilename(java.lang.String)
     */
    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#getChecksum()
     */
    @Override
    public String getChecksum() {
        return checksum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#setChecksum(java.lang.String)
     */
    @Override
    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#getChecksumType()
     */
    @Override
    public String getChecksumType() {
        return checksumType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#setChecksumType(java.lang.String)
     */
    @Override
    public void setChecksumType(String checksumType) {
        this.checksumType = checksumType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#getTransientFile()
     */
    @Override
    public File getTransientFile() {
        return transientFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#setTransientFile(java.io.File)
     */
    @Override
    public void setTransientFile(File transientFile) {
        this.transientFile = transientFile;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#getPersistableId()
     */
    @Override
    public Long getPersistableId() {
        return persistableId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.tdar.filestore.FileStoreFileProxy#setPersistableId(java.lang.Long)
     */
    public void setPersistableId(Long persistableId) {
        this.persistableId = persistableId;
    }

    public DirectoryType getType() {
        return type;
    }

    public void setType(DirectoryType type) {
        this.type = type;
    }

    @Override
    public Long getInformationResourceFileId() {
        return informationResourceFileId;
    }

    public void setInformationResourceFileId(Long informationResourceFileId) {
        this.informationResourceFileId = informationResourceFileId;
    }

    @Override
    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public boolean isUploaded() {
        return versionType.isUploaded();
    }

    @Override
    public boolean isArchival() {
        return versionType.isArchival();
    }

    @Transient
    @Override
    public boolean isTranslated() {
        return (versionType == VersionType.TRANSLATED);
    }

    @Override
    public boolean isDerivative() {
        return versionType.isDerivative();
    }

    public Long getInformationResourceFileVersionId() {
        return informationResourceFileVersionId;
    }

    public void setInformationResourceFileVersionId(Long informationResourceFileVersionId) {
        this.informationResourceFileVersionId = informationResourceFileVersionId;
    }

    public void setFileVersionType(VersionType uploadedArchival) {
        this.versionType = uploadedArchival;
    }

}
