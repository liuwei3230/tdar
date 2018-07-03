package org.tdar.transform;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.serialize.resource.PDocument;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.serialize.resource.file.PInformationResourceFile;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.UrlService;

public class ScholarMetadataTransformer implements Serializable {

    private static final long serialVersionUID = 6885895727458189965L;

    public List<MetaTag> convertResourceToMetaTag(PResource resource) {
        List<MetaTag> toReturn = new ArrayList<MetaTag>();
        addMetaTag(toReturn, "citation_title", resource.getTitle());
        for (PResourceCreator creator : resource.getPrimaryCreators()) {
            addMetaTag(toReturn, "citation_author", creator.getCreator().getProperName());
        }
        if (resource instanceof PInformationResource) {
            String publisherField = "DC.publisher";
            PInformationResource ir = (PInformationResource) resource;
            addMetaTag(toReturn, "citation_date", ir.getDate().toString());

            for (PInformationResourceFile file : ir.getInformationResourceFiles()) {
                if (file.getLatestPDF() != null && file.isViewable()) {
                    addMetaTag(toReturn, "citation_pdf_url", UrlService.downloadUrl(ir, file.getLatestPDF()));
                }
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            addMetaTag(toReturn, "citation_online_date", format.format(resource.getDateCreated()));
            if (ir instanceof PDocument) {
                PDocument doc = (PDocument) ir;
                switch (doc.getDocumentType()) {
                    case CONFERENCE_PRESENTATION:
                        publisherField = "citation_conference_title";
                        break;
                    case JOURNAL_ARTICLE:
                        addMetaTagIfNotBlank(toReturn, "citation_journal_title", doc.getJournalName());
                        break;
                    case THESIS:
                        publisherField = "citation_dissertation_institution";
                        break;
                    default:
                        break;
                }
                addMetaTagIfNotBlank(toReturn, "citation_volume", doc.getVolume());
                addMetaTagIfNotBlank(toReturn, "citation_issue", doc.getJournalNumber());
                addMetaTagIfNotBlank(toReturn, "citation_issn", doc.getIssn());
                addMetaTagIfNotBlank(toReturn, "citation_isbn", doc.getIsbn());
                addMetaTagIfNotBlank(toReturn, "citation_firstpage", doc.getStartPage());
                addMetaTagIfNotBlank(toReturn, "citation_lastpage", doc.getEndPage());
            }
            addMetaTagIfNotBlank(toReturn, publisherField, ir.getPublisherName());
        }
        return toReturn;
    }

    private void addMetaTagIfNotBlank(List<MetaTag> toReturn, String name, String val) {
        if (StringUtils.isNotBlank(val)) {
            addMetaTag(toReturn, name, val);
        }
    }

    private void addMetaTag(List<MetaTag> toReturn, String name, String content) {
        toReturn.add(new MetaTag(name, content));
    }

}
