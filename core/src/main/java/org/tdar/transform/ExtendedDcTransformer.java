package org.tdar.transform;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.tdar.core.bean.Persistable;
import org.tdar.core.serialize.collection.PResourceCollection;
import org.tdar.core.serialize.coverage.PCoverageDate;
import org.tdar.core.bean.coverage.CoverageType;
import org.tdar.core.serialize.coverage.PLatitudeLongitudeBox;
import org.tdar.core.bean.entity.Creator.CreatorType;
import org.tdar.core.serialize.entity.PInstitution;
import org.tdar.core.serialize.entity.PPerson;
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.bean.entity.ResourceCreatorRole;
import org.tdar.core.serialize.keyword.PCultureKeyword;
import org.tdar.core.serialize.keyword.PGeographicKeyword;
import org.tdar.core.serialize.keyword.PInvestigationType;
import org.tdar.core.serialize.keyword.PMaterialKeyword;
import org.tdar.core.serialize.keyword.POtherKeyword;
import org.tdar.core.serialize.keyword.PSiteNameKeyword;
import org.tdar.core.serialize.keyword.PSiteTypeKeyword;
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
import org.tdar.core.serialize.entity.PResourceCreator;
import org.tdar.core.serialize.resource.PResource;
import org.tdar.core.service.UrlService;
import org.tdar.utils.ResourceCitationFormatter;
import org.tdar.utils.XmlEscapeHelper;

import edu.asu.lib.qdc.QualifiedDublinCoreDocument;

public abstract class ExtendedDcTransformer<R extends PResource> implements Transformer<R, QualifiedDublinCoreDocument> {

    protected Set<String> contributors = new HashSet<>();
    protected Set<String> creators = new HashSet<>();
    private XmlEscapeHelper x;

    @Override
    public QualifiedDublinCoreDocument transform(R source) {
        QualifiedDublinCoreDocument dc = new QualifiedDublinCoreDocument();
        setX(new XmlEscapeHelper(source.getId()));
        dc.addTitle(getX().stripNonValidXMLCharacters(source.getTitle()));

        String abst = source.getDescription();
        if (abst != null) {
            dc.addAbstract(getX().stripNonValidXMLCharacters(abst));
        }

        dc.addCreated(source.getDateCreated());

        ResourceCitationFormatter rcf = new ResourceCitationFormatter(source);
        String cit = rcf.getFullCitation();
        if (StringUtils.isNotBlank(cit)) {
            dc.addBibliographicCitation(getX().stripNonValidXMLCharacters(cit));
        }

        // add creators and contributors
        for (PResourceCreator resourceCreator : toSortedList(source.getActiveResourceCreators())) {
            String name = resourceCreator.getCreator().getProperName();
            if (resourceCreator.getCreatorType() == CreatorType.PERSON) {
                // display person names in special format
                name = getX().stripNonValidXMLCharacters(dcConstructPersonalName(resourceCreator));
            }

            // FIXME: check this logic
            if (resourceCreator.getRole() == ResourceCreatorRole.AUTHOR || resourceCreator.getRole() == ResourceCreatorRole.CREATOR) {
                if (!creators.contains(name)) {
                    dc.addCreator(name);
                    creators.add(name);
                }
            } else {
                if (!contributors.contains(name)) {
                    dc.addContributor(name);
                    contributors.add(name);
                }
            }
        }

        // add geographic subjects
        for (PGeographicKeyword geoTerm : toSortedList(source.getActiveGeographicKeywords())) {
            dc.addSpatial(getX().stripNonValidXMLCharacters(geoTerm.getLabel()));
        }

        // add temporal subjects
        for (PTemporalKeyword temporalTerm : toSortedList(source.getActiveTemporalKeywords())) {
            dc.addTemporal(getX().stripNonValidXMLCharacters(temporalTerm.getLabel()));
        }

        // add culture subjects
        for (PCultureKeyword cultureTerm : toSortedList(source.getActiveCultureKeywords())) {
            dc.addSubject(getX().stripNonValidXMLCharacters(cultureTerm.getLabel()));
        }

        // add culture subjects
        for (PInvestigationType investigationType : toSortedList(source.getActiveInvestigationTypes())) {
            dc.addSubject(getX().stripNonValidXMLCharacters(investigationType.getLabel()));
        }

        // add site name subjects
        for (PSiteNameKeyword siteNameTerm : toSortedList(source.getActiveSiteNameKeywords())) {
            dc.addSubject(getX().stripNonValidXMLCharacters(siteNameTerm.getLabel()));
        }

        // add site name subjects
        for (PSiteTypeKeyword siteNameTerm : toSortedList(source.getActiveSiteTypeKeywords())) {
            dc.addSubject(getX().stripNonValidXMLCharacters(siteNameTerm.getLabel()));
        }

        // add site name subjects
        for (PMaterialKeyword term : toSortedList(source.getActiveMaterialKeywords())) {
            dc.addSubject(getX().stripNonValidXMLCharacters(term.getLabel()));
        }

        for (PCoverageDate cov : toSortedList(source.getActiveCoverageDates())) {
            if (cov.getDateType() == CoverageType.CALENDAR_DATE) {
                dc.addDate(getX().stripNonValidXMLCharacters(String.format("start:%s end:%s", cov.getStartDate(), cov.getEndDate())));
            } else {
                dc.addDate(getX().stripNonValidXMLCharacters(cov.toString()));
            }
        }

        for (PResourceCollection coll : toSortedList(source.getVisibleSharedResourceCollections())) {
            dc.addIsPartOf(getX().stripNonValidXMLCharacters(coll.getName()));
        }

        // add other subjects
        for (POtherKeyword otherTerm : toSortedList(source.getActiveOtherKeywords())) {
            dc.addSubject(getX().stripNonValidXMLCharacters(otherTerm.getLabel()));
        }

        dc.addType(getX().stripNonValidXMLCharacters(source.getResourceType().getLabel()));

        dc.addIdentifier(getX().stripNonValidXMLCharacters(source.getId().toString()));
        dc.addReferences(getX().stripNonValidXMLCharacters(UrlService.absoluteUrl(source)));
        for (PLatitudeLongitudeBox longLat : toSortedList(source.getActiveLatitudeLongitudeBoxes())) {
            String maxy = longLat.getNorth().toString();
            String miny = longLat.getSouth().toString();
            String maxx = longLat.getEast().toString();
            String minx = longLat.getWest().toString();
            dc.addSpatial(minx, miny, maxx, maxy);
            // dc.addCoverage(String.format("%s, %s, %s, %s", maxy, miny, maxx, minx));
        }

        for (PCoverageDate date : toSortedList(source.getCoverageDates())) {
            dc.addTemporal(getX().stripNonValidXMLCharacters(date.toString()));
        }

        getX().logChange();
        return dc;
    }

    private <K extends Persistable> List<K> toSortedList(Set<K> activeCultureKeywords) {
        List<K> list = new ArrayList<>(activeCultureKeywords);
        list.sort(new Comparator<K>() {

            @Override
            public int compare(K o1, K o2) {
                return ObjectUtils.compare(o1.getId(), o2.getId());
            }
        });
        // TODO Auto-generated method stub
        return list;
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
        // if (!StringUtils.isEmpty("" + resourceCreator.getRole())) {
        // name += String.format(", %s", resourceCreator.getRole());
        // }
        if (!StringUtils.isEmpty(person.getInstitutionName())) {
            name += String.format(" (%s)", person.getInstitution());
        }
        return name;
    }

    public static class InformationResourceTransformer<I extends PInformationResource> extends ExtendedDcTransformer<I> {

        @Override
        public QualifiedDublinCoreDocument transform(I source) {
            QualifiedDublinCoreDocument dc = super.transform(source);

            String doi = source.getDoi();
            if (StringUtils.isNotBlank(doi)) {
                dc.addIdentifier(getX().stripNonValidXMLCharacters(doi));
            }

            if (source.getProject() != PProject.NULL) {
                dc.addIsPartOf(getX().stripNonValidXMLCharacters(source.getProjectTitle()));
            }

            String copyLocation = source.getCopyLocation();
            if (copyLocation != null) {
                dc.addRelation(getX().stripNonValidXMLCharacters(copyLocation));
            }

            for (PResourceCreator resourceCreator : source.getActiveResourceCreators()) {
                if (resourceCreator.getRole() == ResourceCreatorRole.CONTACT) {
                    dc.addPublisher(getX().stripNonValidXMLCharacters(resourceCreator.getCreator().getProperName()));
                }
            }
            if (source.getDate() != null) {
                dc.addDate(getX().stripNonValidXMLCharacters(source.getDate().toString()));
            }

            Language resourceLanguage = source.getResourceLanguage();
            if (resourceLanguage != null) {
                dc.addLanguageISO639_2(getX().stripNonValidXMLCharacters(resourceLanguage.getIso639_2()));
            }

            if (source.getResourceType().toDcmiTypeString() != null) {
                dc.addType(getX().stripNonValidXMLCharacters(source.getResourceType().toDcmiTypeString()));
            }

            SortedSet<String> types = new TreeSet<>();
            for (PInformationResourceFileVersion version : source.getLatestUploadedVersions()) {
                types.add(version.getMimeType());
            }
            types.forEach(type -> dc.addType(getX().stripNonValidXMLCharacters(type)));

            PInstitution resourceProviderInstitution = source.getResourceProviderInstitution();
            if (resourceProviderInstitution != null) {
                String name = resourceProviderInstitution.getName();
                if (!contributors.contains(name)) {
                    dc.addContributor(getX().stripNonValidXMLCharacters(name));
                    contributors.add(name);
                }
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
                dc.addPublisher(getX().stripNonValidXMLCharacters(pub));
            }

            getX().logChange();
            return dc;
        }

    }

    public static class DocumentTransformer extends InformationResourceTransformer<PDocument> {

        @Override
        public QualifiedDublinCoreDocument transform(PDocument source) {
            QualifiedDublinCoreDocument dc = super.transform(source);

            String isbn = source.getIsbn();
            if (StringUtils.isNotBlank(isbn)) {
                dc.addIdentifier(getX().stripNonValidXMLCharacters(isbn));
            }
            String issn = source.getIssn();
            if (StringUtils.isNotBlank(issn)) {
                dc.addIdentifier(getX().stripNonValidXMLCharacters(issn));
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
                dc.addRelation(getX().stripNonValidXMLCharacters("Series: " + series));
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
            dc.addType(getX().stripNonValidXMLCharacters(documentType.getLabel()));
            switch (documentType) {
                case JOURNAL_ARTICLE:
                    if (!src.isEmpty()) {
                        dc.addSource(getX().stripNonValidXMLCharacters(src));
                    }
                    break;
                case BOOK_SECTION:
                    if (!src.isEmpty()) {
                        dc.addSource(getX().stripNonValidXMLCharacters(src));
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
                    dc.addRelation(getX().stripNonValidXMLCharacters(rel.trim()));
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

    public static class ProjectTransformer extends ExtendedDcTransformer<PProject> {
        // marker class
    }

    public static QualifiedDublinCoreDocument transformAny(PResource resource) {
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

        throw new TdarRecoverableRuntimeException("transformer.no_extended_dc_transformer", Arrays.asList(resource.getClass()));
    }

    public XmlEscapeHelper getX() {
        return x;
    }

    public void setX(XmlEscapeHelper x) {
        this.x = x;
    }
}
