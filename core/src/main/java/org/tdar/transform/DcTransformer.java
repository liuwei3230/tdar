package org.tdar.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.tdar.core.serialize.coverage.PCoverageDate;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.core.serialize.entity.PInstitution;
import org.tdar.core.serialize.entity.PPerson;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.serialize.keyword.PCultureKeyword;
import org.tdar.core.serialize.keyword.PGeographicKeyword;
import org.tdar.core.serialize.keyword.POtherKeyword;
import org.tdar.core.serialize.keyword.PSiteNameKeyword;
import org.tdar.core.serialize.keyword.PTemporalKeyword;
import org.tdar.core.serialize.resource.PArchive;
import org.tdar.core.serialize.resource.PAudio;
import org.tdar.core.serialize.resource.PCodingSheet;
import org.tdar.core.serialize.resource.PDataset;
import org.tdar.core.serialize.resource.PDocument;
import org.tdar.core.bean.resource.DocumentType;
import org.tdar.core.serialize.resource.PGeospatial;
import org.tdar.core.serialize.resource.PImage;
import org.tdar.core.serialize.resource.PInformationResource;
import org.tdar.core.bean.resource.Language;
import org.tdar.core.serialize.resource.POntology;
import org.tdar.core.serialize.resource.PProject;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.bean.resource.ResourceType;
import org.tdar.core.serialize.resource.PSensoryData;
import org.tdar.core.serialize.resource.PVideo;
import org.tdar.core.serialize.resource.file.PInformationResourceFileVersion;
import org.tdar.core.exception.TdarRecoverableRuntimeException;
import org.tdar.core.service.UrlService;
import org.tdar.utils.XmlEscapeHelper;

import edu.asu.lib.dc.DublinCoreDocument;

public abstract class DcTransformer<R extends PResource> implements Transformer<R, DublinCoreDocument> {

    private XmlEscapeHelper x;

    @Override
    public DublinCoreDocument transform(R source) {
        DublinCoreDocument dc = new DublinCoreDocument();
        setX(new XmlEscapeHelper(source.getId()));

        dc.getTitle().add(getX().stripNonValidXMLCharacters(source.getTitle()));

        // add creators and contributors
        List<PResourceCreator> sortedResourceCreators = new ArrayList<>(source.getResourceCreators());
        Collections.sort(sortedResourceCreators);
        for (PResourceCreator resourceCreator : source.getResourceCreators()) {
            String name;
            if (resourceCreator.getCreatorType() == CreatorType.PERSON) {
                // display person names in special format
                name = dcConstructPersonalName(resourceCreator);
            } else {
                // for institution, just display name and role
                name = resourceCreator.toString();
            }

            // FIXME: check this logic
            if (resourceCreator.getRole() == ResourceCreatorRole.AUTHOR) {
                dc.getCreator().add(getX().stripNonValidXMLCharacters(name));
            } else {
                dc.getContributor().add(getX().stripNonValidXMLCharacters(name));
            }
        }

        // add geographic subjects
        for (PGeographicKeyword geoTerm : source.getActiveGeographicKeywords()) {
            dc.getCoverage().add(getX().stripNonValidXMLCharacters(geoTerm.getLabel()));
        }

        // add temporal subjects
        for (PTemporalKeyword temporalTerm : source.getActiveTemporalKeywords()) {
            dc.getCoverage().add(getX().stripNonValidXMLCharacters(temporalTerm.getLabel()));
        }

        // add culture subjects
        for (PCultureKeyword cultureTerm : source.getActiveCultureKeywords()) {
            dc.getSubject().add(getX().stripNonValidXMLCharacters(cultureTerm.getLabel()));
        }

        // add site name subjects
        for (PSiteNameKeyword siteNameTerm : source.getActiveSiteNameKeywords()) {
            dc.getCoverage().add(getX().stripNonValidXMLCharacters(siteNameTerm.getLabel()));
        }

        // add other subjects
        for (POtherKeyword otherTerm : source.getActiveOtherKeywords()) {
            dc.getSubject().add(getX().stripNonValidXMLCharacters(otherTerm.getLabel()));
        }

        dc.getType().add(getX().stripNonValidXMLCharacters(source.getResourceType().getLabel()));

        for (PLatitudeLongitudeBox longLat : source.getActiveLatitudeLongitudeBoxes()) {
            String maxy = "MaxY: ".concat(longLat.getNorth().toString());
            String miny = "MinY: ".concat(longLat.getSouth().toString());
            String maxx = "MaxX: ".concat(longLat.getEast().toString());
            String minx = "MinX: ".concat(longLat.getWest().toString());
            dc.getCoverage().add(String.format("%s, %s, %s, %s", maxy, miny, maxx, minx));
        }

        dc.getIdentifier().add(getX().stripNonValidXMLCharacters(UrlService.absoluteUrl(source)));

        for (PCoverageDate date : source.getCoverageDates()) {
            dc.getCoverage().add(date.toString());
        }

        // TODO: deal with Url here.

        x.logChange();
        return dc;
    }

    protected String dcConstructPersonalName(String firstName, String lastName, String role, String affiliation) {
        String name = String.format("%s, %s", lastName, firstName);
        if (!StringUtils.isEmpty(role)) {
            name += String.format(", %s", role);
        }
        if (!StringUtils.isEmpty(affiliation)) {
            name += String.format(" (%s)", affiliation);
        }
        return name;
    }

    protected String dcConstructPersonalName(PResourceCreator resourceCreator) {
        if (resourceCreator.getCreatorType() != CreatorType.PERSON) {
            return null;
        }
        PPerson person = (PPerson) resourceCreator.getCreator();
        String name = String.format("%s, %s", person.getLastName(), person.getFirstName());
        if (!StringUtils.isEmpty("" + resourceCreator.getRole())) {
            name += String.format(", %s", resourceCreator.getRole());
        }
        if (!StringUtils.isEmpty(person.getInstitutionName())) {
            name += String.format(" (%s)", person.getInstitution());
        }
        return name;
    }

    public static class InformationResourceTransformer<I extends PInformationResource> extends DcTransformer<I> {

        @Override
        public DublinCoreDocument transform(I source) {
            DublinCoreDocument dc = super.transform(source);

            for (PResourceCreator resourceCreator : source.getResourceCreators()) {
                if (resourceCreator.getRole() == ResourceCreatorRole.CONTACT) {
                    dc.getPublisher().add(getX().stripNonValidXMLCharacters(resourceCreator.getCreator().getProperName()));
                }
            }
            if (source.getDate() != null) {
                String dateCreated = source.getDate().toString();
                if (StringUtils.isNotBlank(dateCreated)) {
                    dc.getDate().add(dateCreated);
                }
            }
            Language resourceLanguage = source.getResourceLanguage();
            if (resourceLanguage != null) {
                dc.getLanguage().add(getX().stripNonValidXMLCharacters(resourceLanguage.getCode()));
            }

            if (source.getResourceType().toDcmiTypeString() != null) {
                dc.getType().add(getX().stripNonValidXMLCharacters(source.getResourceType().toDcmiTypeString()));
            }

            for (PInformationResourceFileVersion version : source.getLatestUploadedVersions()) {
                dc.getType().add(getX().stripNonValidXMLCharacters(version.getMimeType()));
            }

            PInstitution resourceProviderInstitution = source.getResourceProviderInstitution();
            if (resourceProviderInstitution != null) {
                dc.getContributor().add(getX().stripNonValidXMLCharacters(resourceProviderInstitution.getName()));
            }

            String publisherLocation = source.getPublisherLocation();

            String pub = "";
            String publisher = source.getPublisherName();
            if (publisher != null) {
                pub += publisher;
            }
            if (publisherLocation != null) {
                pub += ", " + publisherLocation;
            }
            if (!pub.isEmpty()) {
                dc.getPublisher().add(getX().stripNonValidXMLCharacters(pub));
            }

            getX().logChange();
            return dc;
        }

    }

    public static class DocumentTransformer extends InformationResourceTransformer<PDocument> {

        @Override
        public DublinCoreDocument transform(PDocument source) {
            DublinCoreDocument dc = super.transform(source);

            String abst = source.getDescription();
            if (abst != null) {
                dc.getDescription().add(getX().stripNonValidXMLCharacters(abst));
            }

            String doi = source.getDoi();
            if (doi != null) {
                dc.getIdentifier().add(getX().stripNonValidXMLCharacters(doi));
            }

            String copyLocation = source.getCopyLocation();
            if (copyLocation != null) {
                dc.getRelation().add(getX().stripNonValidXMLCharacters(copyLocation));
            }

            String isbn = source.getIsbn();
            if (isbn != null) {
                dc.getIdentifier().add(getX().stripNonValidXMLCharacters(isbn));
            }
            String issn = source.getIssn();
            if (issn != null) {
                dc.getIdentifier().add(getX().stripNonValidXMLCharacters(issn));
            }

            String seriesName = source.getSeriesName();
            String seriesNumber = source.getSeriesNumber();
            String series = "";
            if (seriesName != null) {
                series += seriesName;
            }
            if (seriesNumber != null) {
                series += " #" + seriesNumber;
            }
            if (!series.isEmpty()) {
                dc.getRelation().add(getX().stripNonValidXMLCharacters("Series: " + series));
            }

            String journalName = source.getJournalName();
            String bookTitle = source.getBookTitle();
            String src = "";
            if (journalName != null) {
                src += journalName;
            }
            if (bookTitle != null) {
                src += bookTitle;
            }

            String volume = source.getVolume();
            String journalNumber = source.getJournalNumber();
            String volIssue = "";
            if (volume != null) {
                volIssue += volume;
            }
            if (journalNumber != null) {
                volIssue += String.format("(%s)", journalNumber);
            }

            String startPage = source.getStartPage();
            String endPage = source.getEndPage();
            String pages = "";
            if (startPage != null) {
                pages += startPage + " - ";
            }
            if (endPage != null) {
                pages += endPage;
            }

            if (!volIssue.isEmpty()) {
                src += ", " + volIssue;
            }
            if (!pages.isEmpty()) {
                src += ", " + pages;
            }
            if (!src.isEmpty()) {
                src += ".";
            }

            DocumentType documentType = source.getDocumentType();
            dc.getType().add(getX().stripNonValidXMLCharacters(documentType.getLabel()));
            switch (documentType) {
                case JOURNAL_ARTICLE:
                    if (!src.isEmpty()) {
                        dc.getSource().add(getX().stripNonValidXMLCharacters(src));
                    }
                    break;
                case BOOK_SECTION:
                    if (!src.isEmpty()) {
                        dc.getSource().add(getX().stripNonValidXMLCharacters(src));
                    }
                    break;
                case BOOK:
                    String rel = "";
                    String edition = source.getEdition();
                    if (edition != null) {
                        rel += "Edition: " + edition;
                    }
                    if (volume != null) {
                        rel += " Volume: " + volume;
                    }
                    dc.getRelation().add(getX().stripNonValidXMLCharacters(rel.trim()));
                    break;
                default:
                    break;
            }

            getX().logChange();
            return dc;
        }

    }

    public static class DatasetTransformer extends InformationResourceTransformer<PDataset> {
        // marker class
    }

    public static class SensoryDataTransformer extends InformationResourceTransformer<PSensoryData> {
        // marker class
    }

    public static class VideoTransformer extends InformationResourceTransformer<PVideo> {
        // marker class
    }

    public static class GeospatialTransformer extends InformationResourceTransformer<PGeospatial> {
        // marker class
    }

    public static class CodingSheetTransformer extends InformationResourceTransformer<PCodingSheet> {
        // marker class
    }

    public static class ImageTransformer extends InformationResourceTransformer<PImage> {
        // marker class
    }

    public static class ArchiveTransformer extends InformationResourceTransformer<PArchive> {
        // marker class
    }

    public static class AudioTransformer extends InformationResourceTransformer<PAudio> {
        // marker class
    }

    public static class OntologyTransformer extends InformationResourceTransformer<POntology> {
        // marker class
    }

    public static class ProjectTransformer extends DcTransformer<PProject> {
        // marker class
    }

    public static DublinCoreDocument transformAny(PResource resource) {
        ResourceType resourceType = resource.getResourceType();
        if (resourceType == null) {
            throw new TdarRecoverableRuntimeException("transformer.unsupported_type");
        }
        switch (resourceType) {
            case CODING_SHEET:
                return new CodingSheetTransformer().transform((PCodingSheet) resource);
            case DATASET:
                return new DatasetTransformer().transform((PDataset) resource);
            case DOCUMENT:
                return new DocumentTransformer().transform((PDocument) resource);
            case IMAGE:
                return new ImageTransformer().transform((PImage) resource);
            case ONTOLOGY:
                return new OntologyTransformer().transform((POntology) resource);
            case PROJECT:
                return new ProjectTransformer().transform((PProject) resource);
            case SENSORY_DATA:
                return new SensoryDataTransformer().transform((PSensoryData) resource);
            case VIDEO:
                return new VideoTransformer().transform((PVideo) resource);
            case GEOSPATIAL:
                return new GeospatialTransformer().transform((PGeospatial) resource);
            case ARCHIVE:
                return new ArchiveTransformer().transform((PArchive) resource);
            case AUDIO:
                return new AudioTransformer().transform((PAudio) resource);
            default:
                break;
        }

        throw new TdarRecoverableRuntimeException("transformer.no_dc_transformer", Arrays.asList(resource.getClass()));
    }

    public XmlEscapeHelper getX() {
        return x;
    }

    public void setX(XmlEscapeHelper x) {
        this.x = x;
    }
}
