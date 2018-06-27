package org.tdar.core.serialize.resource;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.collections4.CollectionUtils;
import org.tdar.core.bean.resource.DegreeType;
import org.tdar.core.bean.resource.DocumentSubType;
import org.tdar.core.bean.resource.DocumentType;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.serialize.resource.file.PInformationResourceFile;

/**
 * Represents a Document information resource.
 * 
 * The design decision was made to have null fields instead of overloading fields to mean different things for different
 * document types, e.g., a journal article has journal volume, journal name, and journal number instead of series name, series number,
 * and volume / # of volumes
 * 
 * NOTE: uses Resource.dateCreated as year published field
 * 
 * 
 * @author <a href='mailto:Allen.Lee@asu.edu'>Allen Lee</a>
 * @version $Rev$
 */
@XmlRootElement(name = "document")
public class PDocument extends PInformationResource {


    private DocumentType documentType;
    private DocumentSubType documentSubType;
    private DegreeType degree;
    private String seriesName;
    private String seriesNumber;
    private Integer numberOfPages;
    private String edition;
    private String isbn;
    private String bookTitle;
    private String issn;
    private String startPage;
    private String endPage;
    private String journalName;
    private String volume;
    private Integer numberOfVolumes;
    private String journalNumber;

    public PDocument() {
        setResourceType(ResourceType.DOCUMENT);
        setDocumentType(DocumentType.BOOK);
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String type) {
        setDocumentType(DocumentType.valueOf(type));
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getSeriesNumber() {
        return seriesNumber;
    }

    public void setSeriesNumber(String seriesNumber) {
        this.seriesNumber = seriesNumber;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getNumberOfVolumes() {
        return numberOfVolumes;
    }

    public void setNumberOfVolumes(Integer numberOfVolumes) {
        this.numberOfVolumes = numberOfVolumes;
    }

    @Deprecated
    public Integer getNumberOfPages() {
        return numberOfPages;
    }

    public Integer getTotalNumberOfPages() {
        Integer count = 0;
        if (CollectionUtils.isNotEmpty(getInformationResourceFiles())) {
            for (PInformationResourceFile file : getInformationResourceFiles()) {
                if (!file.isDeleted() && (file.getNumberOfParts() != null)) {
                    count += file.getNumberOfParts();
                }
            }
            if (count > 0) {
                return count;
            }
        }
        return numberOfPages;
    }

    public void setNumberOfPages(Integer numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    public String getEndPage() {
        return endPage;
    }

    public void setEndPage(String endPage) {
        this.endPage = endPage;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public String getJournalNumber() {
        return journalNumber;
    }

    public void setJournalNumber(String journalNumber) {
        this.journalNumber = journalNumber;
    }

    public DegreeType getDegree() {
        return degree;
    }

    public void setDegree(DegreeType degree) {
        this.degree = degree;
    }

    @Override
    public boolean isSupportsThumbnails() {
        return true;
    }

    public DocumentSubType getDocumentSubType() {
        return documentSubType;
    }

    public void setDocumentSubType(DocumentSubType documentSubType) {
        this.documentSubType = documentSubType;
    }
}
