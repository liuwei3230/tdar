package org.tdar.core.bean.resource;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tdar.core.bean.entity.Institution;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.serialize.entity.PInstitution;
import org.tdar.core.serialize.entity.PPerson;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.resource.PDocument;
import org.tdar.utils.ResourceCitationFormatter;

public class DocumentCitationFormatTestCase {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testBookWithSeries() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.BOOK);
        assertEquals("seriesName ,seriesNumber. publisherLocation: publisher. 1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testBookWithPublisherPeriod() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.BOOK);
        doc.getPublisher().setName("publisher.");
        assertEquals("seriesName ,seriesNumber. publisherLocation: publisher. 1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testBook() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.BOOK);
        doc.setSeriesName(null);
        doc.setSeriesNumber(null);
        assertEquals("publisherLocation: publisher. 1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testBookChapter() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.BOOK_SECTION);
        assertEquals("In bookTitle. Pp. startPage-endPage. publisherLocation: publisher. 1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testConferencePresentation() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.CONFERENCE_PRESENTATION);
        assertEquals("Presented at publisher, publisherLocation. 1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testJournalArticle() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.JOURNAL_ARTICLE);
        assertEquals("journalName. volume (journalNumber): startPage-endPage. 1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testOther() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.OTHER);
        assertEquals("1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testReport() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.REPORT);
        assertEquals("1234", formatter.getFormattedSourceInformation());
    }

    @Test
    public void testThesis() {
        PDocument doc = new PDocument();
        ResourceCitationFormatter formatter = setupDocumentWithAllFields(doc, DocumentType.THESIS);
        assertEquals("Masters Thesis. publisher, publisherLocation. 1234", formatter.getFormattedSourceInformation());
    }

    public static ResourceCitationFormatter setupDocumentWithAllFields(PDocument document, DocumentType type) {
        ResourceCitationFormatter formatter = new ResourceCitationFormatter(document);
        document.getResourceCreators().add(new PResourceCreator(new PPerson("First", "Last", "first@last"), ResourceCreatorRole.AUTHOR));
        document.getResourceCreators().add(new PResourceCreator(new PInstitution("institution auth"), ResourceCreatorRole.AUTHOR));
        document.getResourceCreators().add(new PResourceCreator(new PPerson("First2", "Last2", "first2@last"), ResourceCreatorRole.EDITOR));
        document.getResourceCreators().add(new PResourceCreator(new PInstitution("Collaborating institution"), ResourceCreatorRole.COLLABORATOR));
        document.setDocumentType(type);
        document.setDate(1234);
        if ((document.getId() == null) || (document.getId() == -1)) {
            document.setId(123456L);
        }
        document.setDescription("description");
        document.setBookTitle("bookTitle");
        document.setDegree(DegreeType.MASTERS);
        document.setDoi("doi");
        document.setEdition("edition");
        document.setEndPage("endPage");
        document.setIsbn("isbn");
        document.setIssn("issn");
        document.setJournalName("journalName");
        document.setJournalNumber("journalNumber");
        document.setNumberOfPages(10);
        document.setNumberOfVolumes(2);
        document.setPublisher(new PInstitution("publisher"));
        document.setPublisherLocation("publisherLocation");
        document.setSeriesName("seriesName");
        document.setSeriesNumber("seriesNumber");
        document.setStartPage("startPage");
        document.setVolume("volume");
        return formatter;
    }

}
